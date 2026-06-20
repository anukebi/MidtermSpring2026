package edu.kiu.uno.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.kiu.uno.model.CardMapper;
import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.model.card.CardRank;

public class Deck {

  private final List<Card> drawPile;
  private final List<Card> discardPile;
  private final Random random;

  public Deck(Random random) {
    this.random = random;
    this.drawPile =  new LinkedList<>();
    this.discardPile = new ArrayList<>();
  }

  public int size() {
    return drawPile.size();
  }

  public void initializeDeck() {
    drawPile.clear();
    discardPile.clear();

    for (var color : CardColor.values()) {
      if (color == CardColor.WILD) {
        continue;
      }
      
      addCard(color, CardRank.NUMBER, 0, 1);
      for (int n = 1; n <= 9; n++) {
        addCard(color, CardRank.NUMBER, n, 2);
      }

      addCard(color, CardRank.SKIP, 2);
      addCard(color, CardRank.REVERSE, 2);
      addCard(color, CardRank.DRAW, 2, 2);
    }

    addCard(CardColor.WILD, CardRank.CHANGE, 4);
    addCard(CardColor.WILD, CardRank.DRAW, 4, 4);

    Collections.shuffle(drawPile, random);
  }

  public void discard(Card card) {
    discardPile.add(card);
  }

  public Card draw() {
    if (drawPile.isEmpty()) {
      drawPile.addAll(discardPile);
      discardPile.clear();
      Collections.shuffle(drawPile, random);
    }

    return drawPile.isEmpty()
        ? CardMapper.getCard(CardColor.WILD, CardRank.CHANGE, null)
        : drawPile.removeFirst();
  }

  private void addCard(CardColor color, CardRank rank, int times) {
    addCard(color, rank, null, times);
  }

  private void addCard(CardColor color, CardRank rank, Integer value, int times) {
    for (int i = 0; i < times; i++) {
      drawPile.add(CardMapper.getCard(color, rank, value));
    }
  }

}
