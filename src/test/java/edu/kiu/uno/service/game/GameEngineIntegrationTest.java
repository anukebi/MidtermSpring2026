package edu.kiu.uno.service.game;

import static edu.kiu.uno.TestUtils.check;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import edu.kiu.uno.CoreTest;
import edu.kiu.uno.config.properties.GameProperties;

class GameEngineIntegrationTest extends CoreTest {

  @Autowired
  private GameEngine gameEngine;

  @Autowired
  private GameState gameState;

  @Autowired
  private GameProperties gameProperties;

  @Test
  void springContextWiresGameEngine() {
    check("game engine is available", gameEngine != null);
    check("players configured", gameState.playerCount() >= 2);
  }

  @Test
  void fullBotMatchCompletesWithScoring() {
    gameEngine.playGame();

    check("at least one player scored points", gameState.getPlayers().stream().anyMatch(player -> player.getTotalScore() > 0));
    check("target score configured", gameProperties.getTarget() == 500);
  }

}
