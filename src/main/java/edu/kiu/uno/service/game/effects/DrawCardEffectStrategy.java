package edu.kiu.uno.service.game.effects;

import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.model.card.CardRank;
import edu.kiu.uno.model.player.Player;
import edu.kiu.uno.service.controller.input.PlayerInputServiceProvider;
import edu.kiu.uno.service.controller.output.PlayerOutputService;
import edu.kiu.uno.service.game.GameState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class DrawCardEffectStrategy extends ChangeCardEffectStrategy {

  public DrawCardEffectStrategy(PlayerOutputService outputService, PlayerInputServiceProvider inputServiceProvider) {
    super(outputService, inputServiceProvider);
  }

  @Override
  public boolean supports(CardRank cardRank) {
    return cardRank == CardRank.DRAW;
  }

  @Override
  protected void doExecute(GameState state, Card card, Player player) {
    var toDraw = 2;
    if (card.color() == CardColor.WILD) {
      super.doExecute(state, card, player);
    }
    if (state.getPendingDraw() > 0) {
      state.addPendingDraw(toDraw);
    } else {
      state.startPendingDraw(toDraw);
    }
  }

}
