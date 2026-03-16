package com.example.backend.dto;

import com.example.backend.model.Position;

public record PlayerStatsResponse(
        Long id,
        Long playerId,
        String playerName,
        Position position,
        Integer gameweekNumber,
        String matchDetails,
        Integer minutesPlayed,
        Integer goals,
        Integer assists,
        Integer yellowCards,
        Integer redCards,
        Integer pointsEarned) {
}
