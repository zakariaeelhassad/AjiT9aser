package com.example.backend.service;

import com.example.backend.dto.game.GameweekStatsResponse;
import com.example.backend.dto.game.GameweekTotalPointsResponse;
import com.example.backend.dto.game.GameweekTransferCountResponse;
import com.example.backend.dto.team.TeamLineupResponse;
import com.example.backend.model.entity.UserTeam;
import com.example.backend.model.entity.UserTeamPlayer;

import java.util.List;

public interface TeamManagementService {

    UserTeamPlayer addPlayerToSquad(Long userId, Long playerId);

    void removePlayerFromSquad(Long userId, Long playerId);

    UserTeam saveFullSquad(Long userId, List<Long> playerIds);

    UserTeam saveTransfers(Long userId, List<Long> playerIds, int transferCost);

    TeamLineupResponse getTeamLineup(Long userId);

    TeamLineupResponse makeSubstitution(Long userId, Long starterPlayerId, Long benchPlayerId);

    void saveLineup(Long userId, List<Long> starterPlayerIds);

    UserTeam getUserSquad(Long userId);

    com.example.backend.service.Impl.TeamManagementService.SquadStatistics getSquadStatistics(Long userId);

    GameweekStatsResponse getGameweekStats(Long userId, int gameweek);

    List<GameweekTotalPointsResponse> getPersistedGameweekTotals(Long userId);

    GameweekTransferCountResponse getCurrentGameweekTransferCount(Long userId);
}
