package edu.kiu.uno.model.player;

import java.util.ArrayList;
import java.util.List;

import edu.kiu.uno.model.card.Card;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class Player {

  private final String name;
  private final PlayerType type;
  private final List<Card> hand = new ArrayList<>();
  private int score = 0;
  private int totalScore = 0;

  public void clearHand() {
    hand.clear();
    score = 0;
  }

  public void addCard(Card card) {
    hand.add(card);
  }

  public void addScore(int score) {
    this.score += score;
    this.totalScore += score;
  }

}
