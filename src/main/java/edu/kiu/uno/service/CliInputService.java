package edu.kiu.uno.service;

import java.util.List;
import java.util.Scanner;

import org.springframework.stereotype.Service;

import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.util.RulesValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CliInputService {

  private final Scanner scanner;
  private final RulesValidator rulesValidator;

  public int askCardChoice(List<Card> hand, Card upCard, CardColor calledColor, int pendingDrawAmount) {
    while (true) {
      if (pendingDrawAmount > 0) {
        System.out.print("Stack +" + pendingDrawAmount + " or draw: ");
      } else {
        System.out.print("Choose card index/code or draw: ");
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
          System.out.println("Must play a matching +" + pendingDrawAmount + " or draw.");
          continue;
        }
      } catch (Exception ignored) {}

      for (int i = 0; i < hand.size(); i++) {
        if (hand.get(i).code().equals(input)) {
          if (pendingDrawAmount > 0) {
            if (rulesValidator.canStack(hand.get(i), pendingDrawAmount)) {
              return i;
            }
            System.out.println("Must play a matching +" + pendingDrawAmount + " or draw.");
          } else if (rulesValidator.isValid(hand.get(i), upCard, calledColor)) {
            return i;
          } else {
            System.out.println("That card is not legal.");
          }
        }
      }

      System.out.println("Card not found.");
    }
  }

  public boolean askPlayDrawnCard(Card drawn) {
    System.out.print("Play drawn card " + drawn + "? y/n: ");
    var answer = scanner.nextLine();
    return answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes");
  }

  public CardColor askColor() {
    while (true) {
      System.out.print("Call color R/Y/G/B: ");
      var input = scanner.nextLine().trim().toUpperCase();
      try {
        return CardColor.fromCode(input);
      } catch (Exception e) {
        System.out.println("Bad color.");
      }
    }
  }
}
