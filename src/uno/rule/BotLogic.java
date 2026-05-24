package uno.rule;

import uno.model.card.Card;
import uno.model.card.CardColor;
import uno.model.card.CardRank;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class BotLogic {

  private BotLogic() {}

  public static int chooseCardIndex(List<Card> hand, Card upCard, CardColor calledColor) {
    int drawTwo = findFirstLegal(hand, upCard, calledColor, CardRank.DRAW);
    if (drawTwo >= 0) {
      return drawTwo;
    }
    int skip = findFirstLegal(hand, upCard, calledColor, CardRank.SKIP);
    if (skip >= 0) {
      return skip;
    }
    int number = findFirstLegal(hand, upCard, calledColor, CardRank.NUMBER);
    if (number >= 0) {
      return number;
    }
    for (int i = 0; i < hand.size(); i++) {
      if (hand.get(i).color() == CardColor.WILD) {
        return i;
      }
    }
    return -1;
  }

  private static int findFirstLegal(List<Card> hand, Card upCard, CardColor calledColor, CardRank targetRank) {
    for (int i = 0; i < hand.size(); i++) {
      var card = hand.get(i);
      if (card.rank().equals(targetRank)
          && card.color() != CardColor.WILD
          && RulesValidator.isValid(card, upCard, calledColor)) {
        return i;
      }
    }
    return -1;
  }

  public static CardColor chooseColor(List<Card> hand) {
    EnumMap<CardColor, Integer> colorCounts = new EnumMap<>(CardColor.class);

    for (var card : hand) {
      colorCounts.put(card.color(), colorCounts.getOrDefault(card.color(), 0) + 1);
    }

    return colorCounts.entrySet()
        .stream()
        .filter(e -> e.getKey() != CardColor.WILD)
        .max(Comparator.comparingInt(Map.Entry::getValue))
        .map(Map.Entry::getKey)
        .orElseGet(() -> colorCounts.entrySet().iterator().next().getKey());
  }
}
