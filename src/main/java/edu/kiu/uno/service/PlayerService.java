package edu.kiu.uno.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kiu.uno.model.entity.PlayerEntity;
import edu.kiu.uno.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PlayerService {

	private final PlayerRepository playerRepository;

	@Transactional
	public PlayerEntity getOrCreatePlayer(String Name) {
		return playerRepository.findByName(Name)
				.orElseGet(() -> playerRepository.save(new PlayerEntity().setName(Name)));
	}

}
