package edu.kiu.uno.service.game.effects;

import static edu.kiu.uno.GameTestFixtures.CARD_MAPPER;
import static edu.kiu.uno.GameTestFixtures.RULES_VALIDATOR;
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

class WildCardEffectTest {

  private PlayerOutputService outputService;
  private PlayerInputServiceProvider inputProvider;

  @BeforeEach
  void setUp() {
    outputService = mock(PlayerOutputService.class);
    inputProvider = new PlayerInputServiceProvider(List.of(new BotInputService(RULES_VALIDATOR)));
  }

  @Test
  void wildSetsCalledColor() {
    var state = GameTestFixtures.newGameState(new Random(42), 2, false);
    state.setCurrentPlayer(0);
    var player = state.getCurrentPlayer();
    player.addCard(CARD_MAPPER.getCard("B3"));
    player.addCard(CARD_MAPPER.getCard("B5"));
    new ChangeCardEffectStrategy(outputService, inputProvider).execute(state, CARD_MAPPER.getCard("W"), player);
    check("wild sets called color", state.getCalledColor() == CardColor.BLUE);
    check("called color affects legality", RULES_VALIDATOR.isValid(CARD_MAPPER.getCard("B3"), CARD_MAPPER.getCard("W"), state.getCalledColor()));
  }

}
