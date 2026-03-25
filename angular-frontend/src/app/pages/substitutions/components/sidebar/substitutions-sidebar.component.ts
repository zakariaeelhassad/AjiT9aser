import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { getTeamLogo } from '../../../../shared/utils/team-visuals';
import { LineupSlot } from '../substitutions-types';

@Component({
  selector: 'app-substitutions-sidebar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './substitutions-sidebar.component.html',
  styles: [
    `
      .glass-card {
        background: rgba(30, 41, 59, 0.4);
        backdrop-filter: blur(12px);
        -webkit-backdrop-filter: blur(12px);
        border: 1px solid rgba(255, 255, 255, 0.08);
        border-radius: 1rem;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
      }
    `
  ]
})
export class SubstitutionsSidebarComponent {
  @Input() loadingLineup = true;
  @Input() starterSlots: LineupSlot[] = [];
  @Input() benchSlots: LineupSlot[] = [];
  @Input() selectedSlotId: number | null = null;

  @Output() slotSelected = new EventEmitter<LineupSlot>();

  onSlotClick(slot: LineupSlot): void {
    this.slotSelected.emit(slot);
  }

  isSelected(slot: LineupSlot): boolean {
    return this.selectedSlotId === slot.id;
  }

  getClubLogo(team: string | null | undefined): string {
    return getTeamLogo(team || '');
  }
}
