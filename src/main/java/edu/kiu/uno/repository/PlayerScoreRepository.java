package edu.kiu.uno.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.kiu.uno.model.entity.PlayerEntity;
import edu.kiu.uno.model.entity.PlayerScoreEntity;
import edu.kiu.uno.model.entity.RoundEntity;

@Repository
public interface PlayerScoreRepository extends JpaRepository<PlayerScoreEntity, UUID> {

	PlayerScoreEntity findByRoundAndPlayer(RoundEntity round, PlayerEntity playerEntity);

}
