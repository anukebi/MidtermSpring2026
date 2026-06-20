package edu.kiu.uno.service.game;

import static edu.kiu.uno.TestUtils.check;

import java.util.Random;

import org.junit.jupiter.api.Test;

import edu.kiu.uno.service.game.Deck;
import edu.kiu.uno.util.CardMapper;

public class DeckTest {

  @Test
  void testDeckSize() {
		var deck = new Deck(new Random(0), new CardMapper());
		deck.initializeDeck();
		check("standard deck has 108 cards", deck.size() == 108);
	}

  @Test
  void testDeckRecycle() {
		var deck = new Deck(new Random(7), new CardMapper());
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
		var deck = new Deck(new Random(1), new CardMapper());
		check("empty deck returns wild fallback", deck.draw().code().equals("W"));
	}

}
