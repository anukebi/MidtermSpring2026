package edu.kiu.uno.service;

import java.time.LocalDateTime;

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

}
