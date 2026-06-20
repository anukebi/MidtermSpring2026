package edu.kiu.uno;

import java.util.Random;
import java.util.Scanner;

import edu.kiu.uno.game.Deck;
import edu.kiu.uno.game.GameConfig;
import edu.kiu.uno.game.GameEngine;
import edu.kiu.uno.game.GameState;
import edu.kiu.uno.io.CliInput;
import edu.kiu.uno.io.CliOutput;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {

    public static void main(String[] args) {

        var config = new GameConfig(args);
        if (config.isHelp()) {
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

        log.info("main:: Starting UNO with config: {}", config);
        for (int g = 1; g <= config.getGames(); g++) {
            log.info("main:: Starting game {} of {}", g, config.getGames());
            cliOutput.printGameHeader(g);
            engine.playGame();
        }

        log.info("main:: All games completed. Final scores: {}", state.getPlayers().stream().map(p -> p.getName() + ": " + p.getScore()).toList());
        cliOutput.printFinalScores(state);
    }

}