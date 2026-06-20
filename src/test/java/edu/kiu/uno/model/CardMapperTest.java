package edu.kiu.uno.model;

import static edu.kiu.uno.TestUtils.check;

import org.junit.jupiter.api.Test;

import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.model.card.CardRank;
import edu.kiu.uno.rule.RulesValidator;

public class CardMapperTest {

	@Test
	void testCardParsing() {
		var r5 = CardMapper.getCard("R5");
		check("color R5", r5.color() == CardColor.RED);
		check("rank +2", CardMapper.getCard("G+2").rank() == CardRank.DRAW);
		check("draw two value", CardMapper.getCard("G+2").value() == 2);
		check("wild points", CardMapper.getCard("W4").points() == 50);
		check("number card points", CardMapper.getCard("R7").points() == 7);
		check("skip points", CardMapper.getCard("BS").points() == 20);
		check("reverse code", CardMapper.getCard("BR").code().equals("BR"));
		check("wild code", CardMapper.getCard("W").code().equals("W"));
	}

	@Test
	void testWildDrawFourParsing() {
		var w4 = CardMapper.getCard("W4");
		check("W4 is wild color", w4.color() == CardColor.WILD);
		check("W4 is draw rank", w4.rank() == CardRank.DRAW);
		check("W4 draw value", w4.value() == 4);
		check("W4 code string", w4.code().equals("W4"));
		check("R4 stays a number card", CardMapper.getCard("R4").rank() == CardRank.NUMBER);
	}

	@Test
	void testMatchingByColor() {
		check("same color legal",
				RulesValidator.isValid(CardMapper.getCard("R2"), CardMapper.getCard("R9"), null));
		check("different color illegal",
				!RulesValidator.isValid(CardMapper.getCard("G2"), CardMapper.getCard("R9"), null));
	}

	@Test
	void testMatchingByNumber() {
		check("same number legal",
				RulesValidator.isValid(CardMapper.getCard("G9"), CardMapper.getCard("R9"), null));
		check("different number illegal",
				!RulesValidator.isValid(CardMapper.getCard("G8"), CardMapper.getCard("R9"), null));
	}

	@Test
	void testMatchingByActionType() {
		check("skip on skip",
				RulesValidator.isValid(CardMapper.getCard("YS"), CardMapper.getCard("RS"), null));
		check("draw two on draw two",
				RulesValidator.isValid(CardMapper.getCard("B+2"), CardMapper.getCard("G+2"), null));
		check("reverse on reverse",
				RulesValidator.isValid(CardMapper.getCard("YR"), CardMapper.getCard("BR"), null));
	}

	@Test
	void testWildCards() {
		check("wild always legal",
				RulesValidator.isValid(CardMapper.getCard("W"), CardMapper.getCard("R9"), null));
		check("wild draw four always legal",
				RulesValidator.isValid(CardMapper.getCard("W4"), CardMapper.getCard("G+2"), null));
		check("plain wild rank", CardMapper.getCard("W").rank() == CardRank.CHANGE);
	}

	@Test
	void testCalledColorAfterWild() {
		check("called color match",
				RulesValidator.isValid(CardMapper.getCard("B3"), CardMapper.getCard("W"), CardColor.BLUE));
		check("wrong color after call",
				!RulesValidator.isValid(CardMapper.getCard("R3"), CardMapper.getCard("W"), CardColor.BLUE));
	}

	@Test
	void testScoring() {
		int total = CardMapper.getCard("R5").points()
				+ CardMapper.getCard("B9").points()
				+ CardMapper.getCard("GS").points()
				+ CardMapper.getCard("W").points();
		check("example win score from rules", total == 84);
	}

}
