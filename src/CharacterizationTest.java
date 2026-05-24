import uno.rule.BotLogic;
import uno.model.CardMapper;
import uno.game.Deck;
import uno.game.GameState;
import uno.rule.RulesValidator;
import uno.model.card.Card;
import uno.model.card.CardColor;
import uno.model.card.CardRank;

import java.util.List;
import java.util.Random;

public class CharacterizationTest {

  private static int passed = 0;
  private static int failed = 0;

  public static void main(String[] args) {
    testCardParsing();
    testWildDrawFourParsing();
    testMatchingByColor();
    testMatchingByNumber();
    testMatchingByActionType();
    testWildCards();
    testCalledColorAfterWild();
    testScoring();
    testBotLogicPriorities();
    testBotColorChoice();
    testDeckSize();
    testDeckRecycle();
    testDeckFallbackWild();
    testReverseTwoPlayers();
    testIllegalMismatch();
    testRulesValidatorWithNullCalledColor();

    System.out.println("Characterization: " + passed + " passed, " + failed + " failed.");
    if (failed > 0) {
      System.exit(1);
    }
  }

  private static void testCardParsing() {
    var r5 = CardMapper.getCard("R5");
    check("color R5", r5.color() == CardColor.RED);
    check("rank +2", CardMapper.getCard("G+2").rank() == CardRank.DRAW);
    check("draw two value", CardMapper.getCard("G+2").value() == 2);
    check("wild points", CardMapper.getCard("W4").points() == 50);
    check("number card points", CardMapper.getCard("R7").points() == 7);
    check("skip points", CardMapper.getCard("BS").points() == 20);
    check("reverse code", CardMapper.getCard("BR").code().equals("BR"));
    check("wild code", CardMapper.getCard("W").code().equals("W"));
  }

  private static void testWildDrawFourParsing() {
    var w4 = CardMapper.getCard("W4");
    check("W4 is wild color", w4.color() == CardColor.WILD);
    check("W4 is draw rank", w4.rank() == CardRank.DRAW);
    check("W4 draw value", w4.value() == 4);
    check("W4 code string", w4.code().equals("W4"));
    check("R4 stays a number card", CardMapper.getCard("R4").rank() == CardRank.NUMBER);
  }

  private static void testMatchingByColor() {
    check("same color legal",
        RulesValidator.isValid(CardMapper.getCard("R2"), CardMapper.getCard("R9"), null));
    check("different color illegal",
        !RulesValidator.isValid(CardMapper.getCard("G2"), CardMapper.getCard("R9"), null));
  }

  private static void testMatchingByNumber() {
    check("same number legal",
        RulesValidator.isValid(CardMapper.getCard("G9"), CardMapper.getCard("R9"), null));
    check("different number illegal",
        !RulesValidator.isValid(CardMapper.getCard("G8"), CardMapper.getCard("R9"), null));
  }

  private static void testMatchingByActionType() {
    check("skip on skip",
        RulesValidator.isValid(CardMapper.getCard("YS"), CardMapper.getCard("RS"), null));
    check("draw two on draw two",
        RulesValidator.isValid(CardMapper.getCard("B+2"), CardMapper.getCard("G+2"), null));
    check("reverse on reverse",
        RulesValidator.isValid(CardMapper.getCard("YR"), CardMapper.getCard("BR"), null));
  }

  private static void testWildCards() {
    check("wild always legal",
        RulesValidator.isValid(CardMapper.getCard("W"), CardMapper.getCard("R9"), null));
    check("wild draw four always legal",
        RulesValidator.isValid(CardMapper.getCard("W4"), CardMapper.getCard("G+2"), null));
    check("plain wild rank", CardMapper.getCard("W").rank() == CardRank.CHANGE);
  }

  private static void testCalledColorAfterWild() {
    check("called color match",
        RulesValidator.isValid(CardMapper.getCard("B3"), CardMapper.getCard("W"), CardColor.BLUE));
    check("wrong color after call",
        !RulesValidator.isValid(CardMapper.getCard("R3"), CardMapper.getCard("W"), CardColor.BLUE));
  }

  private static void testScoring() {
    int total = CardMapper.getCard("R5").points()
        + CardMapper.getCard("B9").points()
        + CardMapper.getCard("GS").points()
        + CardMapper.getCard("W").points();
    check("example win score from rules", total == 84);
  }

  private static void testBotLogicPriorities() {
    List<Card> hand = List.of(
        CardMapper.getCard("B3"),
        CardMapper.getCard("R4"),
        CardMapper.getCard("W"));
    check("bot prefers matching number over wild",
        BotLogic.chooseCardIndex(hand, CardMapper.getCard("R9"), null) == 1);

    List<Card> hand2 = List.of(
        CardMapper.getCard("R1"),
        CardMapper.getCard("R+2"),
        CardMapper.getCard("R5"));
    check("bot prefers draw two when legal",
        BotLogic.chooseCardIndex(hand2, CardMapper.getCard("G+2"), null) == 1);

    List<Card> hand3 = List.of(
        CardMapper.getCard("B1"),
        CardMapper.getCard("YS"),
        CardMapper.getCard("R5"));
    check("bot prefers skip over number",
        BotLogic.chooseCardIndex(hand3, CardMapper.getCard("RS"), null) == 1);
  }

  private static void testBotColorChoice() {
    List<Card> hand = List.of(
        CardMapper.getCard("B1"),
        CardMapper.getCard("B2"),
        CardMapper.getCard("R3"));
    check("bot picks majority color", BotLogic.chooseColor(hand) == CardColor.BLUE);
  }

  private static void testDeckSize() {
    var deck = new Deck(new Random(0));
    deck.initializeDeck();
    check("standard deck has 108 cards", deck.size() == 108);
  }

  private static void testDeckRecycle() {
    var deck = new Deck(new Random(7));
    deck.initializeDeck();
    var first = deck.draw();
    for (int i = 0; i < 107; i++) {
      deck.discard(deck.draw());
    }
    var afterRecycle = deck.draw();
    check("deck recycles discard pile", afterRecycle != null);
    deck.discard(first);
  }

  private static void testDeckFallbackWild() {
    var deck = new Deck(new Random(1));
    check("empty deck returns wild fallback", deck.draw().code().equals("W"));
  }

  private static void testReverseTwoPlayers() {
    var state = new GameState(new Random(1), 1, false);
    state.initializeState();
    state.reverseDirection();
    state.advancePlayer();
    state.advancePlayer();
    check("two-player double advance stays on same player", state.getCurrentPlayerIndex() == 0);
  }

  private static void testIllegalMismatch() {
    check("no color number or rank match",
        !RulesValidator.isValid(CardMapper.getCard("B3"), CardMapper.getCard("R9"), null));
  }

  private static void testRulesValidatorWithNullCalledColor() {
    var up = CardMapper.getCard("R9");
    check("null called uses up card color",
        RulesValidator.isValid(CardMapper.getCard("R2"), up, null));
  }

  private static void check(String name, boolean condition) {
    if (condition) {
      passed++;
    } else {
      failed++;
      System.err.println("FAIL: " + name);
    }
  }
}