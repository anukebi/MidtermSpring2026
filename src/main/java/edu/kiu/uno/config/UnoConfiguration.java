package edu.kiu.uno.config;

import java.io.PrintStream;
import java.util.Random;

import edu.kiu.uno.util.ScannerInputProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.kiu.uno.config.properties.GameProperties;
import edu.kiu.uno.service.game.GameState;

@Configuration
public class UnoConfiguration {

	@Bean
	public Random random(GameProperties properties) {
		return new Random(properties.getSeed());
	}

	@Bean
	public GameState gameState(Random random, GameProperties properties) {
		return new GameState(random, properties.getBots(), properties.isHuman());
	}

	@Bean
	public ScannerInputProvider scannerInputProvider() {
		return new ScannerInputProvider(System.in);
	}

	@Bean
	public PrintStream outputStream() {
		return System.out;
	}

}
