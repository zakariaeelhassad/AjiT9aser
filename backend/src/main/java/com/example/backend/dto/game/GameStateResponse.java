package com.example.backend.dto.game;

public record GameStateResponse(
        boolean engineRunning,
        boolean matchInProgress,
        boolean gameweekActive,
        int currentGameweek,
        int currentMatchIndex,
        int currentMinute,
        String homeTeam,
        String awayTeam,
        int homeScore,
        int awayScore) {
}
