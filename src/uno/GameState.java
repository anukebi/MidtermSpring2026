package uno;

import java.util.ArrayList;
import java.util.List;

public class GameState {

  public final List<String> playerNames = new ArrayList<>();
  public final List<Boolean> humanPlayers = new ArrayList<>();
  public final List<List<String>> hands = new ArrayList<>();
  public final int[] scores = new int[10];

  public int currentPlayer = 0;
  public int direction = 1;
  public String upCard = "";
  public String calledColor = "";

  public void setupPlayers(int bots, boolean human) {
    playerNames.clear();
    humanPlayers.clear();
    hands.clear();

    if (human) {
      playerNames.add("You");
      humanPlayers.add(Boolean.TRUE);
      hands.add(new ArrayList<>());
    }

    for (int i = 1; i <= bots; i++) {
      playerNames.add("Bot" + i);
      humanPlayers.add(Boolean.FALSE);
      hands.add(new ArrayList<>());
    }
  }

  public void clearHands() {
    for (List<String> hand : hands) {
      hand.clear();
    }
  }

  public int playerCount() {
    return playerNames.size();
  }

  public boolean isHuman(int playerIndex) {
    return humanPlayers.get(playerIndex);
  }

  public void advancePlayer() {
    currentPlayer += direction;
    if (currentPlayer >= playerNames.size()) {
      currentPlayer = 0;
    }
    if (currentPlayer < 0) {
      currentPlayer = playerNames.size() - 1;
    }
  }

}
