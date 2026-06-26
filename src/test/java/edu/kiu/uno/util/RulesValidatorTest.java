package edu.kiu.uno.util;

import static edu.kiu.uno.TestUtils.check;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kiu.uno.model.card.CardColor;

public class RulesValidatorTest {
	
	private CardMapper cardMapper;
	private RulesValidator rulesValidator;
	
	@BeforeEach
	void setUp() {
		cardMapper = new CardMapper();
		rulesValidator = new RulesValidator();
	}

	@Test
	void testIllegalMismatch() {
		check("no color number or rank match",
				!rulesValidator.isValid(cardMapper.getCard("B3"), cardMapper.getCard("R9"), null));
	}

	@Test
	void testRulesValidatorWithNullCalledColor() {
		var up = cardMapper.getCard("R9");
		check("null called uses up card color",
				rulesValidator.isValid(cardMapper.getCard("R2"), up, null));
		check("null called uses up card number",
				rulesValidator.isValid(cardMapper.getCard("G9"), up, null));
	}

	@Test
	void testRulesValidatorWithCalledColor() {
		check("null called uses up card color",
				rulesValidator.isValid(cardMapper.getCard("B3"), cardMapper.getCard("W"), CardColor.BLUE));
	}

	@Test
	void testDrawStacking() {
		var plusTwo = cardMapper.getCard("G+2");
		var wildFour = cardMapper.getCard("W4");
		check("+2 stacks on pending +2", rulesValidator.canStack(plusTwo, 2));
		check("W4 stacks on pending +4", rulesValidator.canStack(wildFour, 4));
		check("+2 does not stack on pending +4", !rulesValidator.canStack(plusTwo, 4));
	}

	@Test
	void drawnCardCanBePlayedWhenLegal() {
		check("drawn card legal by color",
				rulesValidator.isValid(cardMapper.getCard("R9"), cardMapper.getCard("R5"), null));
	}

}
