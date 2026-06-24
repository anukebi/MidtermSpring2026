package edu.kiu.uno.service.persistence;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kiu.uno.model.entity.PlayerEntity;
import edu.kiu.uno.model.entity.PlayerScoreEntity;
import edu.kiu.uno.model.entity.RoundEntity;
import edu.kiu.uno.repository.PlayerScoreRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PlayerScoreService {

	private final PlayerScoreRepository playerScoreRepository;

	@Transactional
	public PlayerScoreEntity savePlayerParticipation(RoundEntity roundEntity, PlayerEntity playerEntity) {
		return playerScoreRepository.save(new PlayerScoreEntity().setRoundEntity(roundEntity).setPlayerEntity(playerEntity).setScore(0));
	}

	@Transactional
	public void updatePlayerParticipation(PlayerScoreEntity playerScoreEntity, long score) {
		playerScoreRepository.save(playerScoreEntity.setScore(score));
	}

	@Transactional(readOnly = true)
	public PlayerEntity getMostActivePlayer() {
		return playerScoreRepository.findMostActivePlayer().findFirst().orElse(null);
	}

	@Transactional(readOnly = true)
	public Map<String, Long> getPlayerScores() {
		return playerScoreRepository.findPlayerScores().stream()
				.collect(Collectors.toMap(Pair::getKey, Pair::getValue));
	}

}
