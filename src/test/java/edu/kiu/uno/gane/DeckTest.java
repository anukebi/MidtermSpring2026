package edu.kiu.uno.gane;

import static edu.kiu.uno.TestUtils.check;

import java.util.Random;

import org.junit.jupiter.api.Test;

import edu.kiu.uno.game.Deck;

public class DeckTest {

  @Test
  void testDeckSize() {
		var deck = new Deck(new Random(0));
		deck.initializeDeck();
		check("standard deck has 108 cards", deck.size() == 108);
	}

  @Test
  void testDeckRecycle() {
		var deck = new Deck(new Random(7));
		deck.initializeDeck();
		var first = deck.draw();
		for (int i = 0; i < 107; i++) {
			deck.discard(deck.draw());
		}
		var afterRecycle = deck.draw();
		check("deck recycles discard pile", afterRecycle != null);
		deck.discard(first);
	}

  @Test
  void testDeckFallbackWild() {
		var deck = new Deck(new Random(1));
		check("empty deck returns wild fallback", deck.draw().code().equals("W"));
	}

}
