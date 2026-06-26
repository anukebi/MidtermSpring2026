package edu.kiu.uno.service.game;

import static edu.kiu.uno.GameTestFixtures.CARD_MAPPER;
import static edu.kiu.uno.TestUtils.check;

import java.util.Random;

import org.junit.jupiter.api.Test;

import edu.kiu.uno.GameTestFixtures;
import edu.kiu.uno.config.properties.GameProperties;

class ScoreCalculationServiceTest {

  @Test
  void roundWinnerReceivesOpponentCardPoints() {
    var state = GameTestFixtures.newGameState(new Random(42), 2, false);
    state.setCurrentPlayer(0);
    state.getPlayers().get(1).addCard(CARD_MAPPER.getCard("R5"));
    state.getPlayers().get(1).addCard(CARD_MAPPER.getCard("GS"));
    state.getPlayers().get(1).addCard(CARD_MAPPER.getCard("W"));
    int points = new ScoreCalculationService().updatePlayerScore(state);
    check("round score sums opponent cards", points == 75);
    check("winner total score updated", state.getCurrentPlayer().getTotalScore() == 75);
  }

  @Test
  void defaultTargetScoreIs500() {
    check("default target score is 500", new GameProperties().getTarget() == 500);
  }

}
