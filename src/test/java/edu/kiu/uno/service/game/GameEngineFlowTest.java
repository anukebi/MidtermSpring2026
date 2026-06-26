package edu.kiu.uno.service.game;

import static edu.kiu.uno.GameTestFixtures.CARD_MAPPER;
import static edu.kiu.uno.TestUtils.check;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import edu.kiu.uno.GameTestFixtures;
import edu.kiu.uno.config.properties.GameProperties;
import edu.kiu.uno.service.controller.input.PlayerInputServiceProvider;
import edu.kiu.uno.service.controller.input.ScriptedInputService;
import edu.kiu.uno.service.controller.output.PlayerOutputService;
import edu.kiu.uno.service.game.GamePersistenceOrchestrator.PersistenceContext;

class GameEngineFlowTest {

  @Test
  void roundEndsWhenPlayerPlaysLastCard() {
    var random = new Random(42);
    var properties = new GameProperties();
    properties.setGames(1);
    properties.setTurnSafetyLimit(50);

    var state = GameTestFixtures.newGameState(random, 2, false);
    var scripted = new ScriptedInputService();
    scripted.chooseCard(0);

    var engine = engineWithScriptedRound(
        random, properties, state, scripted,
        roundState -> {
          roundState.getPlayers().get(0).addCard(CARD_MAPPER.getCard("R5"));
          roundState.getPlayers().get(1).addCard(CARD_MAPPER.getCard("R7"));
          roundState.getPlayers().get(1).addCard(CARD_MAPPER.getCard("GS"));
          roundState.setUpCard(CARD_MAPPER.getCard("R3"));
          roundState.setCurrentPlayer(0);
        });

    engine.playRound(new PersistenceContext(), 1);

    check("round winner receives opponent card points", state.getPlayers().getFirst().getTotalScore() == 27);
    check("winner has empty hand", state.getPlayers().getFirst().cardCount() == 0);
  }

  @Test
  void drawTwoForcesNextPlayerToDrawAndSkipTurn() {
    var random = new Random(42);
    var properties = new GameProperties();
    properties.setTurnSafetyLimit(2);

    var state = GameTestFixtures.newGameState(random, 2, false);
    var scripted = new ScriptedInputService();
    scripted.chooseCard(0);
    scripted.chooseDraw();

    var engine = engineWithScriptedRound(
        random, properties, state, scripted,
        roundState -> {
          roundState.getPlayers().get(0).addCard(CARD_MAPPER.getCard("G+2"));
          roundState.getPlayers().get(0).addCard(CARD_MAPPER.getCard("G5"));
          roundState.getPlayers().get(1).addCard(CARD_MAPPER.getCard("R5"));
          roundState.setUpCard(CARD_MAPPER.getCard("G3"));
          roundState.setCurrentPlayer(0);
        });

    engine.playRound(new PersistenceContext(), 1);

    check("draw two adds two cards to next player", state.getPlayers().get(1).cardCount() == 3);
    check("pending draw cleared after taking cards", state.getPendingDraw() == 0);
  }

  @Test
  void missedUnoAppliesTwoCardPenaltyDuringRound() {
    var random = new Random(42);
    var properties = new GameProperties();
    properties.setTurnSafetyLimit(1);

    var state = GameTestFixtures.newGameState(random, 1, true);
    var scripted = new ScriptedInputService();
    scripted.chooseCard(0);
    scripted.confirmUno(false);

    var engine = engineWithScriptedRound(
        random, properties, state, scripted,
        roundState -> {
          roundState.getPlayers().get(0).addCard(CARD_MAPPER.getCard("R5"));
          roundState.getPlayers().get(0).addCard(CARD_MAPPER.getCard("R7"));
          roundState.getPlayers().get(1).addCard(CARD_MAPPER.getCard("B3"));
          roundState.setUpCard(CARD_MAPPER.getCard("R3"));
          roundState.setCurrentPlayer(0);
        });

    engine.playRound(new PersistenceContext(), 1);

    check("missed UNO leaves player with penalty cards", state.getPlayers().getFirst().cardCount() == 3);
  }

  @Test
  void matchStopsWhenTargetScoreIsReached() {
    var random = new Random(42);
    var properties = new GameProperties();
    properties.setGames(5);
    properties.setTarget(27);
    properties.setTurnSafetyLimit(50);

    var state = GameTestFixtures.newGameState(random, 2, false);
    var scripted = new ScriptedInputService();
    scripted.chooseCard(0);

    var engine = engineWithScriptedRound(
        random, properties, state, scripted,
        roundState -> {
          roundState.getPlayers().get(0).addCard(CARD_MAPPER.getCard("R5"));
          roundState.getPlayers().get(1).addCard(CARD_MAPPER.getCard("R7"));
          roundState.getPlayers().get(1).addCard(CARD_MAPPER.getCard("GS"));
          roundState.setUpCard(CARD_MAPPER.getCard("R3"));
          roundState.setCurrentPlayer(0);
        });

    engine.playGame();

    check("winner reached target score", state.getPlayers().getFirst().getTotalScore() >= 27);
    check("round score recorded for winner", state.getPlayers().getFirst().getScore() == 27);
  }

  private GameEngine engineWithScriptedRound(Random random, GameProperties properties, GameState state,
                                             ScriptedInputService scripted, Consumer<GameState> roundSetup) {
    var inputProvider = new PlayerInputServiceProvider(List.of(scripted));
    var output = mock(PlayerOutputService.class);
    var deckService = spy(GameTestFixtures.newDeckService(random));

    doAnswer(invocation -> {
      roundSetup.accept(invocation.getArgument(0));
      return null;
    }).when(deckService).distribute(any());
    doCallRealMethod().when(deckService).initializeDeck();
    doCallRealMethod().when(deckService).draw();
    doCallRealMethod().when(deckService).discard(any());

    var persistence = mock(GamePersistenceOrchestrator.class);
    return GameTestFixtures.newGameEngine(properties, state, deckService, output, inputProvider, persistence);
  }

}
