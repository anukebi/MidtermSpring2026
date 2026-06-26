package edu.kiu.uno.service.game;

import static edu.kiu.uno.TestUtils.check;

import java.util.Random;

import org.junit.jupiter.api.Test;

import edu.kiu.uno.GameTestFixtures;

class UnoPenaltyTest {

  @Test
  void missedUnoPenaltyAddsTwoCards() {
    var deckService = GameTestFixtures.newDeckService(new Random(42));
    deckService.initializeDeck();
    var state = GameTestFixtures.newGameState(new Random(42), 1, true);
    var player = state.getPlayers().getFirst();
    int before = player.cardCount();
    deckService.draw(player);
    deckService.draw(player);
    check("missed UNO penalty is two cards", player.cardCount() == before + 2);
  }

}
