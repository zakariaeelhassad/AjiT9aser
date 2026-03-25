package com.example.backend.controller;

import com.example.backend.dto.match.CurrentGameweekResponse;
import com.example.backend.dto.match.GameweekSummaryResponse;
import com.example.backend.dto.match.MatchStatusResponse;
import com.example.backend.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
@Tag(name = "Matches", description = "Gameweek fixtures with simulated live status and events")
public class MatchController {

        private final MatchService matchService;

        @GetMapping("/gameweek/{gameweekNumber}")
        @Operation(summary = "All fixtures for a given gameweek with live status and events")
        public ResponseEntity<List<MatchStatusResponse>> getByGameweek(
                        @PathVariable Integer gameweekNumber) {
                return matchService.getByGameweek(gameweekNumber)
                                .map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @GetMapping("/live")
        @Operation(summary = "All currently LIVE matches across any gameweek")
        public ResponseEntity<List<MatchStatusResponse>> getLive() {
                return ResponseEntity.ok(matchService.getLive());
        }

        @GetMapping("/gameweeks")
        @Operation(summary = "All gameweek numbers with match summary")
        public ResponseEntity<List<GameweekSummaryResponse>> getGameweeks() {
                return ResponseEntity.ok(matchService.getGameweeks());
        }

        @GetMapping("/clock")
        @Operation(summary = "Current simulated time")
        public ResponseEntity<Map<String, String>> getClock() {
                return ResponseEntity.ok(matchService.getClock());
        }

        @GetMapping("/current")
        @Operation(summary = "Current backend date with DB-derived current gameweek and its fixtures")
        public ResponseEntity<CurrentGameweekResponse> getCurrentGameweekFromDb() {
                return ResponseEntity.ok(matchService.getCurrentGameweekFromDb());
        }
}

