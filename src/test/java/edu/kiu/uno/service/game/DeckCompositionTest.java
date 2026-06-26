package edu.kiu.uno.service.game;

import static edu.kiu.uno.TestUtils.check;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;

import edu.kiu.uno.GameTestFixtures;
import edu.kiu.uno.model.card.CardColor;

class DeckCompositionTest {

  @Test
  void deckHas108Cards() {
    var deckService = GameTestFixtures.newDeckService(new Random(42));
    deckService.initializeDeck();
    int count = 0;
    for (int i = 0; i < 108; i++) {
      deckService.discard(deckService.draw());
      count++;
    }
    check("standard deck has 108 cards", count == 108);
  }

  @Test
  void deckContainsExpectedCardCounts() {
    var deckService = GameTestFixtures.newDeckService(new Random(42));
    deckService.initializeDeck();
    int skip = 0;
    int reverse = 0;
    int drawTwo = 0;
    int wild = 0;
    int wildDrawFour = 0;
    int numberCards = 0;
    Set<CardColor> numberColors = EnumSet.noneOf(CardColor.class);
    for (int i = 0; i < 108; i++) {
      var card = deckService.draw();
      deckService.discard(card);
      switch (card.rank()) {
        case SKIP -> skip++;
        case REVERSE -> reverse++;
        case DRAW -> {
          if (card.color() == CardColor.WILD) {
            wildDrawFour++;
          } else {
            drawTwo++;
          }
        }
        case CHANGE -> wild++;
        case NUMBER -> {
          numberCards++;
          numberColors.add(card.color());
        }
        default -> { }
      }
    }
    check("four colors in numbered cards", numberColors.size() == 4);
    check("76 numbered cards", numberCards == 76);
    check("eight skip cards", skip == 8);
    check("eight reverse cards", reverse == 8);
    check("eight draw two cards", drawTwo == 8);
    check("four wild cards", wild == 4);
    check("four wild draw four cards", wildDrawFour == 4);
  }

}
