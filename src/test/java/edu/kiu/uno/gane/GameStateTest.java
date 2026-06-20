package edu.kiu.uno.gane;

import static edu.kiu.uno.TestUtils.check;

import java.util.Random;

import org.junit.jupiter.api.Test;

import edu.kiu.uno.service.game.GameState;

public class GameStateTest {

  @Test
	void testReverseTwoPlayers() {
		var state = new GameState(new Random(1), 1, false);
		state.initializeState();
		state.reverseDirection();
		state.advancePlayer();
		state.advancePlayer();
		check("two-player double advance stays on same player", state.getCurrentPlayerIndex() == 0);
	}

	@Test
	void testDrawStacking() {
		var state = new GameState(new Random(1), 2, false);
		state.initializeState();
		state.startPendingDraw(2);
		state.addPendingDraw(2);
		check("pending draw accumulates", state.getPendingDraw() == 4);
		state.clearPendingDraw();
		check("pending draw clears", state.getPendingDraw() == 0);
	}

}
