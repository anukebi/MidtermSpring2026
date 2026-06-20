package edu.kiu.uno;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

import edu.kiu.uno.config.properties.GameProperties;
import edu.kiu.uno.service.game.GameEngine;
import edu.kiu.uno.service.game.GameState;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class UnoApplication implements CommandLineRunner {

    private final GameProperties gameProperties;
    private final GameState gameState;
    private final GameEngine gameEngine;

    public static void main(String[] args) {
        SpringApplication.run(UnoApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (gameProperties.isHelp()) {
            System.out.println("Usage: scripts/run.sh [--bots N] [--games N] [--human] [--quiet] [--seed N]");
            System.exit(0);
            return;
        }

        if (gameState.playerCount() < 2 || gameState.playerCount() > 4) {
            System.out.println("UNO needs 2 to 4 players.");
            System.exit(1);
            return;
        }

        log.info("run:: Starting UNO with config: {}", gameProperties);
        for (int g = 1; g <= gameProperties.getGames(); g++) {
            log.info("run:: Starting game {} of {}", g, gameProperties.getGames());
            gameEngine.playGame(g);
        }
        log.info("run:: All games completed. Final scores: {}", gameState.getPlayers().stream().map(p -> p.getName() + ": " + p.getScore()).toList());
        System.exit(0);
    }

}