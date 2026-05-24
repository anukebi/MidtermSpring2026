package uno;

import java.util.List;

/**
 * Console output separated from rule execution.
 */
public record CliOutput(boolean quiet) {

  public void printGameHeader(int gameNumber) {
    if (!quiet) {
      System.out.println("\n=== Game " + gameNumber + " ===");
    }
  }

  public void printTurn(String upCard, String calledColor, String playerName, List<String> hand) {
    if (!quiet) {
      System.out.println("\nUp card: " + upCard + (calledColor.isBlank() ? "" : " called " + calledColor));
      System.out.println(playerName + " hand: " + formatHand(hand));
    }
  }

  public void printDraw(String playerName, String drawn) {
    if (!quiet) {
      System.out.println(playerName + " draws " + drawn);
    }
  }

  public void printInvalidIndexPenalty(String playerName) {
    if (!quiet) {
      System.out.println(playerName + " selected an invalid index and draws a penalty card.");
    }
  }

  public void printIllegalPlayPenalty(String playerName, String card) {
    if (!quiet) {
      System.out.println(playerName + " tried illegal card " + card + " and draws a penalty card.");
    }
  }

  public void printPlay(String playerName, String card) {
    if (!quiet) {
      System.out.println(playerName + " plays " + card);
    }
  }

  public void printCalledColor(String playerName, String color) {
    if (!quiet) {
      System.out.println(playerName + " calls " + color);
    }
  }

  public void printUno(String playerName) {
    if (!quiet) {
      System.out.println(playerName + " says UNO!");
    }
  }

  public void printWin(String playerName, int points) {
    if (!quiet) {
      System.out.println(playerName + " wins and scores " + points);
    }
  }

  public void printDraws(String playerName, int count) {
    if (!quiet) {
      if (count == 2) {
        System.out.println(playerName + " draws two.");
      } else if (count == 4) {
        System.out.println(playerName + " draws four.");
      }
    }
  }

  public void printSafetyLimit() {
    if (!quiet) {
      System.out.println("Game stopped at safety limit.");
    }
  }

  public void printFinalScores(GameState state) {
    System.out.println("\nFinal scores:");
    for (int i = 0; i < state.playerNames.size(); i++) {
      System.out.println(state.playerNames.get(i) + ": " + state.scores[i]);
    }
  }

  public static String formatHand(List<String> cards) {
    String out = "";
    for (int i = 0; i < cards.size(); i++) {
      out += i + ":" + cards.get(i);
      if (i < cards.size() - 1) {
        out += " ";
      }
    }
    return out;
  }
}
