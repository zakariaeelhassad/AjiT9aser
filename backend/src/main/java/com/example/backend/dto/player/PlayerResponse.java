package com.example.backend.dto.player;

import com.example.backend.model.enums.Position;

import java.math.BigDecimal;

public record PlayerResponse(
        Long id,
        String name,
        Position position,
        String realTeam,
        BigDecimal price,
        Integer totalPoints,
        Integer goalPoints) {
}
