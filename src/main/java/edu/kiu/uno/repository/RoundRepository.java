package edu.kiu.uno.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.kiu.uno.model.entity.PlayerEntity;
import edu.kiu.uno.model.entity.RoundEntity;

@Repository
public interface RoundRepository extends JpaRepository<RoundEntity, UUID> {

}
