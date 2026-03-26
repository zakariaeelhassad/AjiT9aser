package com.example.backend.dto.team;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class SquadStatisticsResponse {
    private int totalPlayers;
    private BigDecimal remainingBudget;
    private int totalPoints;
    private int goalkeepers;
    private int defenders;
    private int midfielders;
    private int forwards;
    private Map<String, Long> teamCounts;
    private boolean isComplete;
}
