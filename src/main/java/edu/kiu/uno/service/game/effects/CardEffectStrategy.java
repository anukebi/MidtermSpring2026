package edu.kiu.uno.service.game.effects;

import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardRank;
import edu.kiu.uno.model.player.Player;
import edu.kiu.uno.service.game.GameState;

public abstract class CardEffectStrategy {

  public abstract boolean supports(CardRank cardRank);

  public void execute(GameState state, Card card, Player player) {
    doExecute(state, card, player);
    state.advancePlayer();
  }

  protected abstract void doExecute(GameState state, Card card, Player player);

}
