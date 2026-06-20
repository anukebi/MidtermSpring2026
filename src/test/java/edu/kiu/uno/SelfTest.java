package edu.kiu.uno;

import java.util.ArrayList;
import java.util.List;

import edu.kiu.uno.model.CardMapper;
import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.model.card.CardRank;
import edu.kiu.uno.rule.BotLogic;
import edu.kiu.uno.rule.RulesValidator;

public class SelfTest {

	static void selfTest() {
		int passed = 0;

		if (CardMapper.getCard("R5").color().equals(CardColor.RED)) passed++;
		else fail("color R5");

		if (CardMapper.getCard("G+2").rank().equals(CardRank.DRAW)) passed++;
		else fail("rank +2");

		if (CardMapper.getCard("G+2").value().equals(2)) passed++;
		else fail("rank +2");

		if (CardMapper.getCard("W4").points() == 50) passed++;
		else fail("wild points");

		if (RulesValidator.isValid(CardMapper.getCard("R2"), CardMapper.getCard("R9"), null)) passed++;
		else fail("same color");

		if (RulesValidator.isValid(CardMapper.getCard("G9"), CardMapper.getCard("R9"), null)) passed++;
		else fail("same number");

		if (RulesValidator.isValid(CardMapper.getCard("B3"), CardMapper.getCard("W"), CardColor.BLUE)) passed++;
		else fail("called color");

		if (!RulesValidator.isValid(CardMapper.getCard("B3"), CardMapper.getCard("R9"), null)) passed++;
		else fail("illegal mismatch");

		List<Card> h = new ArrayList<>();
		h.add(CardMapper.getCard("B3"));
		h.add(CardMapper.getCard("R4"));
		h.add(CardMapper.getCard("W"));
		if (BotLogic.chooseCardIndex(h, CardMapper.getCard("R9"), null, 0) == 1) passed++;
		else fail("bot normal before wild");

		List<Card> h2 = new ArrayList<>();
		h2.add(CardMapper.getCard("B1"));
		h2.add(CardMapper.getCard("B2"));
		h2.add(CardMapper.getCard("R3"));
		if (BotLogic.chooseColor(h2).equals(CardColor.BLUE)) passed++;
		else fail("bot color");

		System.out.println("Passed " + passed + " characterization checks.");
	}

	static void fail(String name) {
		throw new RuntimeException("Failed: " + name);
	}

}
