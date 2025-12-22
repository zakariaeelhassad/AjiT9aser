package org.example.ajit9aser.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_week_results",
        uniqueConstraints = @UniqueConstraint(columnNames = {"fantasy_team_id", "game_week_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameWeekResult {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "fantasy_team_id")
    private FantasyTeam fantasyTeam;


    @ManyToOne
    @JoinColumn(name = "game_week_id")
    private GameWeek gameWeek;


    private Integer points;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
