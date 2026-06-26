package edu.kiu.uno;

import java.util.List;
import java.util.Random;

import edu.kiu.uno.config.properties.DeckProperties;
import edu.kiu.uno.config.properties.GameProperties;
import edu.kiu.uno.service.controller.input.PlayerInputServiceProvider;
import edu.kiu.uno.service.controller.output.PlayerOutputService;
import edu.kiu.uno.service.game.DeckService;
import edu.kiu.uno.service.game.GameEngine;
import edu.kiu.uno.service.game.GamePersistenceOrchestrator;
import edu.kiu.uno.service.game.GameState;
import edu.kiu.uno.service.game.ScoreCalculationService;
import edu.kiu.uno.service.game.effects.CardEffectStrategy;
import edu.kiu.uno.service.game.effects.CardEffectStrategyProvider;
import edu.kiu.uno.service.game.effects.ChangeCardEffectStrategy;
import edu.kiu.uno.service.game.effects.DrawCardEffectStrategy;
import edu.kiu.uno.service.game.effects.NumberCardEffectStrategy;
import edu.kiu.uno.service.game.effects.ReverseCardEffectStrategy;
import edu.kiu.uno.service.game.effects.SkipCardEffectStrategy;
import edu.kiu.uno.util.CardMapper;
import edu.kiu.uno.util.RulesValidator;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GameTestFixtures {

  public static final CardMapper CARD_MAPPER = new CardMapper();
  public static final RulesValidator RULES_VALIDATOR = new RulesValidator();

  public static DeckProperties standardDeckProperties() {
    return new DeckProperties(1, 2, 2, 2, 2, 4, 4, 7);
  }

  public static DeckService newDeckService(Random random) {
    return new DeckService(CARD_MAPPER, standardDeckProperties(), random);
  }

  public static GameState newGameState(Random random, int bots, boolean human) {
    var state = new GameState(random, bots, human);
    state.initializeState();
    return state;
  }

  public static CardEffectStrategyProvider newEffectProvider(PlayerOutputService output, PlayerInputServiceProvider inputProvider) {
    List<CardEffectStrategy> strategies = List.of(
        new NumberCardEffectStrategy(),
        new SkipCardEffectStrategy(),
        new ReverseCardEffectStrategy(),
        new DrawCardEffectStrategy(output, inputProvider),
        new ChangeCardEffectStrategy(output, inputProvider)
    );
    return new CardEffectStrategyProvider(strategies);
  }

  public static GameEngine newGameEngine(GameProperties properties, GameState state, DeckService deckService,
                                         PlayerOutputService output, PlayerInputServiceProvider inputProvider,
                                         GamePersistenceOrchestrator persistence) {
    return new GameEngine(
        properties,
        state,
        deckService,
        output,
        inputProvider,
        newEffectProvider(output, inputProvider),
        new ScoreCalculationService(),
        RULES_VALIDATOR,
        persistence);
  }

}
