package uno;

import java.util.List;
import java.util.Scanner;

public class CliInput {

  private final Scanner scanner;

  public CliInput(Scanner scanner) {
    this.scanner = scanner;
  }

  public int askCardChoice(List<String> hand, String upCard, String calledColor) {
    while (true) {
      System.out.print("Choose card index/code or draw: ");
      String input = scanner.nextLine().trim().toUpperCase();
      if (input.equals("DRAW")) {
        return -1;
      }

      try {
        int index = Integer.parseInt(input);
        if (index >= 0 && index < hand.size()) {
          return index;
        }
      } catch (Exception ignored) {}

      for (int i = 0; i < hand.size(); i++) {
        if (hand.get(i).equals(input)) {
          if (RulesValidator.isValid(input, upCard, calledColor)) {
            return i;
          }
          System.out.println("That card is not legal.");
        }
      }

      System.out.println("Card not found.");
    }
  }

  public boolean askPlayDrawnCard(String drawn) {
    System.out.print("Play drawn card " + drawn + "? y/n: ");
    String answer = scanner.nextLine();
    return answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes");
  }

  public String askColor() {
    while (true) {
      System.out.print("Call color R/Y/G/B: ");
      String input = scanner.nextLine().trim().toUpperCase();
      if (input.equals("R")) {
        return "R";
      }
      if (input.equals("Y")) {
        return "Y";
      }
      if (input.equals("G")) {
        return "G";
      }
      if (input.equals("B")) {
        return "B";
      }
      System.out.println("Bad color.");
    }
  }
}
