import { MatchResponse } from './match.models';
import { PlayerAvailability, PlayerSummary, PlayerTransferStat } from './player.models';
import { TeamGameweekStats, TeamLineupResponse, TeamResponse } from './team.models';

export interface GameweekResponse {
    id: number;
    gameweekNumber: number;
    startDate: string;
    endDate: string;
    status: string;
}

export interface TransferWindowStatus {
    currentDate: string;
    activeGameweek: number | null;
    nextGameweek: number | null;
    nextDeadline: string | null;
    transfersAllowed: boolean;
    phase: string;
    message: string;
}

export interface SimClock {
    simulatedNow: string;
}

export interface GameState {
    currentGameweek: number;
    isRunning: boolean;
    gameweekActive: boolean;
    averagePoints: number;
    highestPoints: number;
    topManager: string;
    topManagerPoints: number;
}

export interface LeaderboardEntry {
    rank: number;
    userId: number;
    username: string;
    teamName: string;
    totalPoints: number;
}

export interface Deadline {
    gameweek: number;
    deadlineTime: string;
    isNext: boolean;
}

export interface DashboardStats {
    gameState: GameState;
    topTransfersIn: PlayerTransferStat[];
    topTransfersOut: PlayerTransferStat[];
    deadlines: Deadline[];
    potw: PlayerSummary[];
    totw: PlayerSummary[];
    availability: PlayerAvailability[];
    wildcardsPlayed: number;
    transfersMade: number;
    mostTransferredInPlayer: string;
    mostCaptainedPlayer: string;
}

export interface UserLeague {
    id: number;
    name: string;
    userRank: number;
    type: 'classic' | 'broadcaster' | 'general' | 'cup' | 'h2h';
}

export interface PitchPlayer {
    player: PlayerSummary;
    isCaptain: boolean;
    isViceCaptain: boolean;
    multiplier: number;
    isSubstitite: boolean;
}

export interface AutoSub {
    playerOut: PlayerSummary;
    playerIn: PlayerSummary;
}

export interface Fixture {
    id: number;
    homeTeam: string;
    awayTeam: string;
    homeScore: number;
    awayScore: number;
    kickoffTime: string;
    status: 'Upcoming' | 'Live' | 'FT';
    broadcasters: string[];
}

export interface PointsPageData {
    activeGameweek: number;
    gwPoints: number;
    averagePoints: number;
    highestPoints: number;
    gwRank: number;
    gwTransfers: number;
    leagues: UserLeague[];
    pitchPlayers: PitchPlayer[];
    benchPlayers: PitchPlayer[];
    autoSubs: AutoSub[];
    fixtures: Fixture[];
}

export interface PointsViewData {
    context: MatchResponse[];
    team: TeamResponse;
    stats: TeamGameweekStats;
    lineup: TeamLineupResponse;
}