export interface MatchEvent {
    type: string;
    player: string;
    team: string;
    minute: number;
}

export interface MatchResponse {
    id: number;
    gameweekNumber: number;
    homeTeam: string;
    awayTeam: string;
    homeScore: number;
    awayScore: number;
    kickoffTime: string;
    finished: boolean;
    status: string;
    elapsedMinutes: number;
    events: MatchEvent[];
}

export interface CurrentGameweekContext {
    currentDate: string;
    currentGameweek: number | null;
    matches: MatchResponse[];
}