package edu.kiu.uno.service.controller.input;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.model.player.PlayerType;

public class ScriptedInputService implements PlayerInputService {

  private final Deque<Integer> cardChoices = new ArrayDeque<>();
  private final Deque<CardColor> colorChoices = new ArrayDeque<>();
  private final Deque<Boolean> drawConfirmations = new ArrayDeque<>();
  private final Deque<Boolean> unoConfirmations = new ArrayDeque<>();

  public void chooseCard(int index) {
    cardChoices.add(index);
  }

  public void chooseDraw() {
    cardChoices.add(-1);
  }

  public void chooseColor(CardColor color) {
    colorChoices.add(color);
  }

  public void confirmDrawnCard(boolean play) {
    drawConfirmations.add(play);
  }

  public void confirmUno(boolean called) {
    unoConfirmations.add(called);
  }

  @Override
  public int getCardChoice(List<Card> hand, Card upCard, CardColor calledColor, int pendingDrawAmount) {
    if (cardChoices.isEmpty()) {
      return -1;
    }
    return cardChoices.removeFirst();
  }

  @Override
  public CardColor getCardColor(List<Card> hand) {
    if (colorChoices.isEmpty()) {
      throw new IllegalStateException("No scripted color choice");
    }
    return colorChoices.removeFirst();
  }

  @Override
  public boolean confirmDrawnCard(Card drawn) {
    if (drawConfirmations.isEmpty()) {
      return false;
    }
    return drawConfirmations.removeFirst();
  }

  @Override
  public boolean awaitConfirmUno() {
    if (unoConfirmations.isEmpty()) {
      return false;
    }
    return unoConfirmations.removeFirst();
  }

  @Override
  public boolean supportsPlayer(PlayerType playerType) {
    return true;
  }

}
