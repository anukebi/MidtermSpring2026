package edu.kiu.uno.service.game;

import edu.kiu.uno.service.controller.input.PlayerInputServiceProvider;
import edu.kiu.uno.service.controller.output.PlayerOutputService;
import edu.kiu.uno.service.game.effects.CardEffectStrategyProvider;
import org.springframework.stereotype.Service;

import edu.kiu.uno.config.properties.GameProperties;
import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.player.Player;
import edu.kiu.uno.service.game.GamePersistenceOrchestrator.PersistenceContext;
import edu.kiu.uno.util.RulesValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class GameEngine {
  
  private final GameProperties properties;
  private final GameState state;
  private final DeckService deckService;
  private final PlayerOutputService outputService;
  private final PlayerInputServiceProvider inputServiceProvider;
  private final CardEffectStrategyProvider cardEffectStrategyProvider;
  private final RulesValidator rulesValidator;
  private final GamePersistenceOrchestrator persistenceOrchestrator;

  public void playGame() {
    log.info("run:: Starting UNO with config: {}", properties);
    var ctx = new PersistenceContext();
    persistenceOrchestrator.startGame(ctx);
    for (int g = 1; g <= properties.getGames(); g++) {
      log.info("run:: Starting game {} of {}", g, properties.getGames());
      playRound(ctx, g);
    }
    persistenceOrchestrator.endGame(ctx);
    log.info("run:: All games completed. Final scores: {}", state.getPlayers().stream().map(p -> p.getName() + ": " + p.getTotalScore()).toList());
    outputService.displayFinalScores();
  }

  public void playRound(PersistenceContext ctx, int round) {
    log.info("playRound:: Starting new game with players {}", state.getPlayers().stream().map(Player::getName).toList());
    outputService.displayGameHeader(round);

    persistenceOrchestrator.startRound(ctx, round);
    deckService.initializeDeck();
    deckService.distribute(state);
    state.initializeState();

    for (int guard = 0; guard < properties.getTurnSafetyLimit(); guard++) {
      log.debug("playRound:: Starting turn {} with current player {} and up card {}", guard, state.getCurrentPlayer().getName(), state.getUpCard());
      if (playTurn()) {
        log.info("playRound:: Player {} won the game in turn {}, ending game", state.getCurrentPlayer().getName(), guard);
        persistenceOrchestrator.endRound(ctx, false);
        return;
      }
    }

    log.info("playRound:: Reached turn safety limit of {}, ending game to prevent infinite loop", properties.getTurnSafetyLimit());
    persistenceOrchestrator.endRound(ctx, true);
    outputService.displaySafetyLimit();
  }

  private boolean playTurn() {
    var player = state.getCurrentPlayer();
    log.info("playTurn:: Starting turn for player {}", player.getName());
    outputService.displayPlayerTurn(player, state.getUpCard(), state.getCalledColor());
    if (state.getPendingDraw() > 0) {
      outputService.displayPendingDraw(player, state.getPendingDraw());
    }

    int chosen = resolveCardChoice(player);
    if (chosen == -1) {
      if (state.getPendingDraw() > 0) {
        resolvePendingDraw(player);
      }
      state.advancePlayer();
      return false;
    }

    return executePlay(player, chosen);
  }

  private int resolveCardChoice(Player player) {
    int pendingAmount = state.getPendingDrawAmount();
    int chosen = inputServiceProvider.get(player).getCardChoice(player.getHand(), state.getUpCard(), state.getCalledColor(), pendingAmount);

    if (chosen == -1 && state.getPendingDraw() == 0) {
      var drawn = deckService.draw();
      player.addCard(drawn);
      outputService.displayDraw(player, drawn);

      if (rulesValidator.isValid(drawn, state.getUpCard(), state.getCalledColor()) && inputServiceProvider.get(player).confirmDrawnCard(drawn)) {
        chosen = player.getHand().size() - 1;
      }
    }

    return chosen;
  }

  private boolean executePlay(Player player, int chosen) {
    var hand = player.getHand();
    if (chosen >= hand.size()) {
      log.warn("executePlay:: Player {} selected invalid index {}, hand size is {}", player.getName(), chosen, hand.size());
      outputService.displayInvalidIndexPenalty(player);
      hand.add(deckService.draw());
      state.advancePlayer();
      return false;
    }

    var card = hand.get(chosen);
    log.debug("executePlay:: Player {} chose card {} with pending draw {}", player.getName(), card, state.getPendingDraw());

    if (state.getPendingDraw() > 0) {
      if (!rulesValidator.canStack(card, state.getPendingDrawAmount())) {
        outputService.displayIllegalPlayPenalty(player, card);
        hand.add(deckService.draw());
        state.advancePlayer();
        return false;
      }
    } else if (!rulesValidator.isValid(card, state.getUpCard(), state.getCalledColor())) {
      outputService.displayIllegalPlayPenalty(player, card);
      hand.add(deckService.draw());
      state.advancePlayer();
      return false;
    }

    hand.remove(chosen);
    deckService.discard(state.getUpCard());
    state.setUpCard(card);
    state.setCalledColor(null);
    outputService.displayPlay(player, card);

    if (hand.size() == 1) {
      outputService.displayUno(player);
    }

    if (hand.isEmpty()) {
      int points = scoreOpponents(state.getCurrentPlayerIndex());
      player.addScore(points);
      outputService.displayWin(player, points);
      return true;
    }

    log.debug("applyCardEffect:: Applying effect of card {} for player {}", card, player.getName());
    cardEffectStrategyProvider.get(card.rank()).execute(state, card, player);
    return false;
  }

  private int scoreOpponents(int winnerIndex) {
    int points = 0;
    for (int i = 0; i < state.getPlayers().size(); i++) {
      if (i == winnerIndex) {
        continue;
      }
      points += state.getPlayers().get(i)
          .getHand().stream()
          .map(Card::points)
          .reduce(0, Integer::sum);
    }
    return points;
  }

  private void resolvePendingDraw(Player player) {
    int count = state.getPendingDraw();
    for (int i = 0; i < count; i++) {
      player.addCard(deckService.draw());
    }
    state.clearPendingDraw();
    outputService.displayDraws(player, count);
  }

}
