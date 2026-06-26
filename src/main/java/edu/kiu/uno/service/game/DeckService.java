package edu.kiu.uno.service.game;

import edu.kiu.uno.config.properties.DeckProperties;
import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.model.card.CardRank;
import edu.kiu.uno.model.card.Deck;
import edu.kiu.uno.model.player.Player;
import edu.kiu.uno.util.CardMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.stream.IntStream;

@Log4j2
@Service
public class DeckService {

  private final CardMapper cardMapper;
  private final DeckProperties deckProperties;
  private final Deck deck;

  public DeckService(CardMapper cardMapper, DeckProperties deckProperties, Random random) {
    this.cardMapper = cardMapper;
    this.deckProperties = deckProperties;
    this.deck = new Deck(random, cardMapper.getCard(CardColor.WILD, CardRank.CHANGE, null));
  }

  public void initializeDeck() {
    log.info("initializeDeck:: Initializing deck with properties: {}", deckProperties);
    for (var color : CardColor.values()) {
      if (color == CardColor.WILD) {
        addCard(color, CardRank.CHANGE, deckProperties.getWild());
        addCard(color, CardRank.DRAW, 4, deckProperties.getWildDrawFour());
        continue;
      }
      
      addCard(color, CardRank.NUMBER, 0, deckProperties.getZero());
      for (int n = 1; n <= 9; n++) {
        addCard(color, CardRank.NUMBER, n, deckProperties.getNumber());
      }

      addCard(color, CardRank.SKIP, deckProperties.getSkip());
      addCard(color, CardRank.REVERSE, deckProperties.getReverse());
      addCard(color, CardRank.DRAW, 2, deckProperties.getDrawTwo());
    }
    deck.shuffle();
  }

  public void discard(Card card) {
    log.info("discard:: Discarding card {} to discard pile", card);
    deck.add(card);
  }

  public Card draw() {
    return draw(false);
  }

  public Card draw(Player player) {
    return player.addCard(draw(true));
  }

  private Card draw(boolean quiet) {
    var card = deck.draw();
    if (!quiet) log.info("draw:: Drew card {} from draw pile", card);
    return card;
  }
  
  public void distribute(GameState state) {
    log.info("distribute:: Distributing deck cards to players and setting up initial card");
    // Give each player 7 cards
    state.getPlayers().forEach(player -> IntStream.range(0, deckProperties.getPlayerCards()).forEach(i -> draw(player)));

    // Put down initial non-wild card
    var card = draw();
    while (card.color() == CardColor.WILD) {
      discard(card);
      card = draw();
    }
    state.setUpCard(card);
  }

  private void addCard(CardColor color, CardRank rank, int times) {
    addCard(color, rank, null, times);
  }

  private void addCard(CardColor color, CardRank rank, Integer value, int times) {
    for (int i = 0; i < times; i++) {
      deck.add(cardMapper.getCard(color, rank, value));
    }
  }

}
