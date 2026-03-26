package com.example.backend.dto.game;

public record TransferWindowStatusResponse(
        String currentDate,
        Integer activeGameweek,
        Integer nextGameweek,
        String nextDeadline,
        boolean transfersAllowed,
        String phase,
        String message) {
}
