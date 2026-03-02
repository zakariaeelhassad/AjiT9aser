package com.example.backend.repository;

import com.example.backend.model.PlayerGameweekStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerGameweekStatsRepository extends JpaRepository<PlayerGameweekStats, Long> {

    List<PlayerGameweekStats> findByPlayerId(Long playerId);

    List<PlayerGameweekStats> findByGameweekId(Long gameweekId);

    List<PlayerGameweekStats> findByPlayerIdAndGameweekId(Long playerId, Long gameweekId);

    java.util.Optional<PlayerGameweekStats> findByPlayerIdAndGameweekNumber(Long playerId, int gameweekNumber);

    List<PlayerGameweekStats> findByMatchId(Long matchId);
}
