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
import com.example.backend.repository.PlayerGameweekStatsRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.UserTeamPlayerRepository;
import com.example.backend.repository.UserTeamRepository;
import com.example.backend.dto.GameweekStatsResponse;
import com.example.backend.dto.GameweekStatsResponse.PlayerGameweekScore;
import com.example.backend.dto.LineupPlayerMeta;
import com.example.backend.dto.TeamLineupPlayerResponse;
import com.example.backend.dto.TeamLineupResponse;
import com.example.backend.model.PlayerGameweekStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamManagementService {

        private final UserTeamRepository userTeamRepository;
        private final PlayerRepository playerRepository;
        private final UserRepository userRepository;
        private final UserTeamPlayerRepository userTeamPlayerRepository;
        private final TransferWindowService transferWindowService;
        private final PlayerGameweekStatsRepository statsRepository;

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
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Player not found with id: " + playerId));

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

        @Transactional
        public UserTeam saveFullSquad(Long userId, List<Long> playerIds) {
                log.info("Saving full squad for user {}", userId);

                ensureTransferWindowOpen();

                if (playerIds == null || playerIds.size() != MAX_SQUAD_SIZE) {
                        throw new InvalidTransferException(
                                        "A complete squad must have exactly " + MAX_SQUAD_SIZE + " players.");
                }

                // Get or create user team
                UserTeam userTeam = userTeamRepository.findByUserId(userId)
                                .orElseGet(() -> createDefaultTeam(userId));

                // Fetch all players
                List<Player> players = playerRepository.findAllById(playerIds);
                if (players.size() != MAX_SQUAD_SIZE) {
                        throw new ResourceNotFoundException(
                                        "One or more players not found. Received: " + players.size());
                }

                // Validation: Budget Check
                BigDecimal totalCost = players.stream()
                                .map(Player::getPrice)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (totalCost.compareTo(TOTAL_BUDGET) > 0) {
                        throw new InsufficientBudgetException(
                                        "Total cost exceeds the maximum budget of " + TOTAL_BUDGET);
                }

                // Validation: Team Limit (Max 3 players from same real team)
                Map<String, Long> teamCounts = players.stream()
                                .collect(Collectors.groupingBy(Player::getRealTeam, Collectors.counting()));

                for (Map.Entry<String, Long> entry : teamCounts.entrySet()) {
                        if (entry.getValue() > MAX_PLAYERS_PER_REAL_TEAM) {
                                throw new InvalidTransferException("Cannot have more than " + MAX_PLAYERS_PER_REAL_TEAM
                                                + " players from " + entry.getKey());
                        }
                }

                // Validation: Position Balance
                Map<Position, Long> positionCounts = players.stream()
                                .collect(Collectors.groupingBy(Player::getPosition, Collectors.counting()));

                for (Map.Entry<Position, Integer> entry : POSITION_LIMITS.entrySet()) {
                        long count = positionCounts.getOrDefault(entry.getKey(), 0L);
                        if (count > entry.getValue()) {
                                throw new InvalidTransferException(
                                                "Position limit exceeded for " + entry.getKey() + ".");
                        }
                        if (count < entry.getValue()) {
                                throw new InvalidTransferException(
                                                "Not enough players for position " + entry.getKey() + ".");
                        }
                }

                // Atomic update: Clear and replace
                userTeam.getTeamPlayers().clear();

                // Auto-assign starter status: 1 GK, 4 DEF, 4 MID, 2 FWD = 11 starters
                Map<Position, Integer> starterCounts = new java.util.HashMap<>();
                Map<Position, Integer> starterLimits = Map.of(
                                Position.GK, 1, Position.DEF, 4, Position.MID, 4, Position.FWD, 2);

                for (Player player : players) {
                        int currentCount = starterCounts.getOrDefault(player.getPosition(), 0);
                        int limit = starterLimits.getOrDefault(player.getPosition(), 0);
                        boolean isStarter = currentCount < limit;

                        UserTeamPlayer userTeamPlayer = UserTeamPlayer.builder()
                                        .team(userTeam)
                                        .player(player)
                                        .purchasePrice(player.getPrice())
                                        .starter(isStarter)
                                        .build();
                        userTeam.getTeamPlayers().add(userTeamPlayer);

                        starterCounts.put(player.getPosition(), currentCount + 1);
                }

                userTeam.setRemainingBudget(TOTAL_BUDGET.subtract(totalCost));
                log.info("Successfully updated full squad for user {}", userId);
                return userTeamRepository.save(userTeam);
        }

        @Transactional
        public UserTeam saveTransfers(Long userId, List<Long> playerIds, int transferCost) {
                log.info("Saving transfers for user {} with cost {} pts", userId, transferCost);

                ensureTransferWindowOpen();

                // Reuse the full squad save logic (validates 15 players, budget, positions,
                // team limits)
                UserTeam updatedTeam = saveFullSquad(userId, playerIds);

                // Deduct the transfer cost from totalPoints (cannot go below 0)
                int currentPoints = updatedTeam.getTotalPoints() != null ? updatedTeam.getTotalPoints() : 0;
                int newPoints = Math.max(0, currentPoints - transferCost);
                updatedTeam.setTotalPoints(newPoints);

                log.info("Transfer cost of {} pts deducted. Points updated from {} to {}", transferCost, currentPoints,
                                newPoints);
                return userTeamRepository.save(updatedTeam);
        }

        @Transactional(readOnly = true)
        public TeamLineupResponse getTeamLineup(Long userId) {
                UserTeam userTeam = getDetailedTeam(userId);
                return toLineupResponse(userTeam);
        }

        @Transactional
        public TeamLineupResponse makeSubstitution(Long userId, Long starterPlayerId, Long benchPlayerId) {
                ensureTransferWindowOpen();

                if (starterPlayerId == null || benchPlayerId == null) {
                        throw new InvalidTransferException("Both starter and bench players are required.");
                }

                if (starterPlayerId.equals(benchPlayerId)) {
                        throw new InvalidTransferException("Starter and bench player must be different.");
                }

                UserTeam userTeam = getDetailedTeam(userId);

                UserTeamPlayer starter = userTeam.getTeamPlayers().stream()
                                .filter(tp -> tp.getPlayer().getId().equals(starterPlayerId))
                                .findFirst()
                                .orElseThrow(() -> new InvalidTransferException("Starter player is not in your squad."));

                UserTeamPlayer bench = userTeam.getTeamPlayers().stream()
                                .filter(tp -> tp.getPlayer().getId().equals(benchPlayerId))
                                .findFirst()
                                .orElseThrow(() -> new InvalidTransferException("Bench player is not in your squad."));

                if (!starter.isStarter()) {
                        throw new InvalidTransferException("Selected starter player is currently on the bench.");
                }

                if (bench.isStarter()) {
                        throw new InvalidTransferException("Selected bench player is currently in the starting lineup.");
                }

                if (starter.getPlayer().getPosition() != bench.getPlayer().getPosition()) {
                        throw new InvalidTransferException("Substitutions are only allowed between players of the same position.");
                }

                starter.setStarter(false);
                bench.setStarter(true);

                userTeamRepository.save(userTeam);
                return toLineupResponse(userTeam);
        }

        @Transactional
        public void saveLineup(Long userId, List<Long> starterPlayerIds) {
                ensureTransferWindowOpen();

                if (starterPlayerIds == null || starterPlayerIds.size() != 11) {
                        throw new InvalidTransferException("Starting lineup must contain exactly 11 players.");
                }

                Set<Long> uniqueStarterIds = new HashSet<>(starterPlayerIds);
                if (uniqueStarterIds.size() != 11) {
                        throw new InvalidTransferException("Starting lineup contains duplicate players.");
                }

                Long teamId = userTeamRepository.findTeamIdByUserId(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User team not found"));

                List<LineupPlayerMeta> lineupMeta = userTeamPlayerRepository.findLineupMetaByTeamId(teamId);
                if (lineupMeta.size() != MAX_SQUAD_SIZE) {
                        throw new InvalidTransferException("A complete 15-player squad is required before saving lineup.");
                }

                Set<Long> squadPlayerIds = lineupMeta.stream()
                                .map(LineupPlayerMeta::playerId)
                                .collect(Collectors.toSet());

                if (!squadPlayerIds.containsAll(uniqueStarterIds)) {
                        throw new InvalidTransferException("One or more selected starters are not in your squad.");
                }

                int gkCount = 0;
                int defCount = 0;
                int fwdCount = 0;

                for (LineupPlayerMeta meta : lineupMeta) {
                        if (!uniqueStarterIds.contains(meta.playerId())) {
                                continue;
                        }
                        if (meta.position() == Position.GK) {
                                gkCount++;
                        } else if (meta.position() == Position.DEF) {
                                defCount++;
                        } else if (meta.position() == Position.FWD) {
                                fwdCount++;
                        }
                }

                if (gkCount != 1) {
                        throw new InvalidTransferException("Starting lineup must include exactly 1 goalkeeper.");
                }

                if (defCount < 3) {
                        throw new InvalidTransferException("Starting lineup must include at least 3 defenders.");
                }

                if (fwdCount < 1) {
                        throw new InvalidTransferException("Starting lineup must include at least 1 attacker.");
                }

                userTeamPlayerRepository.clearStartersByTeamId(teamId);
                userTeamPlayerRepository.setStartersByTeamId(teamId, starterPlayerIds);
        }

        private UserTeam getDetailedTeam(Long userId) {
                return userTeamRepository.findDetailedByUserId(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User team not found"));
        }

        private TeamLineupResponse toLineupResponse(UserTeam userTeam) {
                List<TeamLineupPlayerResponse> players = userTeam.getTeamPlayers().stream()
                                .sorted(Comparator
                                                .comparing(UserTeamPlayer::isStarter).reversed()
                                                .thenComparing(tp -> tp.getPlayer().getPosition().name())
                                                .thenComparing(tp -> tp.getPlayer().getName()))
                                .map(tp -> new TeamLineupPlayerResponse(
                                                tp.getPlayer().getId(),
                                                tp.getPlayer().getName(),
                                                tp.getPlayer().getPosition(),
                                                tp.getPlayer().getRealTeam(),
                                                tp.getPlayer().getPrice(),
                                                tp.getPlayer().getTotalPoints(),
                                                tp.isStarter()))
                                .toList();

                return new TeamLineupResponse(
                                userTeam.getId(),
                                userTeam.getTeamName(),
                                userTeam.getRemainingBudget(),
                                players);
        }

        private void ensureTransferWindowOpen() {
                TransferWindowService.TransferWindowStatus status = transferWindowService.getTransferWindowStatus();
                if (!status.transfersAllowed()) {
                        if (status.activeGameweek() != null) {
                                throw new InvalidTransferException(
                                                "Transfers are locked. We are currently in Gameweek "
                                                                + status.activeGameweek() + ".");
                        }
                        throw new InvalidTransferException("Transfers are currently locked.");
                }
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

        @Transactional(readOnly = true)
        public GameweekStatsResponse getGameweekStats(Long userId, int gameweek) {
                UserTeam userTeam = userTeamRepository.findByUserId(userId)
                                .orElseGet(() -> createDefaultTeam(userId));

                // Map each player to their score for this gameweek (includes bench)
                List<PlayerGameweekScore> playerScores = userTeam.getTeamPlayers().stream().map(tp -> {
                        int points = statsRepository.findByPlayerIdAndGameweekNumber(tp.getPlayer().getId(), gameweek)
                                        .map(PlayerGameweekStats::getPointsEarned)
                                        .orElse(0);
                        return new PlayerGameweekScore(tp.getPlayer().getId(), tp.getPlayer().getName(), points,
                                        tp.isStarter());
                }).toList();

                // teamPoints = ONLY starters
                int teamPoints = playerScores.stream()
                                .filter(PlayerGameweekScore::isStarter)
                                .mapToInt(PlayerGameweekScore::getPoints)
                                .sum();

                // Global highest = ONLY starters for each team
                int globalHighestPoints = 0;
                List<UserTeam> allTeams = userTeamRepository.findAll();
                for (UserTeam team : allTeams) {
                        int currentPoints = team.getTeamPlayers().stream()
                                        .filter(UserTeamPlayer::isStarter)
                                        .mapToInt(tp -> statsRepository
                                                        .findByPlayerIdAndGameweekNumber(tp.getPlayer().getId(),
                                                                        gameweek)
                                                        .map(PlayerGameweekStats::getPointsEarned)
                                                        .orElse(0))
                                        .sum();
                        if (currentPoints > globalHighestPoints) {
                                globalHighestPoints = currentPoints;
                        }
                }

                return new GameweekStatsResponse(
                                gameweek,
                                teamPoints,
                                globalHighestPoints,
                                playerScores);
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
