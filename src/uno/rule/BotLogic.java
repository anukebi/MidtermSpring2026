package uno.rule;

import uno.model.card.Card;
import uno.model.card.CardColor;
import uno.model.card.CardRank;

import java.util.List;

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
    int r = 0;
    int y = 0;
    int g = 0;
    int b = 0;

    for (var card : hand) {
      switch (card.color()) {
        case RED -> r++;
        case YELLOW -> y++;
        case GREEN -> g++;
        case BLUE -> b++;
      }
    }

    if (r >= y && r >= g && r >= b) {
      return CardColor.RED;
    } else if (y >= r && y >= g && y >= b) {
      return CardColor.YELLOW;
    } else if (g >= r && g >= y && g >= b) {
      return CardColor.GREEN;
    } else {
      return CardColor.BLUE;
    }
  }
}
