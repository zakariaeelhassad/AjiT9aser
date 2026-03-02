package com.example.backend.dto;

/**
 * A single match event (goal, assist, yellow/red card) with its simulated
 * minute.
 */
public record MatchEventDTO(
        /** GOAL | ASSIST | YELLOW_CARD | RED_CARD */
        String type,
        String player,
        String team,
        /** Simulated match minute (1-90) at which this event fires */
        int minute) {
}
