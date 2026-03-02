package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_team_players")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTeamPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private UserTeam team;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime selectedAt = LocalDateTime.now();

    /**
     * Composite unique constraint: one player can only be in a team once
     */
    @Table(uniqueConstraints = {
            @UniqueConstraint(columnNames = { "team_id", "player_id" })
    })
    public static class Constraints {
    }
}
