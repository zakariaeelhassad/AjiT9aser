package com.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_team_gameweek_transfers", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "team_id", "gameweek_number" })
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTeamGameweekTransfers {

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

    @Column(name = "transfer_count", nullable = false)
    @Builder.Default
    private Integer transferCount = 0;
}
