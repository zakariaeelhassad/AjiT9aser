package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_team_gameweek_lineups", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "team_id", "gameweek_number" })
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTeamGameweekLineup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private UserTeam team;

    @ManyToOne
    @JoinColumn(name = "gameweek_id")
    private Gameweek gameweek;

    @Column(name = "gameweek_number", nullable = false)
    private Integer gameweekNumber;

    @Column(name = "team_points", nullable = false)
    @Builder.Default
    private Integer teamPoints = 0;

    @Column(name = "captured_at", nullable = false)
    @Builder.Default
    private LocalDateTime capturedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "lineup", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserTeamGameweekLineupPlayer> players = new ArrayList<>();
}
