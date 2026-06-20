package edu.kiu.uno.service.game;

import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import edu.kiu.uno.config.properties.GameProperties;
import edu.kiu.uno.service.CliInputService;
import edu.kiu.uno.service.CliOutputService;
import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.model.player.Player;
import edu.kiu.uno.model.player.PlayerType;
import edu.kiu.uno.service.BotLogicService;
import edu.kiu.uno.service.game.GamePersistenceService.PersistenceContext;
import edu.kiu.uno.util.RulesValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class GameEngine {
  
  private final GameProperties properties;
  private final GameState state;
  private final Deck deck;
  private final CliOutputService cliOutputService;
  private final CliInputService cliInputService;
  private final RulesValidator rulesValidator;
  private final BotLogicService botLogicService;
  private final GamePersistenceService persistenceService;

  public void playGame() {
    log.info("run:: Starting UNO with config: {}", properties);
    var ctx = new PersistenceContext();
    persistenceService.startGame(ctx);
    for (int g = 1; g <= properties.getGames(); g++) {
      log.info("run:: Starting game {} of {}", g, properties.getGames());
      playRound(ctx, g);
    }
    persistenceService.endGame(ctx);
    log.info("run:: All games completed. Final scores: {}", state.getPlayers().stream().map(p -> p.getName() + ": " + p.getTotalScore()).toList());
    cliOutputService.printFinalScores();
  }

  public void playRound(PersistenceContext ctx, int round) {
    log.info("playRound:: Starting new game with players {}", state.getPlayers().stream().map(Player::getName).toList());
    persistenceService.startRound(ctx, round);
    cliOutputService.printGameHeader(round);
    deck.initializeDeck();
    state.initializeState();
    distributeCards();

    for (int guard = 0; guard < properties.getTurnSafetyLimit(); guard++) {
      log.debug("playRound:: Starting turn {} with current player {} and up card {}", guard, state.getCurrentPlayer().getName(), state.getUpCard());
      if (playTurn()) {
        log.info("playRound:: Player {} won the game in turn {}, ending game", state.getCurrentPlayer().getName(), guard);
        persistenceService.endRound(ctx, false);
        return;
      }
    }

    log.info("playRound:: Reached turn safety limit of {}, ending game to prevent infinite loop", properties.getTurnSafetyLimit());
    persistenceService.endRound(ctx, true);
    cliOutputService.printSafetyLimit();
  }

  private void distributeCards() {
    log.info("distributeCards:: Distributing cards to players and setting up initial card");
    // Give each player 7 cards
    state.getPlayers().forEach(player -> IntStream.range(0, 7)
        .forEach(i -> player.addCard(deck.draw())));

    // Put down initial non-wild card
    state.setUpCard(deck.draw());
    while (state.getUpCard().color() == CardColor.WILD) {
      deck.discard(state.getUpCard());
      state.setUpCard(deck.draw());
    }
  }

  private boolean playTurn() {
    var player = state.getCurrentPlayer();
    log.info("playTurn:: Starting turn for player {}", player.getName());
    cliOutputService.printTurn(player, state.getUpCard(), state.getCalledColor());
    if (state.getPendingDraw() > 0) {
      cliOutputService.printPendingDraw(player, state.getPendingDraw());
    }

    int chosen = resolveCardChoice(player);
    if (chosen == -1) {
      if (state.getPendingDraw() > 0) {
        resolvePendingDraw(player);
        state.clearPendingDraw();
      }
      state.advancePlayer();
      return false;
    }

    return executePlay(player, chosen);
  }

  private int resolveCardChoice(Player player) {
    int pendingAmount = state.getPendingDrawAmount();
    int chosen;
    if (player.getType() == PlayerType.HUMAN) {
      chosen = cliInputService.askCardChoice(player.getHand(), state.getUpCard(), state.getCalledColor(), pendingAmount);
    } else {
      chosen = botLogicService.chooseCardIndex(player.getHand(), state.getUpCard(), state.getCalledColor(), pendingAmount);
    }

    if (chosen == -1 && state.getPendingDraw() == 0) {
      var drawn = deck.draw();
      player.addCard(drawn);
      cliOutputService.printDraw(player, drawn);

      if (rulesValidator.isValid(drawn, state.getUpCard(), state.getCalledColor())) {
        if (player.getType() != PlayerType.HUMAN) {
          chosen = player.getHand().size() - 1;
        } else if (cliInputService.askPlayDrawnCard(drawn)) {
          chosen = player.getHand().size() - 1;
        }
      }
    }

    return chosen;
  }

  private boolean executePlay(Player player, int chosen) {
    var hand = player.getHand();
    if (chosen >= hand.size()) {
      log.warn("executePlay:: Player {} selected invalid index {}, hand size is {}", player.getName(), chosen, hand.size());
      cliOutputService.printInvalidIndexPenalty(player);
      hand.add(deck.draw());
      state.advancePlayer();
      return false;
    }

    var card = hand.get(chosen);
    log.debug("executePlay:: Player {} chose card {} with pending draw {}", player.getName(), card, state.getPendingDraw());

    if (state.getPendingDraw() > 0) {
      if (!rulesValidator.canStack(card, state.getPendingDrawAmount())) {
        cliOutputService.printIllegalPlayPenalty(player, card);
        hand.add(deck.draw());
        state.advancePlayer();
        return false;
      }
    } else if (!rulesValidator.isValid(card, state.getUpCard(), state.getCalledColor())) {
      cliOutputService.printIllegalPlayPenalty(player, card);
      hand.add(deck.draw());
      state.advancePlayer();
      return false;
    }

    hand.remove(chosen);
    deck.discard(state.getUpCard());
    state.setUpCard(card);
    state.setCalledColor(null);
    cliOutputService.printPlay(player, card);

    if (hand.size() == 1) {
      cliOutputService.printUno(player);
    }

    if (hand.isEmpty()) {
      int points = scoreOpponents(state.getCurrentPlayerIndex());
      player.addScore(points);
      cliOutputService.printWin(player, points);
      return true;
    }

    applyCardEffect(player, card);
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

  private void applyCardEffect(Player player, Card card) {
    log.debug("applyCardEffect:: Applying effect of card {} for player {}", card, player.getName());
    switch (card.rank()) {
      case SKIP -> {
        state.advancePlayer();
        state.advancePlayer();
      }
      case REVERSE -> {
        state.reverseDirection();
        if (state.playerCount() == 2) {
          state.advancePlayer();
          state.advancePlayer();
        } else {
          state.advancePlayer();
        }
      }
      case DRAW -> {
        var toDraw = 2;
        if (card.color() == CardColor.WILD) {
          triggerColorUpdate(player);
        }
        if (state.getPendingDraw() > 0) {
          state.addPendingDraw(toDraw);
        } else {
          state.startPendingDraw(toDraw);
        }
        state.advancePlayer();
      }
      case CHANGE -> {
        triggerColorUpdate(player);
        state.advancePlayer();
      }
      default -> state.advancePlayer();
    }
  }

  private void resolvePendingDraw(Player player) {
    int count = state.getPendingDraw();
    for (int i = 0; i < count; i++) {
      player.addCard(deck.draw());
    }
    cliOutputService.printDraws(player, count);
  }

  private void triggerColorUpdate(Player player) {
    if (player.getType() == PlayerType.HUMAN) {
      state.setCalledColor(cliInputService.askColor());
    } else {
      state.setCalledColor(botLogicService.chooseColor(player.getHand()));
    }
    cliOutputService.printCalledColor(player, state.getCalledColor());
  }

}
