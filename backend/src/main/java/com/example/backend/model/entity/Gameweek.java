package com.example.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "gameweeks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Gameweek {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer gameweekNumber;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "UPCOMING"; // UPCOMING, ACTIVE, COMPLETED

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
}
