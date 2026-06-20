package edu.kiu.uno.rule;

import static edu.kiu.uno.TestUtils.check;

import org.junit.jupiter.api.Test;

import edu.kiu.uno.model.CardMapper;
import edu.kiu.uno.model.card.CardColor;

public class RulesValidatorTest {

	@Test
	void testIllegalMismatch() {
		check("no color number or rank match",
				!RulesValidator.isValid(CardMapper.getCard("B3"), CardMapper.getCard("R9"), null));
	}

	@Test
	void testRulesValidatorWithNullCalledColor() {
		var up = CardMapper.getCard("R9");
		check("null called uses up card color",
				RulesValidator.isValid(CardMapper.getCard("R2"), up, null));
		check("null called uses up card number",
				RulesValidator.isValid(CardMapper.getCard("G9"), up, null));
	}

	@Test
	void testRulesValidatorWithCalledColor() {
		check("null called uses up card color",
				RulesValidator.isValid(CardMapper.getCard("B3"), CardMapper.getCard("W"), CardColor.BLUE));
	}

	@Test
	void testDrawStacking() {
		var plusTwo = CardMapper.getCard("G+2");
		var wildFour = CardMapper.getCard("W4");
		check("+2 stacks on pending +2", RulesValidator.canStack(plusTwo, 2));
		check("W4 stacks on pending +4", RulesValidator.canStack(wildFour, 4));
		check("+2 does not stack on pending +4", !RulesValidator.canStack(plusTwo, 4));
	}

}
