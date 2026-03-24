package com.example.backend.dto.team;

import com.example.backend.model.enums.Position;

import java.math.BigDecimal;

public record TeamLineupPlayerResponse(
        Long id,
        String name,
        Position position,
        String realTeam,
        BigDecimal price,
        Integer totalPoints,
        boolean starter) {
}
