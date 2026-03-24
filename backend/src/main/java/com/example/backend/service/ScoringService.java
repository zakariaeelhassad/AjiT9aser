package com.example.backend.service;

import com.example.backend.model.entity.UserTeam;

public interface ScoringService {

    void processGoal(String playerName, String team, int gameweek);

    void processAssist(String playerName, String team, int gameweek);

    void processYellowCard(String playerName, String team, int gameweek);

    void processRedCard(String playerName, String team, int gameweek);

    void processCleanSheet(String team, int gameweek);

    void processMinutesPlayed(String playerName, String team, int gameweek, int minutes);

    void updateUserTeamPoints(Long userId);

    void syncTeamGameweekPoints(UserTeam team);

    void ensureGameweekStatsInitialized(int gameweekNumber);
}
