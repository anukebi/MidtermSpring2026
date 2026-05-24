package uno.game;

import uno.model.card.Card;
import uno.model.card.CardColor;
import uno.model.player.Player;
import uno.model.player.PlayerType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameState {

  private final Random random;
  private final List<Player> players;
  private int currentPlayer;
  private GameDirection direction;
  private Card upCard;
  private CardColor calledColor;
  private int pendingDraw;
  private int pendingDrawAmount;

  public GameState(Random random, int bots, boolean human) {
    this.players = new ArrayList<>();
    this.random = random;
    this.currentPlayer = 0;
    this.direction = GameDirection.FORWARD;
    this.upCard = null;
    this.calledColor = null;

    if (human) {
      players.add(new Player("You", PlayerType.HUMAN));
    }
    for (int i = 1; i <= bots; i++) {
      players.add(new Player("Bot" + i, PlayerType.BOT));
    }
  }

  public void initializeState() {
    players.forEach(Player::clearHand);
    currentPlayer = random.nextInt(players.size());
    direction = GameDirection.FORWARD;
    upCard = null;
    calledColor = null;
    pendingDraw = 0;
    pendingDrawAmount = 0;
  }

  public int getPendingDraw() {
    return pendingDraw;
  }

  public int getPendingDrawAmount() {
    return pendingDrawAmount;
  }

  public void startPendingDraw(int amount) {
    pendingDraw = amount;
    pendingDrawAmount = amount;
  }

  public void addPendingDraw(int amount) {
    pendingDraw += amount;
  }

  public void clearPendingDraw() {
    pendingDraw = 0;
    pendingDrawAmount = 0;
  }

  public int playerCount() {
    return players.size();
  }

  public boolean isHuman(int playerIndex) {
    return players.get(playerIndex).getType() == PlayerType.HUMAN;
  }

  public void advancePlayer() {
    currentPlayer += direction.getValue();
    if (currentPlayer >= players.size()) {
      currentPlayer = 0;
    }
    if (currentPlayer < 0) {
      currentPlayer = players.size() - 1;
    }
  }

  public List<Player> getPlayers() {
    return players;
  }

  public Player getCurrentPlayer() {
    return players.get(currentPlayer);
  }

  public int getCurrentPlayerIndex() {
    return currentPlayer;
  }

  public GameDirection getDirection() {
    return direction;
  }

  public void reverseDirection() {
    this.direction = direction.reverse();
  }

  public void resetDirection() {
    this.direction = GameDirection.FORWARD;
  }

  public Card getUpCard() {
    return upCard;
  }

  public void setUpCard(Card upCard) {
    this.upCard = upCard;
  }

  public CardColor getCalledColor() {
    return calledColor;
  }

  public void setCalledColor(CardColor calledColor) {
    this.calledColor = calledColor;
  }

}
