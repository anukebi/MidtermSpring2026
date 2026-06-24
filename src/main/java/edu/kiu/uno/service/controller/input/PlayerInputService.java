package edu.kiu.uno.service.controller.input;

import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.model.player.PlayerType;

import java.util.List;

public interface PlayerInputService {

  int getCardChoice(List<Card> hand, Card upCard, CardColor calledColor, int pendingDrawAmount);

  CardColor getCardColor(List<Card> hand);

  boolean confirmDrawnCard(Card drawn);

  boolean supportsPlayer(PlayerType playerType);

}
