package edu.kiu.uno.model.card;

import java.util.Objects;
import java.util.Optional;

public record Card(String code, CardColor color, CardRank rank, Integer value) {

  public int points() {
    if (Objects.equals(color, CardColor.WILD)) {
      return 50;
    }
    return switch (rank) {
      case NUMBER -> Optional.ofNullable(value).orElse(-1);
      case SKIP, REVERSE, DRAW -> 20;
      default -> 0;
    };
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Card)) {
      return false;
    }
    return code.equals(((Card) other).code);
  }

  @Override
  public String toString() {
    return code;
  }

}
