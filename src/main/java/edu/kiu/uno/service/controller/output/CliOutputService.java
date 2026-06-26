package edu.kiu.uno.service.controller.output;

import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.model.player.Player;
import edu.kiu.uno.service.game.GameState;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@ConditionalOnProperty(value = "quiet", havingValue = "false", matchIfMissing = true)
@RequiredArgsConstructor
public class CliOutputService implements PlayerOutputService {

  private final PrintStream out;
  private final GameState state;

  @Override
  public void displayGameHeader(int gameNumber) {
    out.println("\n=== Game " + gameNumber + " ===");
  }

  @Override
  public void displayPlayerTurn(Player player, Card upCard, CardColor calledColor) {
    out.println("\nUp card: " + upCard + (calledColor == null ? "" : " called " + calledColor));
    out.println(player.getName() + " hand: " + formatHand(player.getHand()));
  }

  @Override
  public void displayPendingDraw(Player player, int pendingDraw) {
    out.println(player.getName() + " must stack or draw " + pendingDraw + " cards.");
  }

  @Override
  public void displayDraw(Player player, Card drawn) {
    out.println(player.getName() + " draws " + drawn);
  }

  @Override
  public void displayMissedUnoPenalty(Player player) {
    out.println(player.getName() + " failed to call UNO and draws a penalty card.");
  }

  @Override
  public void displayInvalidIndexPenalty(Player player) {
    out.println(player.getName() + " selected an invalid index and draws a penalty card.");
  }

  @Override
  public void displayIllegalPlayPenalty(Player player, Card card) {
    out.println(player.getName() + " tried illegal card " + card + " and draws a penalty card.");
  }

  @Override
  public void displayPlay(Player player, Card card) {
    out.println(player.getName() + " plays " + card);
  }

  @Override
  public void displayCalledColor(Player player, CardColor color) {
    out.println(player.getName() + " calls " + color);
  }

  @Override
  public void displayUno(Player player) {
    out.println(player.getName() + " says UNO!");
  }

  @Override
  public void displayWin(Player player, int pointsScored) {
    out.println(player.getName() + " wins and scores " + pointsScored);
  }

  @Override
  public void displayDraws(Player player, int count) {
    out.println(player.getName() + " draws " + count + ".");
  }

  @Override
  public void displaySafetyLimit() {
    out.println("Game stopped at safety limit.");
  }

  @Override
  public void displayFinalScores() {
    System.out.println("\nFinal scores:");
    state.getPlayers().forEach(player -> System.out.println(player.getName() + ": " + player.getTotalScore()));
  }

  @Override
  public <T> String displayQueryResult(String query, T result) {
    out.println("Query: " + query);
    out.println("Result: " + result);
    return result.toString();
  }

  private static String formatHand(List<Card> cards) {
    return IntStream.range(0, cards.size())
        .mapToObj(i -> i + ":" + cards.get(i) + (i < cards.size() - 1 ? " " : ""))
        .collect(Collectors.joining());
  }

}
