package edu.kiu.uno.service.game.effects;

import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardRank;
import edu.kiu.uno.model.player.Player;
import edu.kiu.uno.service.controller.input.PlayerInputServiceProvider;
import edu.kiu.uno.service.controller.output.PlayerOutputService;
import edu.kiu.uno.service.game.GameState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChangeCardEffectStrategy extends CardEffectStrategy {

  private final PlayerOutputService outputService;
  private final PlayerInputServiceProvider inputServiceProvider;

  @Override
  public boolean supports(CardRank cardRank) {
    return cardRank == CardRank.CHANGE;
  }

  @Override
  protected void doExecute(GameState state, Card card, Player player) {
    state.setCalledColor(inputServiceProvider.get(player).getCardColor(player.getHand()));
    outputService.displayCalledColor(player, state.getCalledColor());
  }

}
