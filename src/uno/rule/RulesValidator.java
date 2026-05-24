package uno.rule;

import uno.model.card.Card;
import uno.model.card.CardColor;
import uno.model.card.CardRank;

import java.util.Objects;
import java.util.Set;

public final class RulesValidator {

  private RulesValidator() {}

  public static boolean isValid(Card card, Card upCard, CardColor calledColor) {
    return isValid(card, calledColor != null ? calledColor : upCard.color(), upCard.rank(), upCard.value());
  }

  public static boolean isValid(Card card, CardColor calledColor, CardRank calledRank, Integer calledNumber) {
    if (Set.of(CardColor.WILD, calledColor).contains(card.color())) {
      return true;
    }

    if (card.rank().equals(calledRank) && !card.rank().equals(CardRank.NUMBER)) {
      return true;
    }

    return card.rank().equals(CardRank.NUMBER)
        && calledRank.equals(CardRank.NUMBER)
        && Objects.equals(card.value(), calledNumber);
  }

}
