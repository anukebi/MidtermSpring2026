package edu.kiu.uno.service.game;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kiu.uno.model.entity.GameEntity;
import edu.kiu.uno.model.entity.PlayerEntity;
import edu.kiu.uno.model.entity.PlayerScoreEntity;
import edu.kiu.uno.model.entity.RoundEntity;
import edu.kiu.uno.model.player.Player;
import edu.kiu.uno.service.GameService;
import edu.kiu.uno.service.PlayerScoreService;
import edu.kiu.uno.service.PlayerService;
import edu.kiu.uno.service.RoundService;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GamePersistenceService {

	private final GameService gameService;
	private final RoundService roundService;
	private final PlayerService playerService;
	private final PlayerScoreService playerScoreService;
	private final GameState gameState;

	@Transactional
	public void startGame(PersistenceContext ctx) {
		ctx.game = gameService.initiateGame();
	}

	public void endGame(PersistenceContext ctx) {
		gameService.endGame(ctx.game);
	}

	@Transactional
	public void startRound(PersistenceContext ctx, int r) {
		ctx.round = roundService.initiateRound(ctx.game, r);
		ctx.playerScores = gameState.getPlayers().stream()
				.map(player -> playerService.getOrCreatePlayer(player.getName()))
				.map(player -> playerScoreService.savePlayerParticipation(ctx.round, player))
				.toList();
	}

	@Transactional
	public void endRound(PersistenceContext ctx, boolean tie) {
		roundService.endRound(ctx.round, tie ? null : getPlayerEntity(ctx, gameState.getCurrentPlayer()));
		gameState.getPlayers().forEach(player -> playerScoreService.updatePlayerParticipation(getPlayerScoreEntity(ctx, player), player.getScore()));
	}

	private PlayerEntity getPlayerEntity(PersistenceContext ctx, Player player) {
		return ctx.playerScores.stream()
				.map(PlayerScoreEntity::getPlayerEntity)
				.filter(p -> p.getName().equals(player.getName()))
				.findFirst().orElseThrow();
	}

	private PlayerScoreEntity getPlayerScoreEntity(PersistenceContext ctx, Player player) {
		return ctx.playerScores.stream()
				.filter(ps -> ps.getPlayerEntity().getName().equals(player.getName()))
				.findFirst().orElseThrow();
	}

	@Data
	public static class PersistenceContext {
		private GameEntity game;
		private RoundEntity round;
		private List<PlayerScoreEntity> playerScores;
	}

}
