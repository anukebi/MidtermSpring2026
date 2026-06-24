package edu.kiu.uno.model.card;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public class Deck {

  private final List<Card> drawPile = new LinkedList<>();
  private final List<Card> discardPile = new ArrayList<>();
  private final Random random;
  private final Card fallbackCard;

  public Card draw() {
    if (drawPile.isEmpty()) {
      drawPile.addAll(discardPile);
      discardPile.clear();
      shuffle();
    }
    return drawPile.isEmpty() ? fallbackCard : drawPile.removeFirst();
  }

  public void discard(Card card) {
    discardPile.add(card);
  }

  public void add(Card card) {
    drawPile.add(card);
  }

  public void reset() {
    drawPile.clear();
    discardPile.clear();
  }

  public void shuffle() {
    Collections.shuffle(drawPile, random);
  }

}
