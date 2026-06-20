package uno.io;

import uno.game.GameState;
import uno.model.card.Card;
import uno.model.card.CardColor;
import uno.model.player.Player;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CliOutput {

  private final PrintStream out;

  public CliOutput(boolean quiet) {
    this.out = quiet ? new PrintStream(new ByteArrayOutputStream()) : System.out;
  }

  public void printGameHeader(int gameNumber) {
    out.println("\n=== Game " + gameNumber + " ===");
  }

  public void printTurn(Player player, Card upCard, CardColor calledColor) {
    out.println("\nUp card: " + upCard + (calledColor == null ? "" : " called " + calledColor));
    out.println(player.getName() + " hand: " + formatHand(player.getHand()));
  }

  public void printPendingDraw(Player player, int pendingDraw) {
    out.println(player.getName() + " must stack or draw " + pendingDraw + " cards.");
  }

  public void printDraw(Player player, Card drawn) {
    out.println(player.getName() + " draws " + drawn);
  }

  public void printInvalidIndexPenalty(Player player) {
    out.println(player.getName() + " selected an invalid index and draws a penalty card.");
  }

  public void printIllegalPlayPenalty(Player player, Card card) {
    out.println(player.getName() + " tried illegal card " + card + " and draws a penalty card.");
  }

  public void printPlay(Player player, Card card) {
    out.println(player.getName() + " plays " + card);
  }

  public void printCalledColor(Player player, CardColor color) {
    out.println(player.getName() + " calls " + color);
  }

  public void printUno(Player player) {
    out.println(player.getName() + " says UNO!");
  }

  public void printWin(Player player, int pointsScored) {
    out.println(player.getName() + " wins and scores " + pointsScored);
  }

  public void printDraws(Player player, int count) {
    out.println(player.getName() + " draws " + count + ".");
  }

  public void printSafetyLimit() {
    out.println("Game stopped at safety limit.");
  }

  public void printFinalScores(GameState state) {
    System.out.println("\nFinal scores:");
    state.getPlayers()
        .forEach(player -> System.out.println(player.getName() + ": " + player.getScore()));
  }

  public static String formatHand(List<Card> cards) {
    return IntStream.range(0, cards.size())
        .mapToObj(i -> i + ":" + cards.get(i) + (i < cards.size() - 1 ? " " : ""))
        .collect(Collectors.joining());
  }
  
}
