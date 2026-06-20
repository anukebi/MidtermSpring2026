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
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class Deck {

  private final List<Card> drawPile = new LinkedList<>();
  private final List<Card> discardPile = new ArrayList<>();
  private final Random random;

  public int size() {
    return drawPile.size();
  }

  public void initializeDeck() {
    log.info("initializeDeck:: Initializing deck with draw pile {} and discard pile {}", drawPile.size(), discardPile.size());
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
    log.info("discard:: Discarding card {} to discard pile", card);
    discardPile.add(card);
  }

  public Card draw() {
    if (drawPile.isEmpty()) {
      drawPile.addAll(discardPile);
      discardPile.clear();
      Collections.shuffle(drawPile, random);
    }

    var card = drawPile.isEmpty()
        ? CardMapper.getCard(CardColor.WILD, CardRank.CHANGE, null)
        : drawPile.removeFirst();

    log.info("draw:: Drew card {} from draw pile", card);
    return card;
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
