package edu.kiu.uno.util;

import org.springframework.stereotype.Component;

import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.model.card.CardRank;

@Component
public class CardMapper {

  public Card getCard(CardColor color, CardRank rank, Integer value) {
    var code = switch (rank) {
      case NUMBER -> color.getCode() + value.toString();
      case DRAW -> color.getCode() + (color == CardColor.WILD ? "" : "+") + value;
      default -> color.getCode() + rank.getCode();
    };
    return new Card(code, color, rank, value);
  }

  public Card getCard(String code) {
    var cardColor = getCardColor(code);
    var cardRank = getCardRank(code, cardColor);
    var cardValue = getCardValue(code, cardColor, cardRank);
    return new Card(code, cardColor, cardRank, cardValue);
  }

  private CardColor getCardColor(String code) {
    var colorCode = code.substring(0, 1);
    return CardColor.fromCode(colorCode);
  }

  private CardRank getCardRank(String code, CardColor cardColor) {
    var rankCode = code.substring(1);
    return CardRank.fromCode(rankCode, cardColor);
  }

  private Integer getCardValue(String code, CardColor color, CardRank rank) {
    return switch (rank) {
      case NUMBER -> Integer.parseInt(code.substring(1));
      case DRAW -> Integer.parseInt(code.substring(color == CardColor.WILD ? 1 : 2)); // Wildcards currently do not support number ranks, so instead of +4 code, it contains 4 directly
      default -> null;
    };
  }

}
