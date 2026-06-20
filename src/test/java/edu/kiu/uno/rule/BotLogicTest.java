package edu.kiu.uno.rule;

import static edu.kiu.uno.TestUtils.check;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.kiu.uno.model.CardMapper;
import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardColor;

public class BotLogicTest {

  @Test
  void testBotLogicPriorities() {
		List<Card> hand = List.of(
				CardMapper.getCard("B3"),
				CardMapper.getCard("R4"),
				CardMapper.getCard("W"));
		check("bot prefers matching number over wild",
				BotLogic.chooseCardIndex(hand, CardMapper.getCard("R9"), null, 0) == 1);

		List<Card> hand2 = List.of(
				CardMapper.getCard("R1"),
				CardMapper.getCard("R+2"),
				CardMapper.getCard("R5"));
		check("bot prefers draw two when legal",
				BotLogic.chooseCardIndex(hand2, CardMapper.getCard("G+2"), null, 0) == 1);

		List<Card> hand3 = List.of(
				CardMapper.getCard("B1"),
				CardMapper.getCard("YS"),
				CardMapper.getCard("R5"));
		check("bot prefers skip over number",
				BotLogic.chooseCardIndex(hand3, CardMapper.getCard("RS"), null, 0) == 1);
	}

  @Test
  void testDrawStacking() {
		List<Card> hand = List.of(CardMapper.getCard("R+2"), CardMapper.getCard("R5"));
		check("bot stacks +2 when pending +2",
				BotLogic.chooseCardIndex(hand, CardMapper.getCard("G+2"), null, 2) == 0);
		check("bot draws when no stack card",
				BotLogic.chooseCardIndex(List.of(CardMapper.getCard("R5")), CardMapper.getCard("G+2"), null, 2) == -1);
	}

  @Test
  void testBotColorChoice() {
		List<Card> hand = List.of(
				CardMapper.getCard("B1"),
				CardMapper.getCard("B2"),
				CardMapper.getCard("R3"));
		check("bot picks majority color", BotLogic.chooseColor(hand) == CardColor.BLUE);
	}

}
