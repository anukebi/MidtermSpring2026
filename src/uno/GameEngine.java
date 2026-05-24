package uno;

import java.util.List;
import java.util.Random;

public class GameEngine {

  private static final int TURN_SAFETY_LIMIT = 3000;

  private final Random random;
  private final GameState state;
  private final Deck deck;
  private final CliOutput cliOutput;
  private final CliInput cliInput;

  public GameEngine(GameState state, Deck deck, Random random, CliOutput cliOutput, CliInput cliInput) {
    this.state = state;
    this.deck = deck;
    this.random = random;
    this.cliOutput = cliOutput;
    this.cliInput = cliInput;
  }

  public void playGame() {
    deck.buildAndShuffle();
    state.clearHands();

    for (int i = 0; i < state.playerCount(); i++) {
      for (int j = 0; j < 7; j++) {
        state.hands.get(i).add(deck.draw());
      }
    }

    state.upCard = deck.draw();
    while (state.upCard.startsWith("W")) {
      deck.discard(state.upCard);
      state.upCard = deck.draw();
    }

    state.calledColor = "";
    state.direction = 1;
    state.currentPlayer = random.nextInt(state.playerCount());

    int guard = 0;
    while (guard < TURN_SAFETY_LIMIT) {
      guard++;
      if (playTurn()) {
        return;
      }
    }
    cliOutput.printSafetyLimit();
  }

  private boolean playTurn() {
    String name = state.playerNames.get(state.currentPlayer);
    List<String> hand = state.hands.get(state.currentPlayer);

    cliOutput.printTurn(state.upCard, state.calledColor, name, hand);

    int chosen = resolveCardChoice(hand, name);
    if (chosen == -1) {
      state.advancePlayer();
      return false;
    }

    return executePlay(hand, name, chosen);
  }

  private int resolveCardChoice(List<String> hand, String name) {
    int chosen;
    if (state.isHuman(state.currentPlayer)) {
      chosen = cliInput.askCardChoice(hand, state.upCard, state.calledColor);
    } else {
      chosen = BotLogic.chooseCardIndex(hand, state.upCard, state.calledColor);
    }

    if (chosen == -1) {
      String drawn = deck.draw();
      hand.add(drawn);
      cliOutput.printDraw(name, drawn);

      if (RulesValidator.isValid(drawn, state.upCard, state.calledColor)) {
        if (!state.isHuman(state.currentPlayer)) {
          chosen = hand.size() - 1;
        } else if (cliInput.askPlayDrawnCard(drawn)) {
          chosen = hand.size() - 1;
        }
      }
    }

    return chosen;
  }

  private boolean executePlay(List<String> hand, String name, int chosen) {
    if (chosen >= hand.size()) {
      cliOutput.printInvalidIndexPenalty(name);
      hand.add(deck.draw());
      state.advancePlayer();
      return false;
    }

    String cardCode = hand.get(chosen);
    if (!RulesValidator.isValid(cardCode, state.upCard, state.calledColor)) {
      cliOutput.printIllegalPlayPenalty(name, cardCode);
      hand.add(deck.draw());
      state.advancePlayer();
      return false;
    }

    hand.remove(chosen);
    deck.discard(state.upCard);
    state.upCard = cardCode;
    state.calledColor = "";
    cliOutput.printPlay(name, cardCode);

    Card card = new Card(cardCode);
    if (cardCode.equals("W") || cardCode.equals("W4")) {
      if (state.isHuman(state.currentPlayer)) {
        state.calledColor = cliInput.askColor();
      } else {
        state.calledColor = BotLogic.chooseColor(hand);
      }
      cliOutput.printCalledColor(name, state.calledColor);
    }

    if (hand.size() == 1) {
      cliOutput.printUno(name);
    }

    if (hand.isEmpty()) {
      int points = scoreOpponents(state.currentPlayer);
      state.scores[state.currentPlayer] += points;
      cliOutput.printWin(name, points);
      return true;
    }

    applyCardEffect(card);
    return false;
  }

  private int scoreOpponents(int winnerIndex) {
    int points = 0;
    for (int i = 0; i < state.hands.size(); i++) {
      if (i != winnerIndex) {
        for (int j = 0; j < state.hands.get(i).size(); j++) {
          points += new Card(state.hands.get(i).get(j)).points();
        }
      }
    }
    return points;
  }

  private void applyCardEffect(Card card) {
    String rank = card.rank();
    if (rank.equals("SKIP")) {
      state.advancePlayer();
      state.advancePlayer();
    } else if (rank.equals("REVERSE")) {
      state.direction = state.direction * -1;
      if (state.playerCount() == 2) {
        state.advancePlayer();
        state.advancePlayer();
      } else {
        state.advancePlayer();
      }
    } else if (rank.equals("DRAW_TWO")) {
      state.advancePlayer();
      drawToCurrentHand(2);
      cliOutput.printDraws(state.playerNames.get(state.currentPlayer), 2);
      state.advancePlayer();
    } else if (rank.equals("WILD_DRAW_FOUR")) {
      state.advancePlayer();
      drawToCurrentHand(4);
      cliOutput.printDraws(state.playerNames.get(state.currentPlayer), 4);
      state.advancePlayer();
    } else {
      state.advancePlayer();
    }
  }

  private void drawToCurrentHand(int count) {
    List<String> hand = state.hands.get(state.currentPlayer);
    for (int i = 0; i < count; i++) {
      hand.add(deck.draw());
    }
  }

}
