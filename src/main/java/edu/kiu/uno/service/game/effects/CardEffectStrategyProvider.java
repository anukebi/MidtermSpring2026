package edu.kiu.uno.service.game.effects;

import edu.kiu.uno.model.card.CardRank;
import edu.kiu.uno.model.exception.MissingStrategyException;
import edu.kiu.uno.util.StrategyProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CardEffectStrategyProvider implements StrategyProvider<CardRank, CardEffectStrategy> {

  private final List<CardEffectStrategy> cardEffectStrategies;

  @Override
  public CardEffectStrategy get(CardRank cardRank) {
    return cardEffectStrategies.stream()
        .filter(strategy -> strategy.supports(cardRank))
        .findFirst()
        .orElseThrow(() -> new MissingStrategyException("No input service found for player type: " + cardRank));
  }

}
