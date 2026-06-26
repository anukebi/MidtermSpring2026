package edu.kiu.uno.service.controller.input;

import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import edu.kiu.uno.config.properties.GameProperties;
import edu.kiu.uno.model.player.PlayerType;
import edu.kiu.uno.util.ScannerInputProvider;
import org.springframework.stereotype.Service;

import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.util.RulesValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CliInputService implements PlayerInputService {

  private final ScannerInputProvider inputProvider;
  private final PrintStream printStream;
  private final RulesValidator rulesValidator;
  private final GameProperties gameProperties;

  @Override
  public int getCardChoice(List<Card> hand, Card upCard, CardColor calledColor, int pendingDrawAmount) {
    while (true) {
      if (pendingDrawAmount > 0) {
        printStream.print("Stack +" + pendingDrawAmount + " or draw: ");
      } else {
        printStream.print("Choose card index/code or draw: ");
      }
      String input = inputProvider.getInput().toUpperCase();
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
      var input = inputProvider.getInput().toUpperCase();
      try {
        return CardColor.fromCode(input);
      } catch (Exception e) {
        printStream.println("Bad color.");
      }
    }
  }

  public boolean confirmDrawnCard(Card drawn) {
    printStream.print("Play drawn card " + drawn + "? y/n: ");
    var answer = inputProvider.getInput();
    return answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes");
  }

  @Override
  public boolean awaitConfirmUno() {
    printStream.println("You have one card left! Type 'UNO' to confirm!");
    var answer = inputProvider.getInput(gameProperties.getUnoTimeout(), TimeUnit.MILLISECONDS);
    return Optional.ofNullable(answer).map("UNO"::equalsIgnoreCase).orElse(false);
  }

  @Override
  public boolean supportsPlayer(PlayerType playerType) {
    return playerType == PlayerType.HUMAN;
  }

}
