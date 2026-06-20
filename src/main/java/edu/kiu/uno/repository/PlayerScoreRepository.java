package edu.kiu.uno.repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.kiu.uno.model.entity.PlayerEntity;
import edu.kiu.uno.model.entity.PlayerScoreEntity;

@Repository
public interface PlayerScoreRepository extends JpaRepository<PlayerScoreEntity, UUID> {

	@Query("SELECT ps.playerEntity FROM PlayerScoreEntity ps GROUP BY ps.playerEntity ORDER BY SUM(ps.score) DESC")
	Stream<PlayerEntity> findMostActivePlayer();

	@Query("SELECT  new org.apache.commons.lang3.tuple.ImmutablePair(ps.playerEntity.name, SUM(ps.score)) FROM PlayerScoreEntity ps GROUP BY ps.playerEntity ORDER BY SUM(ps.score) DESC")
	List<Pair<String, Long>> findPlayerScores();
}
