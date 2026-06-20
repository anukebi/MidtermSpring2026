package edu.kiu.uno.model.card;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CardColor {

  RED("R"),
  GREEN("G"),
  BLUE("B"),
  YELLOW("Y"),
  WILD("W");

  private final String code;

  public static CardColor fromCode(String code) {
    for (CardColor color : values()) {
      if (color.code.equals(code)) {
        return color;
      }
    }
    throw new IllegalArgumentException("Invalid card code: " + code);
  }

  @Override
  public String toString() {
    return code;
  }

}
