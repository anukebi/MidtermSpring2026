import uno.BotLogic;
import uno.Card;
import uno.CliInput;
import uno.CliOutput;
import uno.Deck;
import uno.GameEngine;
import uno.GameState;
import uno.RulesValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

    static void main(String[] args) {

        int bots = 3;
        int games = 1;
        boolean human = false;
        long seed = System.currentTimeMillis();
        boolean quiet = false;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--bots") && i + 1 < args.length) {
                bots = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--games") && i + 1 < args.length) {
                games = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--human")) {
                human = true;
            } else if (args[i].equals("--quiet")) {
                quiet = true;
            } else if (args[i].equals("--seed") && i + 1 < args.length) {
                seed = Long.parseLong(args[++i]);
            } else if (args[i].equals("--self-test")) {
                selfTest();
                return;
            } else if (args[i].equals("--help")) {
                System.out.println("Usage: scripts/run.sh [--bots N] [--games N] [--human] [--quiet] [--seed N]");
                return;
            }
        }

        GameState state = new GameState();
        state.setupPlayers(bots, human);

        if (state.playerCount() < 2 || state.playerCount() > 4) {
            System.out.println("UNO needs 2 to 4 players.");
            return;
        }

        CliOutput cliOutput = new CliOutput(quiet);
        CliInput cliInput = new CliInput(new Scanner(System.in));

        Random random = new Random(seed);
        Deck deck = new Deck(random);
        GameEngine engine = new GameEngine(state, deck, random, cliOutput, cliInput);

        for (int g = 1; g <= games; g++) {
            cliOutput.printGameHeader(g);
            engine.playGame();
        }

        cliOutput.printFinalScores(state);
    }

    static void selfTest() {
        int passed = 0;

        if (new Card("R5").color().equals("R")) passed++;
        else fail("color R5");

        if (new Card("G+2").rank().equals("DRAW_TWO")) passed++;
        else fail("rank +2");

        if (new Card("W4").points() == 50) passed++;
        else fail("wild points");

        if (RulesValidator.isValid("R2", "R9", "")) passed++;
        else fail("same color");

        if (RulesValidator.isValid("G9", "R9", "")) passed++;
        else fail("same number");

        if (RulesValidator.isValid("B3", "W", "B")) passed++;
        else fail("called color");

        if (!RulesValidator.isValid("B3", "R9", "")) passed++;
        else fail("illegal mismatch");

        List<String> h = new ArrayList<>();
        h.add("B3");
        h.add("R4");
        h.add("W");
        if (BotLogic.chooseCardIndex(h, "R9", "") == 1) passed++;
        else fail("bot normal before wild");

        List<String> h2 = new ArrayList<>();
        h2.add("B1");
        h2.add("B2");
        h2.add("R3");
        if (BotLogic.chooseColor(h2).equals("B")) passed++;
        else fail("bot color");

        System.out.println("Passed " + passed + " characterization checks.");
    }

    static void fail(String name) {
        throw new RuntimeException("Failed: " + name);
    }

}