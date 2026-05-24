package uno;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Deck {

  private static final Set<String> VALID_COLORS = Set.of("R", "Y", "G", "B");

  private final List<String> drawPile = new ArrayList<>();
  private final List<String> discardPile = new ArrayList<>();
  private final Random random;

  public Deck(Random random) {
    this.random = random;
  }

  public void buildAndShuffle() {
    drawPile.clear();

    for (String color : VALID_COLORS) {
      drawPile.add(color + "0");
      for (int n = 1; n <= 9; n++) {
        drawPile.add(color + n);
        drawPile.add(color + n);
      }
      drawPile.add(color + "S");
      drawPile.add(color + "S");
      drawPile.add(color + "R");
      drawPile.add(color + "R");
      drawPile.add(color + "+2");
      drawPile.add(color + "+2");
    }

    for (int i = 0; i < 4; i++) {
      drawPile.add("W");
      drawPile.add("W4");
    }

    Collections.shuffle(drawPile, random);
      discardPile.clear();
  }

  public void discard(String card) {
    discardPile.add(card);
  }

  public String draw() {
    if (drawPile.isEmpty()) {
      drawPile.addAll(discardPile);
      discardPile.clear();
      Collections.shuffle(drawPile, random);
    }

    if (drawPile.isEmpty()) {
      return "W";
    }
    return drawPile.removeFirst();
  }

}
