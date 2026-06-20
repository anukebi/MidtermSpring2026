package edu.kiu.uno.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.kiu.uno.model.entity.GameEntity;

@Repository
public interface GameRepository extends JpaRepository<GameEntity, UUID> {

}
