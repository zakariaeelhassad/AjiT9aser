package com.example.backend.dto;

import java.time.LocalDateTime;

/**
 * DTO for match information
 */
public record MatchResponse(
        Long id,
        Integer gameweekNumber,
        String homeTeam,
        String awayTeam,
        Integer homeScore,
        Integer awayScore,
        LocalDateTime kickoffTime,
        Boolean finished) {
}
