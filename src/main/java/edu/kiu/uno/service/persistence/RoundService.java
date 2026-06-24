package edu.kiu.uno.service.persistence;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kiu.uno.model.entity.GameEntity;
import edu.kiu.uno.model.entity.PlayerEntity;
import edu.kiu.uno.model.entity.RoundEntity;
import edu.kiu.uno.repository.RoundRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RoundService {

	private final RoundRepository roundRepository;

	@Transactional
	public RoundEntity initiateRound(GameEntity gameEntity, int roundNumber) {
		return roundRepository.save(new RoundEntity().setRoundNumber(roundNumber).setGameEntity(gameEntity).setStartTime(LocalDateTime.now()));
	}

	@Transactional
	public void endRound(RoundEntity roundEntity, PlayerEntity playerEntity) {
		roundRepository.save(roundEntity.setWinner(playerEntity).setEndTime(LocalDateTime.now()));
	}

	@Transactional(readOnly = true)
	public Map<String, Long> getPlayerWinCounts() {
		return roundRepository.findPlayerWinCounts().stream()
				.collect(Collectors.toMap(Pair::getKey, Pair::getValue));
	}

}
