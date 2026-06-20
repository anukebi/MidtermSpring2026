package uno.model.player;

import uno.model.card.Card;

import java.util.ArrayList;
import java.util.List;

public final class Player {

  private final String name;
  private final PlayerType type;
  private final List<Card> hand;
  private int score;

  public Player(String name, PlayerType type) {
    this.name = name;
    this.type = type;
    this.hand = new ArrayList<>();
    this.score = 0;
  }

  public String getName() {
    return name;
  }

  public PlayerType getType() {
    return type;
  }

  public List<Card> getHand() {
    return hand;
  }

  public void clearHand() {
    hand.clear();
  }

  public void addCard(Card card) {
    hand.add(card);
  }

  public int getScore() {
    return score;
  }

  public void addScore(int score) {
    this.score += score;
  }

  public void resetScore() {
    this.score = 0;
  }

}
