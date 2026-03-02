package com.example.backend.service;

import com.example.backend.exception.InsufficientBudgetException;
import com.example.backend.exception.InvalidTransferException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.exception.TeamFullException;
import com.example.backend.model.Player;
import com.example.backend.model.Position;
import com.example.backend.model.User;
import com.example.backend.model.UserTeam;
import com.example.backend.model.UserTeamPlayer;
import com.example.backend.repository.PlayerRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.UserTeamPlayerRepository;
import com.example.backend.repository.UserTeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamManagementService {

    private final UserTeamRepository userTeamRepository;
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final UserTeamPlayerRepository userTeamPlayerRepository;

    private static final int MAX_SQUAD_SIZE = 15;
    private static final int MAX_PLAYERS_PER_REAL_TEAM = 3;
    private static final BigDecimal TOTAL_BUDGET = BigDecimal.valueOf(100.0);

    // Position limits: 2 GK, 5 DEF, 5 MID, 3 FWD
    private static final Map<Position, Integer> POSITION_LIMITS = Map.of(
            Position.GK, 2,
            Position.DEF, 5,
            Position.MID, 5,
            Position.FWD, 3);

    @Transactional
    public UserTeamPlayer addPlayerToSquad(Long userId, Long playerId) {
        log.info("Adding player {} to user {} squad", playerId, userId);

        // Get or create user team
        UserTeam userTeam = userTeamRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultTeam(userId));

        // Get player
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + playerId));

        // Validation 1: Squad Size (Max 15 players)
        if (userTeam.getTeamPlayers().size() >= MAX_SQUAD_SIZE) {
            throw new TeamFullException("Squad is full. Maximum " + MAX_SQUAD_SIZE + " players allowed.");
        }

        // Validation 2: Budget Check
        BigDecimal newRemainingBudget = userTeam.getRemainingBudget().subtract(player.getPrice());
        if (newRemainingBudget.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBudgetException(
                    String.format("Insufficient budget. Player costs %.1f, remaining budget: %.1f",
                            player.getPrice().doubleValue(),
                            userTeam.getRemainingBudget().doubleValue()));
        }

        // Validation 3: Team Limit (Max 3 players from same real team)
        long playersFromSameTeam = userTeam.getTeamPlayers().stream()
                .filter(tp -> tp.getPlayer().getRealTeam().equals(player.getRealTeam()))
                .count();

        if (playersFromSameTeam >= MAX_PLAYERS_PER_REAL_TEAM) {
            throw new InvalidTransferException(
                    String.format("Cannot have more than %d players from %s",
                            MAX_PLAYERS_PER_REAL_TEAM, player.getRealTeam()));
        }

        // Validation 4: Position Balance (2 GK, 5 DEF, 5 MID, 3 FWD)
        Map<Position, Long> currentPositionCounts = userTeam.getTeamPlayers().stream()
                .collect(Collectors.groupingBy(
                        tp -> tp.getPlayer().getPosition(),
                        Collectors.counting()));

        long currentCountForPosition = currentPositionCounts.getOrDefault(player.getPosition(), 0L);
        int maxForPosition = POSITION_LIMITS.get(player.getPosition());

        if (currentCountForPosition >= maxForPosition) {
            throw new InvalidTransferException(
                    String.format("Position limit reached. Maximum %d %s allowed.",
                            maxForPosition, player.getPosition()));
        }

        // Check for duplicate player
        boolean alreadyInTeam = userTeam.getTeamPlayers().stream()
                .anyMatch(tp -> tp.getPlayer().getId().equals(playerId));

        if (alreadyInTeam) {
            throw new InvalidTransferException("Player already in your squad");
        }

        // All validations passed - add player to squad
        UserTeamPlayer userTeamPlayer = UserTeamPlayer.builder()
                .team(userTeam)
                .player(player)
                .purchasePrice(player.getPrice())
                .build();

        userTeam.getTeamPlayers().add(userTeamPlayer);
        userTeam.setRemainingBudget(newRemainingBudget);

        userTeamRepository.save(userTeam);
        log.info("Successfully added player {} to squad. Remaining budget: {}",
                player.getName(), newRemainingBudget);

        return userTeamPlayer;
    }

    @Transactional
    public void removePlayerFromSquad(Long userId, Long playerId) {
        log.info("Removing player {} from user {} squad", playerId, userId);

        UserTeam userTeam = userTeamRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User team not found"));

        UserTeamPlayer playerToRemove = userTeam.getTeamPlayers().stream()
                .filter(tp -> tp.getPlayer().getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Player not in your squad"));

        // Refund the purchase price
        BigDecimal refund = playerToRemove.getPurchasePrice();
        userTeam.setRemainingBudget(userTeam.getRemainingBudget().add(refund));

        userTeam.getTeamPlayers().remove(playerToRemove);
        userTeamRepository.save(userTeam);

        log.info("Successfully removed player. Refunded: {}", refund);
    }

    @Transactional(readOnly = true)
    public UserTeam getUserSquad(Long userId) {
        return userTeamRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User team not found"));
    }

    @Transactional(readOnly = true)
    public SquadStatistics getSquadStatistics(Long userId) {
        UserTeam userTeam = userTeamRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultTeam(userId));

        Map<Position, Long> positionCounts = userTeam.getTeamPlayers().stream()
                .collect(Collectors.groupingBy(
                        tp -> tp.getPlayer().getPosition(),
                        Collectors.counting()));

        Map<String, Long> teamCounts = userTeam.getTeamPlayers().stream()
                .collect(Collectors.groupingBy(
                        tp -> tp.getPlayer().getRealTeam(),
                        Collectors.counting()));

        return SquadStatistics.builder()
                .totalPlayers(userTeam.getTeamPlayers().size())
                .remainingBudget(userTeam.getRemainingBudget())
                .totalPoints(userTeam.getTotalPoints())
                .goalkeepers(positionCounts.getOrDefault(Position.GK, 0L).intValue())
                .defenders(positionCounts.getOrDefault(Position.DEF, 0L).intValue())
                .midfielders(positionCounts.getOrDefault(Position.MID, 0L).intValue())
                .forwards(positionCounts.getOrDefault(Position.FWD, 0L).intValue())
                .teamCounts(teamCounts)
                .isComplete(userTeam.getTeamPlayers().size() == MAX_SQUAD_SIZE)
                .build();
    }

    private UserTeam createDefaultTeam(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserTeam userTeam = UserTeam.builder()
                .user(user)
                .teamName(user.getUsername() + "'s Team")
                .budget(TOTAL_BUDGET)
                .remainingBudget(TOTAL_BUDGET)
                .totalPoints(0)
                .build();

        return userTeamRepository.save(userTeam);
    }

    @lombok.Data
    @lombok.Builder
    public static class SquadStatistics {
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
}
