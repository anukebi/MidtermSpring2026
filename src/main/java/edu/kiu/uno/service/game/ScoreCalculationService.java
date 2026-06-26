package edu.kiu.uno.service.game;

import edu.kiu.uno.model.card.Card;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ScoreCalculationService {

  public int updatePlayerScore(GameState state) {
    var score = calculateScore(state);
    state.getCurrentPlayer().addScore(score);
    return score;
  }

  private int calculateScore(GameState state) {
    return state.getPlayers().stream()
        .filter(p -> !Objects.equals(p, state.getCurrentPlayer()))
        .flatMap(player -> player.getHand().stream())
        .map(Card::points)
        .reduce(0, Integer::sum);
  }

}
