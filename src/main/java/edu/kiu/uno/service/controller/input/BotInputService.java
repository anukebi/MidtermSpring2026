package edu.kiu.uno.service.controller.input;

import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.model.card.CardRank;
import edu.kiu.uno.model.player.PlayerType;
import edu.kiu.uno.util.RulesValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class BotInputService implements PlayerInputService {

  private final RulesValidator rulesValidator;

  @Override
  public int getCardChoice(List<Card> hand, Card upCard, CardColor calledColor, int pendingDrawAmount) {
    if (pendingDrawAmount > 0) {
      for (int i = 0; i < hand.size(); i++) {
        if (rulesValidator.canStack(hand.get(i), pendingDrawAmount)) {
          return i;
        }
      }
      return -1;
    }

    int drawTwo = findFirstLegal(hand, upCard, calledColor, CardRank.DRAW);
    if (drawTwo >= 0) {
      return drawTwo;
    }
    int skip = findFirstLegal(hand, upCard, calledColor, CardRank.SKIP);
    if (skip >= 0) {
      return skip;
    }
    int number = findFirstLegal(hand, upCard, calledColor, CardRank.NUMBER);
    if (number >= 0) {
      return number;
    }
    for (int i = 0; i < hand.size(); i++) {
      if (hand.get(i).color() == CardColor.WILD) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public CardColor getCardColor(List<Card> hand) {
    EnumMap<CardColor, Integer> colorCounts = new EnumMap<>(CardColor.class);

    for (var card : hand) {
      colorCounts.put(card.color(), colorCounts.getOrDefault(card.color(), 0) + 1);
    }

    return colorCounts.entrySet()
        .stream()
        .filter(e -> e.getKey() != CardColor.WILD)
        .max(Comparator.comparingInt(Map.Entry::getValue))
        .map(Map.Entry::getKey)
        .orElseGet(() -> colorCounts.entrySet().iterator().next().getKey());
  }

  @Override
  public boolean confirmDrawnCard(Card drawn) {
    return true;
  }

  @Override
  public boolean awaitConfirmUno() {
    return true;
  }

  @Override
  public boolean supportsPlayer(PlayerType playerType) {
    return playerType == PlayerType.BOT;
  }

  private int findFirstLegal(List<Card> hand, Card upCard, CardColor calledColor, CardRank targetRank) {
    for (int i = 0; i < hand.size(); i++) {
      var card = hand.get(i);
      if (card.rank().equals(targetRank)
          && card.color() != CardColor.WILD
          && rulesValidator.isValid(card, upCard, calledColor)) {
        return i;
      }
    }
    return -1;
  }

}
