package edu.kiu.uno.service.controller.input;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import edu.kiu.uno.model.player.PlayerType;
import org.springframework.stereotype.Service;

import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.util.RulesValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CliInputService implements PlayerInputService {

  private final Scanner scanner;
  private final PrintStream printStream;
  private final RulesValidator rulesValidator;

  @Override
  public int getCardChoice(List<Card> hand, Card upCard, CardColor calledColor, int pendingDrawAmount) {
    while (true) {
      if (pendingDrawAmount > 0) {
        printStream.print("Stack +" + pendingDrawAmount + " or draw: ");
      } else {
        printStream.print("Choose card index/code or draw: ");
      }
      String input = scanner.nextLine().trim().toUpperCase();
      if (input.equals("DRAW")) {
        return -1;
      }

      try {
        int index = Integer.parseInt(input);
        if (index >= 0 && index < hand.size()) {
          if (pendingDrawAmount == 0 || rulesValidator.canStack(hand.get(index), pendingDrawAmount)) {
            return index;
          }
          printStream.println("Must play a matching +" + pendingDrawAmount + " or draw.");
          continue;
        }
      } catch (Exception ignored) {}

      for (int i = 0; i < hand.size(); i++) {
        if (hand.get(i).code().equals(input)) {
          if (pendingDrawAmount > 0) {
            if (rulesValidator.canStack(hand.get(i), pendingDrawAmount)) {
              return i;
            }
            printStream.println("Must play a matching +" + pendingDrawAmount + " or draw.");
          } else if (rulesValidator.isValid(hand.get(i), upCard, calledColor)) {
            return i;
          } else {
            printStream.println("That card is not legal.");
          }
        }
      }

      printStream.println("Card not found.");
    }
  }

  public CardColor getCardColor(List<Card> hand) {
    while (true) {
      printStream.print("Call color R/Y/G/B: ");
      var input = scanner.nextLine().trim().toUpperCase();
      try {
        return CardColor.fromCode(input);
      } catch (Exception e) {
        printStream.println("Bad color.");
      }
    }
  }

  public boolean confirmDrawnCard(Card drawn) {
    printStream.print("Play drawn card " + drawn + "? y/n: ");
    var answer = scanner.nextLine();
    return answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes");
  }

  @Override
  public boolean supportsPlayer(PlayerType playerType) {
    return playerType == PlayerType.HUMAN;
  }

}
