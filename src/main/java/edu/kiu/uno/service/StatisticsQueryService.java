package edu.kiu.uno.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StatisticsQueryService {
	private final GameService gameService;
	private final RoundService roundService;
	private final PlayerScoreService playerScoreService;
	private final CliOutputService cliOutputService;

	public enum QueryType {
		RECENT_GAMES,
		PLAYER_SCORES,
		PLAYER_WINS,
		MOST_ACTIVE
	}

	public String query(String query) {
		var queryType = QueryType.valueOf(query.toUpperCase().replace(" ", "_"));
		return cliOutputService.printQueryResult(queryType.name(), switch (queryType) {
			case QueryType.RECENT_GAMES -> gameService.getAllGames();
			case QueryType.PLAYER_SCORES -> playerScoreService.getPlayerScores();
			case QueryType.PLAYER_WINS -> roundService.getPlayerWinCounts();
			case QueryType.MOST_ACTIVE -> playerScoreService.getMostActivePlayer();
			default -> throw new IllegalArgumentException("Unknown query type: " + queryType);
		});
	}

}
