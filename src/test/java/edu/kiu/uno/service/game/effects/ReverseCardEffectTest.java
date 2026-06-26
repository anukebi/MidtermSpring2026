package edu.kiu.uno.service.game.effects;

import static edu.kiu.uno.GameTestFixtures.CARD_MAPPER;
import static edu.kiu.uno.TestUtils.check;

import java.util.Random;

import org.junit.jupiter.api.Test;

import edu.kiu.uno.GameTestFixtures;
import edu.kiu.uno.service.game.GameDirection;
import edu.kiu.uno.service.game.GameState;

class ReverseCardEffectTest {

  @Test
  void reverseChangesDirectionForThreePlayers() {
    var state = GameTestFixtures.newGameState(new Random(42), 3, false);
    state.setCurrentPlayer(0);
    var player = state.getCurrentPlayer();
    new ReverseCardEffectStrategy().execute(state, CARD_MAPPER.getCard("YR"), player);
    check("reverse flips direction", state.getDirection() == GameDirection.BACKWARD);
    check("reverse moves to previous player", state.getCurrentPlayerIndex() == 2);
  }

  @Test
  void reverseActsLikeSkipForTwoPlayers() {
    var state = GameTestFixtures.newGameState(new Random(42), 1, true);
    state.setCurrentPlayer(0);
    var player = state.getCurrentPlayer();
    new ReverseCardEffectStrategy().execute(state, CARD_MAPPER.getCard("BR"), player);
    check("two-player reverse returns to same player", state.getCurrentPlayerIndex() == 0);
  }

}
