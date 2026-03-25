import { PlayerSummary } from './player.models';

export interface TeamResponse {
    id: number;
    teamName: string;
    teamImage?: string | null;
    budget: number;
    remainingBudget: number;
    totalPoints: number;
    players: PlayerSummary[];
    playerCount: number;
    currentGameweekTransferCount?: number;
}

export interface TeamLineupPlayer {
    id: number;
    name: string;
    position: string;
    realTeam: string;
    price: number;
    totalPoints: number;
    starter: boolean;
}

export interface TeamLineupResponse {
    teamId: number;
    teamName: string;
    remainingBudget: number;
    players: TeamLineupPlayer[];
}

export interface TeamGameweekStats {
    gameweek: number;
    teamPoints: number;
    globalHighestPoints: number;
    players: {
        playerId: number;
        name: string;
        position: string;
        realTeam: string;
        price: number;
        totalPoints: number;
        points: number;
        starter: boolean;
    }[];
}