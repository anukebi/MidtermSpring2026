package edu.kiu.uno.model.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
@Entity
@Table(name = "rounds")
public class RoundEntity {

	@Id
	private UUID id;

	@Column(name = "round_number", nullable = false)
	private int roundNumber;

	@ManyToOne
	@JoinColumn(name = "game_id", referencedColumnName = "id", nullable = false)
	private GameEntity gameEntity;

	@ManyToOne
	@JoinColumn(name = "winner_id", referencedColumnName = "id", nullable = false)
	private PlayerEntity winner;

	@Column(name = "start_time", nullable = false)
	private LocalDateTime startTime;

	@Column(name = "end_time")
	private LocalDateTime endTime;

}
