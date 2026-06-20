package edu.kiu.uno.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.kiu.uno.model.entity.GameEntity;
import edu.kiu.uno.model.entity.PlayerEntity;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, UUID> {

	Optional<PlayerEntity> findByName(String name);

}
