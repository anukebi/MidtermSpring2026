package edu.kiu.uno.service.game.effects;

import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardRank;
import edu.kiu.uno.model.player.Player;
import edu.kiu.uno.service.game.GameState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SkipCardEffectStrategy extends CardEffectStrategy {

  @Override
  public boolean supports(CardRank cardRank) {
    return cardRank == CardRank.SKIP;
  }

  @Override
  protected void doExecute(GameState state, Card card, Player player) {
    state.advancePlayer();
  }

}
