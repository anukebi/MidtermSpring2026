package edu.kiu.uno;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import edu.kiu.uno.config.properties.GameProperties;
import edu.kiu.uno.service.StatisticsQueryService;
import edu.kiu.uno.service.game.GameEngine;
import edu.kiu.uno.service.game.GameState;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@SpringBootApplication
public class UnoApplication implements CommandLineRunner {

    private final GameProperties gameProperties;
    private final GameState gameState;
    private final GameEngine gameEngine;
    private final StatisticsQueryService statisticsQueryService;

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

        if (StringUtils.isNotBlank(gameProperties.getQuery())) {
            statisticsQueryService.query(gameProperties.getQuery());
            System.exit(0);
            return;
        }

        if (gameState.playerCount() < 2 || gameState.playerCount() > 4) {
            System.out.println("UNO needs 2 to 4 players.");
            System.exit(1);
            return;
        }

        gameEngine.playGame();
        System.exit(0);
    }

}