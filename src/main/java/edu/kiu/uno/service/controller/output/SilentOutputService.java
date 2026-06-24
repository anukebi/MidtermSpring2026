package edu.kiu.uno.service.controller.output;

import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.model.player.Player;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "quiet", havingValue = "true")
public class SilentOutputService implements PlayerOutputService {
  
  @Override
  public void displayGameHeader(int gameNumber) { }

  @Override
  public void displayPlayerTurn(Player player, Card upCard, CardColor calledColor) { }

  @Override
  public void displayPendingDraw(Player player, int pendingDraw) { }

  @Override
  public void displayDraw(Player player, Card drawn) { }

  @Override
  public void displayInvalidIndexPenalty(Player player) { }

  @Override
  public void displayIllegalPlayPenalty(Player player, Card card) { }

  @Override
  public void displayPlay(Player player, Card card) { }

  @Override
  public void displayCalledColor(Player player, CardColor color) { }

  @Override
  public void displayUno(Player player) { }

  @Override
  public void displayWin(Player player, int pointsScored) { }

  @Override
  public void displayDraws(Player player, int count) { }

  @Override
  public void displaySafetyLimit() { }

  @Override
  public void displayFinalScores() { }

  @Override
  public <T> String displayQueryResult(String query, T result) { return null; }
  
}
