package com.example.backend.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Provides a simulated "now" that replays the 2024/25 Premier League season.
 *
 * Strategy: the real season started on 2024-08-16. We map the simulation so
 * that "today" in simulation time = season start + (real minutes elapsed since
 * a configurable anchor). By default the anchor is set so GW1 matches are
 * "happening right now" when the app first runs.
 *
 * Speed: 1 simulation-minute = 1 real-second (60x faster)
 * This makes a 90-min match complete in 90 real seconds, and a full gameweek
 * (spread over ~3 days) replay in ~4 real minutes — fast enough to demo, slow
 * enough to observe LIVE status.
 *
 * You can adjust SPEED_MULTIPLIER to taste.
 */
@Service
public class SimulatedClockService {

    // ---- Configuration -------------------------------------------------

    /** The first kickoff in the dataset: GW1, 2024-08-16T19:00Z. */
    private static final LocalDateTime SEASON_START = LocalDateTime.of(2024, 8, 16, 18, 0, 0); // 1h before first KO

    /**
     * How many simulation-seconds pass per real second.
     * 60 = 1 sim-minute per real-second → a 90-min game lasts 90 real seconds.
     */
    private static final long SPEED_MULTIPLIER = 60L;

    // ---- State ---------------------------------------------------------

    /** Real-world moment when the simulation "started". */
    private final long anchorEpochSecond;

    public SimulatedClockService() {
        this.anchorEpochSecond = System.currentTimeMillis() / 1000L;
    }

    // ---- Public API ----------------------------------------------------

    /**
     * Returns the current simulated date/time.
     * Callers treat this exactly as LocalDateTime.now() but in "football time".
     */
    public LocalDateTime getSimulatedNow() {
        long realSecondsElapsed = (System.currentTimeMillis() / 1000L) - anchorEpochSecond;
        long simSecondsElapsed = realSecondsElapsed * SPEED_MULTIPLIER;
        return SEASON_START.plusSeconds(simSecondsElapsed);
    }

    /**
     * Compute the match status relative to the simulated clock.
     *
     * @param kickoffTime UTC kickoff time stored in the DB / JSON
     * @param finished    whether the match has a recorded final result
     * @return "SCHEDULED" | "LIVE" | "FINISHED"
     */
    public String computeStatus(LocalDateTime kickoffTime, boolean finished) {
        LocalDateTime now = getSimulatedNow();

        if (finished && kickoffTime.isBefore(now)) {
            return "FINISHED";
        }
        if (kickoffTime.isAfter(now)) {
            return "SCHEDULED";
        }
        // Between kickoff and kickoff+105 min (90 regular + 15 buffer) = LIVE
        LocalDateTime matchEnd = kickoffTime.plusMinutes(105);
        if (now.isBefore(matchEnd)) {
            return "LIVE";
        }
        return "FINISHED";
    }

    /** Elapsed simulation minutes since kickoff (0 if not started). */
    public int getElapsedMinutes(LocalDateTime kickoffTime) {
        LocalDateTime now = getSimulatedNow();
        if (kickoffTime.isAfter(now))
            return 0;
        long secs = java.time.Duration.between(kickoffTime, now).getSeconds();
        return (int) Math.min(secs / 60, 90);
    }
}
