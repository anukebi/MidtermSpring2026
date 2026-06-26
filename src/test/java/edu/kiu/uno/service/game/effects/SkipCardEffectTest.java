package edu.kiu.uno.service.game.effects;

import static edu.kiu.uno.GameTestFixtures.CARD_MAPPER;
import static edu.kiu.uno.TestUtils.check;

import java.util.Random;

import org.junit.jupiter.api.Test;

import edu.kiu.uno.GameTestFixtures;
import edu.kiu.uno.service.game.GameState;

class SkipCardEffectTest {

  @Test
  void skipSkipsNextPlayerInThreePlayerGame() {
    var state = GameTestFixtures.newGameState(new Random(42), 3, false);
    state.setCurrentPlayer(0);
    var player = state.getCurrentPlayer();
    new SkipCardEffectStrategy().execute(state, CARD_MAPPER.getCard("RS"), player);
    check("skip skips the next player", state.getCurrentPlayerIndex() == 2);
  }

}
