package edu.kiu.uno.model.card;

import java.util.regex.Pattern;

public enum CardRank {

  NUMBER("[0-9]+"),
  SKIP("S"),
  REVERSE("R"),
  DRAW("(\\+2|4)"),
  CHANGE("^$");

  private final Pattern codePattern;

  CardRank(String codePattern) {
    this.codePattern = Pattern.compile(codePattern);
  }

  public String getCode() {
    return switch (this) {
      case NUMBER, DRAW -> null;
      case CHANGE -> "";
      default -> codePattern.pattern();
    };
  }

  public static CardRank fromCode(String rankCode, CardColor color) {
    for (CardRank rank : values()) {
      if (rank.codePattern.matcher(rankCode).matches()) {
        if (color == CardColor.WILD && rank == NUMBER) {
          return DRAW;
        }
        return rank;
      }
    }
    throw new IllegalArgumentException("Invalid card rank code: " + rankCode);
  }

}
