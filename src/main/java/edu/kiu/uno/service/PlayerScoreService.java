package edu.kiu.uno.service;

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

}
