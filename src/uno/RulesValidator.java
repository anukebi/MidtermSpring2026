package uno;

public final class RulesValidator {

  private RulesValidator() {}

  public static boolean isValid(Card card, Card upCard, String calledColor) {
    if (card.isWild()) {
      return true;
    }

    if (card.color().equals(upCard.color())) {
      return true;
    }

    if (!calledColor.isBlank() && card.color().equals(calledColor)) {
      return true;
    }

    if (card.rank().equals(upCard.rank()) && !card.rank().equals("NUMBER")) {
      return true;
    }

    return card.rank().equals("NUMBER")
        && upCard.rank().equals("NUMBER")
        && card.number() == upCard.number();
  }

  public static boolean isValid(String cardCode, String upCode, String calledColor) {
    return isValid(new Card(cardCode), new Card(upCode), calledColor);
  }

}
