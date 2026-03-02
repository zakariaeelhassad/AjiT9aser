package com.example.backend.dto;

import com.example.backend.model.Position;

/**
 * DTO for player performance in a specific gameweek
 * Includes calculated fantasy points based on scoring rules:
 * - Goal: +4 (FWD), +5 (MID), +6 (DEF/GK)
 * - Assist: +3
 * - Yellow Card: -1
 * - Red Card: -3
 */
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
