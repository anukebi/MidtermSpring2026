package edu.kiu.uno.util;

import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Component;

import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.model.card.CardRank;

@Component
public class RulesValidator {

  public boolean isValid(Card card, Card upCard, CardColor calledColor) {
    return isValid(card, calledColor != null ? calledColor : upCard.color(), upCard.rank(), upCard.value());
  }

  public boolean canStack(Card card, int pendingDrawAmount) {
    return card.rank() == CardRank.DRAW
        && card.value() != null
        && card.value() == pendingDrawAmount;
  }

  public boolean isValid(Card card, CardColor calledColor, CardRank calledRank, Integer calledNumber) {
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
