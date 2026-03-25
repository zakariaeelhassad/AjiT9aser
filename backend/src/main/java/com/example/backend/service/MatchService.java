package com.example.backend.service;

import com.example.backend.dto.match.CurrentGameweekResponse;
import com.example.backend.dto.match.GameweekSummaryResponse;
import com.example.backend.dto.match.MatchStatusResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MatchService {

    Optional<List<MatchStatusResponse>> getByGameweek(Integer gameweekNumber);

    List<MatchStatusResponse> getLive();

    List<GameweekSummaryResponse> getGameweeks();

    Map<String, String> getClock();

    CurrentGameweekResponse getCurrentGameweekFromDb();
}