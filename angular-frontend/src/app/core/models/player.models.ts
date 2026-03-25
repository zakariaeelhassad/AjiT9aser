export interface PlayerSummary {
    id: number;
    name: string;
    position: string;
    realTeam: string;
    price: number;
    totalPoints: number;
}

export interface PlayerTransferStat {
    playerId: number;
    name: string;
    position: string;
    realTeam: string;
    transfers: number;
}

export interface PlayerAvailability {
    playerId: number;
    name: string;
    realTeam: string;
    position: string;
    status: string;
    news: string;
    chanceOfPlaying: number;
}