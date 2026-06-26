package edu.kiu.uno.service.game.effects;

import static edu.kiu.uno.GameTestFixtures.CARD_MAPPER;
import static edu.kiu.uno.TestUtils.check;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kiu.uno.GameTestFixtures;
import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.service.controller.input.BotInputService;
import edu.kiu.uno.service.controller.input.PlayerInputServiceProvider;
import edu.kiu.uno.service.controller.output.PlayerOutputService;
import edu.kiu.uno.service.game.GameState;

class DrawCardEffectTest {

  private PlayerOutputService outputService;
  private PlayerInputServiceProvider inputProvider;
  private DrawCardEffectStrategy drawEffect;

  @BeforeEach
  void setUp() {
    outputService = mock(PlayerOutputService.class);
    inputProvider = new PlayerInputServiceProvider(List.of(new BotInputService(GameTestFixtures.RULES_VALIDATOR)));
    drawEffect = new DrawCardEffectStrategy(outputService, inputProvider);
  }

  @Test
  void drawTwoCreatesPendingDraw() {
    var state = GameTestFixtures.newGameState(new Random(42), 2, false);
    state.setCurrentPlayer(0);
    drawEffect.execute(state, CARD_MAPPER.getCard("G+2"), state.getCurrentPlayer());
    check("draw two sets pending draw of 2", state.getPendingDraw() == 2);
    check("draw two sets pending amount of 2", state.getPendingDrawAmount() == 2);
  }

  @Test
  void wildDrawFourCreatesPendingDrawOfFour() {
    var state = GameTestFixtures.newGameState(new Random(42), 2, false);
    state.setCurrentPlayer(0);
    var player = state.getCurrentPlayer();
    player.addCard(CARD_MAPPER.getCard("R3"));
    drawEffect.execute(state, CARD_MAPPER.getCard("W4"), player);
    check("wild draw four sets pending draw of 4", state.getPendingDraw() == 4);
    check("wild draw four sets pending amount of 4", state.getPendingDrawAmount() == 4);
    check("wild draw four sets called color", state.getCalledColor() == CardColor.RED);
  }

}
