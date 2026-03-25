package com.example.backend.service.Impl;

import com.example.backend.dto.match.CurrentGameweekResponse;
import com.example.backend.dto.match.MatchEventDTO;
import com.example.backend.dto.match.MatchStatusResponse;
import com.example.backend.model.entity.Gameweek;
import com.example.backend.model.entity.Match;
import com.example.backend.repository.GameweekRepository;
import com.example.backend.repository.MatchRepository;
import com.example.backend.service.MatchEventCacheService;
import com.example.backend.service.SimulatedClockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;
    @Mock
    private GameweekRepository gameweekRepository;
    @Mock
    private SimulatedClockService clockService;
    @Mock
    private MatchEventCacheService eventCache;

    @InjectMocks
    private MatchService matchService;

    @Test
    void getByGameweek_shouldReturnEmpty_whenGameweekNotFound() {
        when(gameweekRepository.findByGameweekNumber(50)).thenReturn(Optional.empty());

        Optional<List<MatchStatusResponse>> result = matchService.getByGameweek(50);

        assertTrue(result.isEmpty());
    }

    @Test
    void getByGameweek_shouldReturnSortedMatchesWithEvents() {
        Gameweek gw = Gameweek.builder()
                .id(10L)
                .gameweekNumber(3)
                .startDate(LocalDateTime.now(ZoneOffset.UTC).minusDays(1))
                .endDate(LocalDateTime.now(ZoneOffset.UTC).plusDays(1))
                .build();

        Match late = Match.builder()
                .id(2L)
                .gameweek(gw)
                .homeTeam("Team C")
                .awayTeam("Team D")
                .homeScore(0)
                .awayScore(0)
                .kickoffTime(LocalDateTime.now(ZoneOffset.UTC).plusHours(2))
                .finished(false)
                .build();

        Match early = Match.builder()
                .id(1L)
                .gameweek(gw)
                .homeTeam("Team A")
                .awayTeam("Team B")
                .homeScore(1)
                .awayScore(0)
                .kickoffTime(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(30))
                .finished(false)
                .build();

        when(gameweekRepository.findByGameweekNumber(3)).thenReturn(Optional.of(gw));
        when(matchRepository.findByGameweekId(gw.getId())).thenReturn(List.of(late, early));
        when(eventCache.getEvents("Team A", "Team B", 3)).thenReturn(List.of(new MatchEventDTO("GOAL", "P1", "Team A", 20)));
        when(eventCache.getEvents("Team C", "Team D", 3)).thenReturn(List.of());

        Optional<List<MatchStatusResponse>> result = matchService.getByGameweek(3);

        assertTrue(result.isPresent());
        assertEquals(2, result.get().size());
        assertEquals(1L, result.get().get(0).id());
        assertEquals(1, result.get().get(0).events().size());
        assertEquals("GOAL", result.get().get(0).events().get(0).type());
    }

    @Test
    void getLive_shouldReturnOnlyLiveMatches() {
        Gameweek gw = Gameweek.builder()
                .id(10L)
                .gameweekNumber(3)
                .startDate(LocalDateTime.now(ZoneOffset.UTC).minusDays(1))
                .endDate(LocalDateTime.now(ZoneOffset.UTC).plusDays(1))
                .build();

        Match live = Match.builder()
                .id(1L)
                .gameweek(gw)
                .homeTeam("Team A")
                .awayTeam("Team B")
                .homeScore(1)
                .awayScore(0)
                .kickoffTime(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(10))
                .finished(false)
                .build();

        Match scheduled = Match.builder()
                .id(2L)
                .gameweek(gw)
                .homeTeam("Team C")
                .awayTeam("Team D")
                .homeScore(0)
                .awayScore(0)
                .kickoffTime(LocalDateTime.now(ZoneOffset.UTC).plusHours(2))
                .finished(false)
                .build();

        when(matchRepository.findAll()).thenReturn(List.of(live, scheduled));
        when(eventCache.getEvents("Team A", "Team B", 3)).thenReturn(List.of());
        when(eventCache.getEvents("Team C", "Team D", 3)).thenReturn(List.of());

        List<MatchStatusResponse> result = matchService.getLive();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("LIVE", result.get(0).status());
    }

    @Test
    void getCurrentGameweekFromDb_shouldReturnCurrentOrUpcomingGameweek() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        Gameweek past = Gameweek.builder()
                .id(1L)
                .gameweekNumber(1)
                .startDate(now.minusDays(10))
                .endDate(now.minusDays(5))
                .build();

        Gameweek active = Gameweek.builder()
                .id(2L)
                .gameweekNumber(2)
                .startDate(now.minusHours(2))
                .endDate(now.plusDays(2))
                .build();

        Match m = Match.builder()
                .id(7L)
                .gameweek(active)
                .homeTeam("X")
                .awayTeam("Y")
                .homeScore(0)
                .awayScore(0)
                .kickoffTime(now.minusMinutes(5))
                .finished(false)
                .build();

        when(gameweekRepository.findAll()).thenReturn(new ArrayList<>(List.of(past, active)));
        when(matchRepository.findByGameweekId(2L)).thenReturn(List.of(m));
        when(eventCache.getEvents("X", "Y", 2)).thenReturn(List.of());

        CurrentGameweekResponse response = matchService.getCurrentGameweekFromDb();

        assertEquals(2, response.currentGameweek());
        assertFalse(response.matches().isEmpty());
    }
}
