package uno;

public record Card(String code) {

  public boolean isWild() {
    return code.startsWith("W");
  }

  public String color() {
    if (code.startsWith("R")) {
      return "R";
    }
    if (code.startsWith("Y")) {
      return "Y";
    }
    if (code.startsWith("G")) {
      return "G";
    }
    if (code.startsWith("B")) {
      return "B";
    }
    return "";
  }

  public String rank() {
    if (code.equals("W")) {
      return "WILD";
    }
    if (code.equals("W4")) {
      return "WILD_DRAW_FOUR";
    }
    if (code.endsWith("S")) {
      return "SKIP";
    }
    if (code.endsWith("R")) {
      return "REVERSE";
    }
    if (code.endsWith("+2")) {
      return "DRAW_TWO";
    }
    return "NUMBER";
  }

  public int number() {
    if (rank().equals("NUMBER")) {
      return Integer.parseInt(code.substring(1));
    }
    return -1;
  }

  public int points() {
    String r = rank();
    if (r.equals("NUMBER")) {
      return number();
    }
    if (r.equals("SKIP") || r.equals("REVERSE") || r.equals("DRAW_TWO")) {
      return 20;
    }
    if (r.equals("WILD") || r.equals("WILD_DRAW_FOUR")) {
      return 50;
    }
    return 0;
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
