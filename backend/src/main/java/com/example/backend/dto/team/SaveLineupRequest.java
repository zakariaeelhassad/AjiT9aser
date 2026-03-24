package com.example.backend.dto.team;

import java.util.List;

public record SaveLineupRequest(
        List<Long> starterPlayerIds) {
}
