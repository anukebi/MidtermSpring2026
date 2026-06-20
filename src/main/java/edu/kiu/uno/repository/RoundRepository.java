package edu.kiu.uno.repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.kiu.uno.model.entity.PlayerEntity;
import edu.kiu.uno.model.entity.RoundEntity;

@Repository
public interface RoundRepository extends JpaRepository<RoundEntity, UUID> {

	@Query("SELECT new org.apache.commons.lang3.tuple.ImmutablePair(p.name, COUNT(r)) FROM RoundEntity r JOIN r.winner p GROUP BY p.name")
	List<Pair<String, Long>> findPlayerWinCounts();
}
