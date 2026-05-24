import uno.game.GameConfig;
import uno.rule.BotLogic;
import uno.model.CardMapper;
import uno.io.CliInput;
import uno.io.CliOutput;
import uno.model.card.Card;
import uno.game.Deck;
import uno.game.GameEngine;
import uno.game.GameState;
import uno.model.card.CardColor;
import uno.model.card.CardRank;
import uno.rule.RulesValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        var config = new GameConfig(args);
        if (config.isSelfTest()) {
            selfTest();
            return;
        } else if (config.isHelp()) {
            System.out.println("Usage: scripts/run.sh [--bots N] [--games N] [--human] [--quiet] [--seed N]");
            return;
        }

        var random = new Random(config.getSeed());
        var state = new GameState(random, config.getBots(), config.isHuman());
        if (state.playerCount() < 2 || state.playerCount() > 4) {
            System.out.println("UNO needs 2 to 4 players.");
            return;
        }

        var cliOutput = new CliOutput(config.isQuiet());
        var cliInput = new CliInput(new Scanner(System.in));

        var deck = new Deck(random);
        var engine = new GameEngine(state, deck, cliOutput, cliInput);

        for (int g = 1; g <= config.getGames(); g++) {
            cliOutput.printGameHeader(g);
            engine.playGame();
        }

        cliOutput.printFinalScores(state);
    }

    static void selfTest() {
        int passed = 0;

        if (CardMapper.getCard("R5").color().equals(CardColor.RED)) passed++;
        else fail("color R5");

        if (CardMapper.getCard("G+2").rank().equals(CardRank.DRAW)) passed++;
        else fail("rank +2");

        if (CardMapper.getCard("G+2").value().equals(2)) passed++;
        else fail("rank +2");

        if (CardMapper.getCard("W4").points() == 50) passed++;
        else fail("wild points");

        if (RulesValidator.isValid(CardMapper.getCard("R2"), CardMapper.getCard("R9"), null)) passed++;
        else fail("same color");

        if (RulesValidator.isValid(CardMapper.getCard("G9"), CardMapper.getCard("R9"), null)) passed++;
        else fail("same number");

        if (RulesValidator.isValid(CardMapper.getCard("B3"), CardMapper.getCard("W"), CardColor.BLUE)) passed++;
        else fail("called color");

        if (!RulesValidator.isValid(CardMapper.getCard("B3"), CardMapper.getCard("R9"), null)) passed++;
        else fail("illegal mismatch");

        List<Card> h = new ArrayList<>();
        h.add(CardMapper.getCard("B3"));
        h.add(CardMapper.getCard("R4"));
        h.add(CardMapper.getCard("W"));
        if (BotLogic.chooseCardIndex(h, CardMapper.getCard("R9"), null, 0) == 1) passed++;
        else fail("bot normal before wild");

        List<Card> h2 = new ArrayList<>();
        h2.add(CardMapper.getCard("B1"));
        h2.add(CardMapper.getCard("B2"));
        h2.add(CardMapper.getCard("R3"));
        if (BotLogic.chooseColor(h2).equals(CardColor.BLUE)) passed++;
        else fail("bot color");

        System.out.println("Passed " + passed + " characterization checks.");
    }

    static void fail(String name) {
        throw new RuntimeException("Failed: " + name);
    }

}