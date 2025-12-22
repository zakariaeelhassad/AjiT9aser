package org.example.ajit9aser.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "fantasy_teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FantasyTeam {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;


    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;


    private Integer totalPoints;


    @OneToMany(mappedBy = "fantasyTeam", cascade = CascadeType.ALL)
    private List<FantasyTeamPlayer> players;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}