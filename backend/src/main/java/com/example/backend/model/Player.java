package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "players")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Position position;

    @Column(nullable = false, length = 50)
    private String realTeam;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalPoints = 0;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PlayerGameweekStats> gameweekStats = new ArrayList<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserTeamPlayer> userTeams = new ArrayList<>();

    /**
     * Calculate points for a goal based on player position
     * FWD: +4, MID: +5, DEF/GK: +6
     */
    public int getGoalPoints() {
        return switch (position) {
            case FWD -> 4;
            case MID -> 5;
            case DEF, GK -> 6;
        };
    }
}
