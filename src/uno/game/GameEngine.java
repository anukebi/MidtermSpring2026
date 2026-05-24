package uno.game;

import uno.rule.BotLogic;
import uno.io.CliInput;
import uno.io.CliOutput;
import uno.rule.RulesValidator;
import uno.model.card.Card;
import uno.model.card.CardColor;
import uno.model.player.Player;
import uno.model.player.PlayerType;

import java.util.stream.IntStream;

public class GameEngine {

  private static final int TURN_SAFETY_LIMIT = 3000;

  private final GameState state;
  private final Deck deck;
  private final CliOutput cliOutput;
  private final CliInput cliInput;

  public GameEngine(GameState state, Deck deck, CliOutput cliOutput, CliInput cliInput) {
    this.state = state;
    this.deck = deck;
    this.cliOutput = cliOutput;
    this.cliInput = cliInput;
  }

  public void playGame() {
    deck.initializeDeck();
    state.initializeState();
    distributeCards();

    for (int guard = 0; guard < TURN_SAFETY_LIMIT; guard++) {
      if (playTurn()) {
        return;
      }
    }
    cliOutput.printSafetyLimit();
  }

  private void distributeCards() {
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
    cliOutput.printTurn(player, state.getUpCard(), state.getCalledColor());

    int chosen = resolveCardChoice(player);
    if (chosen == -1) {
      state.advancePlayer();
      return false;
    }

    return executePlay(player, chosen);
  }

  private int resolveCardChoice(Player player) {
    int chosen;
    if (player.getType() == PlayerType.HUMAN) {
      chosen = cliInput.askCardChoice(player.getHand(), state.getUpCard(), state.getCalledColor());
    } else {
      chosen = BotLogic.chooseCardIndex(player.getHand(), state.getUpCard(), state.getCalledColor());
    }

    if (chosen == -1) {
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
      cliOutput.printInvalidIndexPenalty(player);
      hand.add(deck.draw());
      state.advancePlayer();
      return false;
    }

    var card = hand.get(chosen);
    if (!RulesValidator.isValid(card, state.getUpCard(), state.getCalledColor())) {
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
          toDraw = 4;
        }
        state.advancePlayer();
        drawToCurrentHand(toDraw);
        cliOutput.printDraws(state.getCurrentPlayer(), toDraw);
        state.advancePlayer();
      }
      case CHANGE -> {
        triggerColorUpdate(player);
        state.advancePlayer();
      }
      default -> state.advancePlayer();
    }
  }

  private void drawToCurrentHand(int count) {
    for (int i = 0; i < count; i++) {
      state.getCurrentPlayer().addCard(deck.draw());
    }
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
