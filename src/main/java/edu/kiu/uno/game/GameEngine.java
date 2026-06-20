package edu.kiu.uno.game;

import java.util.stream.IntStream;

import edu.kiu.uno.io.CliInput;
import edu.kiu.uno.io.CliOutput;
import edu.kiu.uno.model.card.Card;
import edu.kiu.uno.model.card.CardColor;
import edu.kiu.uno.model.player.Player;
import edu.kiu.uno.model.player.PlayerType;
import edu.kiu.uno.rule.BotLogic;
import edu.kiu.uno.rule.RulesValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class GameEngine {

  private static final int TURN_SAFETY_LIMIT = 3000;

  private final GameState state;
  private final Deck deck;
  private final CliOutput cliOutput;
  private final CliInput cliInput;

  public void playGame() {
    log.info("playGame:: Starting new game with players {}", state.getPlayers().stream().map(Player::getName).toList());
    deck.initializeDeck();
    state.initializeState();
    distributeCards();

    for (int guard = 0; guard < TURN_SAFETY_LIMIT; guard++) {
      log.debug("playGame:: Starting turn {} with current player {} and up card {}", guard, state.getCurrentPlayer().getName(), state.getUpCard());
      if (playTurn()) {
        log.info("playGame:: Player {} won the game in turn {}, ending game", state.getCurrentPlayer().getName(), guard);
        return;
      }
    }

    log.info("playGame:: Reached turn safety limit of {}, ending game to prevent infinite loop", TURN_SAFETY_LIMIT);
    cliOutput.printSafetyLimit();
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
    cliOutput.printTurn(player, state.getUpCard(), state.getCalledColor());
    if (state.getPendingDraw() > 0) {
      cliOutput.printPendingDraw(player, state.getPendingDraw());
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
      chosen = cliInput.askCardChoice(player.getHand(), state.getUpCard(), state.getCalledColor(), pendingAmount);
    } else {
      chosen = BotLogic.chooseCardIndex(player.getHand(), state.getUpCard(), state.getCalledColor(), pendingAmount);
    }

    if (chosen == -1 && state.getPendingDraw() == 0) {
      var drawn = deck.draw();
      player.addCard(drawn);
      cliOutput.printDraw(player, drawn);

      if (RulesValidator.isValid(drawn, state.getUpCard(), state.getCalledColor())) {
        if (player.getType() != PlayerType.HUMAN) {
          chosen = player.getHand().size() - 1;
        } else if (cliInput.askPlayDrawnCard(drawn)) {
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
      cliOutput.printInvalidIndexPenalty(player);
      hand.add(deck.draw());
      state.advancePlayer();
      return false;
    }

    var card = hand.get(chosen);
    log.debug("executePlay:: Player {} chose card {} with pending draw {}", player.getName(), card, state.getPendingDraw());

    if (state.getPendingDraw() > 0) {
      if (!RulesValidator.canStack(card, state.getPendingDrawAmount())) {
        cliOutput.printIllegalPlayPenalty(player, card);
        hand.add(deck.draw());
        state.advancePlayer();
        return false;
      }
    } else if (!RulesValidator.isValid(card, state.getUpCard(), state.getCalledColor())) {
      cliOutput.printIllegalPlayPenalty(player, card);
      hand.add(deck.draw());
      state.advancePlayer();
      return false;
    }

    hand.remove(chosen);
    deck.discard(state.getUpCard());
    state.setUpCard(card);
    state.setCalledColor(null);
    cliOutput.printPlay(player, card);

    if (hand.size() == 1) {
      cliOutput.printUno(player);
    }

    if (hand.isEmpty()) {
      int points = scoreOpponents(state.getCurrentPlayerIndex());
      player.addScore(points);
      cliOutput.printWin(player, points);
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
    cliOutput.printDraws(player, count);
  }

  private void triggerColorUpdate(Player player) {
    if (player.getType() == PlayerType.HUMAN) {
      state.setCalledColor(cliInput.askColor());
    } else {
      state.setCalledColor(BotLogic.chooseColor(player.getHand()));
    }
    cliOutput.printCalledColor(player, state.getCalledColor());
  }

}
