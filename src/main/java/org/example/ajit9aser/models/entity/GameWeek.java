package org.example.ajit9aser.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ajit9aser.models.enums.GameWeekStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "game_weeks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameWeek {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Integer weekNumber;
    private LocalDate startDate;
    private LocalDate endDate;


    @Enumerated(EnumType.STRING)
    private GameWeekStatus status;


    @OneToMany(mappedBy = "gameWeek")
    private List<Match> matches;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
