package edu.kiu.uno.model.entity;

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
@Table(name = "player_scores")
public class PlayerScoreEntity {

	@Id
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "round_id", referencedColumnName = "id", nullable = false)
	private RoundEntity roundEntity;

	@ManyToOne
	@JoinColumn(name = "player_id", referencedColumnName = "id", nullable = false)
	private PlayerEntity playerEntity;

	@Column(name = "score", nullable = false)
	private long score;

}
