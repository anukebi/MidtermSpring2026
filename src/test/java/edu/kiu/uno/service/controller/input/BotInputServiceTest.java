package edu.kiu.uno.service.controller.input;

import static edu.kiu.uno.TestUtils.check;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.util.CardMapper;
import edu.kiu.uno.util.RulesValidator;

public class BotInputServiceTest {
	
	private BotInputService botInputService;
	private CardMapper cardMapper;

	@BeforeEach
	void setup() {
		botInputService = new BotInputService(new RulesValidator());
		cardMapper = new CardMapper();
	}

  @Test
  void testBotLogicPriorities() {
		List<Card> hand = List.of(
				cardMapper.getCard("B3"),
				cardMapper.getCard("R4"),
				cardMapper.getCard("W"));
		check("bot prefers matching number over wild",
				botInputService.getCardChoice(hand, cardMapper.getCard("R9"), null, 0) == 1);

		List<Card> hand2 = List.of(
				cardMapper.getCard("R1"),
				cardMapper.getCard("R+2"),
				cardMapper.getCard("R5"));
		check("bot prefers draw two when legal",
				botInputService.getCardChoice(hand2, cardMapper.getCard("G+2"), null, 0) == 1);

		List<Card> hand3 = List.of(
				cardMapper.getCard("B1"),
				cardMapper.getCard("YS"),
				cardMapper.getCard("R5"));
		check("bot prefers skip over number",
				botInputService.getCardChoice(hand3, cardMapper.getCard("RS"), null, 0) == 1);
	}

  @Test
  void testDrawStacking() {
		List<Card> hand = List.of(cardMapper.getCard("R+2"), cardMapper.getCard("R5"));
		check("bot stacks +2 when pending +2",
				botInputService.getCardChoice(hand, cardMapper.getCard("G+2"), null, 2) == 0);
		check("bot draws when no stack card",
				botInputService.getCardChoice(List.of(cardMapper.getCard("R5")), cardMapper.getCard("G+2"), null, 2) == -1);
	}

  @Test
  void testBotColorChoice() {
		List<Card> hand = List.of(
				cardMapper.getCard("B1"),
				cardMapper.getCard("B2"),
				cardMapper.getCard("R3"));
		check("bot picks majority color", botInputService.getCardColor(hand) == CardColor.BLUE);
	}

}
