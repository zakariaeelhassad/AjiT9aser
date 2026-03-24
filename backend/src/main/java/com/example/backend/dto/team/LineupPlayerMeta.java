package com.example.backend.dto.team;

import com.example.backend.model.enums.Position;

public record LineupPlayerMeta(
        Long playerId,
        Position position) {
}
