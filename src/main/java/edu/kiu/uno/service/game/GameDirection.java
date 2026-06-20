package edu.kiu.uno.service.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum GameDirection {

  FORWARD(1),
  BACKWARD(-1);

  private final int value;

  public GameDirection reverse() {
    return this == FORWARD ? BACKWARD : FORWARD;
  }

}
