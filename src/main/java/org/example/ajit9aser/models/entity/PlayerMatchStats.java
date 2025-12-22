package org.example.ajit9aser.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "player_match_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerMatchStats {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;


    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;


    private Integer minutesPlayed;
    private Integer goals;
    private Integer assists;
    private Boolean cleanSheet;
    private Integer yellowCards;
    private Integer redCards;
    private Integer pointsCalculated;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}