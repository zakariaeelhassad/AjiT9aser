import { TeamLineupPlayer } from '../../../core/models';

export interface LineupSlot {
  id: number;
  player: TeamLineupPlayer | null;
}
