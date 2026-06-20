package uno.game;

public enum GameDirection {

  FORWARD(1),
  BACKWARD(-1);

  private final int value;

  GameDirection(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public GameDirection reverse() {
    return this == FORWARD ? BACKWARD : FORWARD;
  }

}
