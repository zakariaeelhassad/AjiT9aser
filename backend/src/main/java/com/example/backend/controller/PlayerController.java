package com.example.backend.controller;

import com.example.backend.dto.PlayerResponse;
import com.example.backend.dto.PlayerSummary;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.Player;
import com.example.backend.model.Position;
import com.example.backend.repository.PlayerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
@Tag(name = "Players", description = "Browse and search fantasy-eligible players")
public class PlayerController {

    private final PlayerRepository playerRepository;

    @GetMapping
    @Operation(summary = "Get all players (paginated)", description = "Filter by position and/or team. Sort by price, totalPoints, or name.")
    public ResponseEntity<Page<PlayerSummary>> getPlayers(
            @RequestParam(required = false) Position position,
            @RequestParam(required = false) String team,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "totalPoints") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        List<Player> all = playerRepository.findAll();

        // Apply filters
        List<Player> filtered = all.stream()
                .filter(p -> position == null || p.getPosition() == position)
                .filter(p -> team == null || p.getRealTeam().equalsIgnoreCase(team))
                .filter(p -> search == null ||
                        p.getName().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<PlayerSummary> pageContent = filtered.subList(start > filtered.size() ? filtered.size() : start, end)
                .stream()
                .map(this::toSummary)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new PageImpl<>(pageContent, pageable, filtered.size()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single player by ID")
    public ResponseEntity<PlayerResponse> getPlayer(@PathVariable Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found: " + id));
        return ResponseEntity.ok(toResponse(player));
    }

    @GetMapping("/search")
    @Operation(summary = "Search players by name")
    public ResponseEntity<List<PlayerSummary>> searchPlayers(
            @RequestParam String q,
            @RequestParam(defaultValue = "20") int limit) {
        List<PlayerSummary> results = playerRepository.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(q.toLowerCase()))
                .limit(limit)
                .map(this::toSummary)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/teams")
    @Operation(summary = "Get all distinct real-world team names")
    public ResponseEntity<List<String>> getTeams() {
        List<String> teams = playerRepository.findAll().stream()
                .map(Player::getRealTeam)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        return ResponseEntity.ok(teams);
    }

    private PlayerSummary toSummary(Player p) {
        return new PlayerSummary(p.getId(), p.getName(), p.getPosition(),
                p.getRealTeam(), p.getPrice(), p.getTotalPoints());
    }

    private PlayerResponse toResponse(Player p) {
        return new PlayerResponse(p.getId(), p.getName(), p.getPosition(),
                p.getRealTeam(), p.getPrice(), p.getTotalPoints(), p.getGoalPoints());
    }
}
