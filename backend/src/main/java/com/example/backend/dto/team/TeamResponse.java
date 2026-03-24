package com.example.backend.dto.team;

import com.example.backend.dto.player.PlayerSummary;

import java.math.BigDecimal;
import java.util.List;

public record TeamResponse(
        Long id,
        String teamName,
        String teamImage,
        BigDecimal budget,
        BigDecimal remainingBudget,
        Integer totalPoints,
        List<PlayerSummary> players,
        Integer playerCount) {
}
