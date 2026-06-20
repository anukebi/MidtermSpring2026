package edu.kiu.uno.util;

import static edu.kiu.uno.TestUtils.check;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.model.card.CardRank;

public class CardMapperTest {
	
	private CardMapper cardMapper;
	private RulesValidator rulesValidator;

	@BeforeEach
	void setUp() {
		cardMapper = new CardMapper();
		rulesValidator = new RulesValidator();
	}

	@Test
	void testCardParsing() {
		var r5 = cardMapper.getCard("R5");
		check("color R5", r5.color() == CardColor.RED);
		check("rank +2", cardMapper.getCard("G+2").rank() == CardRank.DRAW);
		check("draw two value", cardMapper.getCard("G+2").value() == 2);
		check("wild points", cardMapper.getCard("W4").points() == 50);
		check("number card points", cardMapper.getCard("R7").points() == 7);
		check("skip points", cardMapper.getCard("BS").points() == 20);
		check("reverse code", cardMapper.getCard("BR").code().equals("BR"));
		check("wild code", cardMapper.getCard("W").code().equals("W"));
	}

	@Test
	void testWildDrawFourParsing() {
		var w4 = cardMapper.getCard("W4");
		check("W4 is wild color", w4.color() == CardColor.WILD);
		check("W4 is draw rank", w4.rank() == CardRank.DRAW);
		check("W4 draw value", w4.value() == 4);
		check("W4 code string", w4.code().equals("W4"));
		check("R4 stays a number card", cardMapper.getCard("R4").rank() == CardRank.NUMBER);
	}

	@Test
	void testMatchingByColor() {
		check("same color legal",
				rulesValidator.isValid(cardMapper.getCard("R2"), cardMapper.getCard("R9"), null));
		check("different color illegal",
				!rulesValidator.isValid(cardMapper.getCard("G2"), cardMapper.getCard("R9"), null));
	}

	@Test
	void testMatchingByNumber() {
		check("same number legal",
				rulesValidator.isValid(cardMapper.getCard("G9"), cardMapper.getCard("R9"), null));
		check("different number illegal",
				!rulesValidator.isValid(cardMapper.getCard("G8"), cardMapper.getCard("R9"), null));
	}

	@Test
	void testMatchingByActionType() {
		check("skip on skip",
				rulesValidator.isValid(cardMapper.getCard("YS"), cardMapper.getCard("RS"), null));
		check("draw two on draw two",
				rulesValidator.isValid(cardMapper.getCard("B+2"), cardMapper.getCard("G+2"), null));
		check("reverse on reverse",
				rulesValidator.isValid(cardMapper.getCard("YR"), cardMapper.getCard("BR"), null));
	}

	@Test
	void testWildCards() {
		check("wild always legal",
				rulesValidator.isValid(cardMapper.getCard("W"), cardMapper.getCard("R9"), null));
		check("wild draw four always legal",
				rulesValidator.isValid(cardMapper.getCard("W4"), cardMapper.getCard("G+2"), null));
		check("plain wild rank", cardMapper.getCard("W").rank() == CardRank.CHANGE);
	}

	@Test
	void testCalledColorAfterWild() {
		check("called color match",
				rulesValidator.isValid(cardMapper.getCard("B3"), cardMapper.getCard("W"), CardColor.BLUE));
		check("wrong color after call",
				!rulesValidator.isValid(cardMapper.getCard("R3"), cardMapper.getCard("W"), CardColor.BLUE));
	}

	@Test
	void testScoring() {
		int total = cardMapper.getCard("R5").points()
				+ cardMapper.getCard("B9").points()
				+ cardMapper.getCard("GS").points()
				+ cardMapper.getCard("W").points();
		check("example win score from rules", total == 84);
	}

}
