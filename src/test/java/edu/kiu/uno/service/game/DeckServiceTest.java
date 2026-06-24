package edu.kiu.uno.service.game;

import static edu.kiu.uno.TestUtils.check;

import java.util.Random;

import edu.kiu.uno.config.properties.DeckProperties;
import org.junit.jupiter.api.Test;

import edu.kiu.uno.util.CardMapper;

public class DeckServiceTest {

  @Test
  void testDeckRecycle() {
		var deck = new DeckService(new CardMapper(), new DeckProperties(), new Random(7));
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
		var deck = new DeckService(new CardMapper(), new DeckProperties(), new Random(1));
		check("empty deck returns wild fallback", deck.draw().code().equals("W"));
	}

}
