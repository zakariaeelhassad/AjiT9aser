package com.example.backend.dto.team;

import java.math.BigDecimal;
import java.util.List;

public record TeamLineupResponse(
        Long teamId,
        String teamName,
        BigDecimal remainingBudget,
        List<TeamLineupPlayerResponse> players) {
}
