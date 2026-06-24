package edu.kiu.uno.service.persistence;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kiu.uno.model.entity.GameEntity;
import edu.kiu.uno.repository.GameRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GameService {

	private final GameRepository gameRepository;

	@Transactional
	public GameEntity initiateGame() {
		return gameRepository.save(new GameEntity().setStartTime(LocalDateTime.now()));
	}

	@Transactional
	public void endGame(GameEntity game) {
		gameRepository.save(game.setEndTime(LocalDateTime.now()));
	}

	public List<GameEntity> getAllGames() {
		return gameRepository.findAll(Sort.sort(GameEntity.class).by(GameEntity::getStartTime).descending());
	}

}
