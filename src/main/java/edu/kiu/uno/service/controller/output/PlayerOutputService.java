package edu.kiu.uno.service.controller.output;

import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.model.player.Player;

public interface PlayerOutputService {

  void displayGameHeader(int gameNumber);

  void displayPlayerTurn(Player player, Card upCard, CardColor calledColor);

  void displayPendingDraw(Player player, int pendingDraw);

  void displayDraw(Player player, Card drawn);

  void displayInvalidIndexPenalty(Player player);

  void displayIllegalPlayPenalty(Player player, Card card);

  void displayPlay(Player player, Card card);

  void displayCalledColor(Player player, CardColor color);

  void displayUno(Player player);

  void displayWin(Player player, int pointsScored);

  void displayDraws(Player player, int count);

  void displaySafetyLimit();

  void displayFinalScores();

  <T> String displayQueryResult(String query, T result);
  
}
