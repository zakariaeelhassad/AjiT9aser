import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TransferWindowStatus } from '../../../../core/models';
import { getTeamJersey, getTeamLogo } from '../../../../shared/utils/team-visuals';
import { LineupSlot } from '../substitutions-types';

@Component({
  selector: 'app-substitutions-board',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './substitutions-board.component.html',
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
export class SubstitutionsBoardComponent {
  private readonly backendBase = 'http://localhost:8081';

  @Input() teamName = '';
  @Input() teamImage: string | null = null;
  @Input() loadingGameState = true;
  @Input() gameweekLocked = false;
  @Input() transferWindowStatus: TransferWindowStatus | null = null;
  @Input() currentGameweek = 0;
  @Input() nextDeadlineLabel = '';

  @Input() starterGks: LineupSlot[] = [];
  @Input() starterDefs: LineupSlot[] = [];
  @Input() starterMids: LineupSlot[] = [];
  @Input() starterFwds: LineupSlot[] = [];
  @Input() benchSlots: LineupSlot[] = [];
  @Input() selectedSlotId: number | null = null;

  @Input() errorMessage: string | null = null;
  @Input() successMessage: string | null = null;
  @Input() hasUnsavedChanges = false;
  @Input() saving = false;

  @Output() slotSelected = new EventEmitter<LineupSlot>();
  @Output() resetClicked = new EventEmitter<void>();
  @Output() saveClicked = new EventEmitter<void>();

  onSlotClick(slot: LineupSlot): void {
    this.slotSelected.emit(slot);
  }

  onReset(): void {
    this.resetClicked.emit();
  }

  onSave(): void {
    this.saveClicked.emit();
  }

  isSelected(slot: LineupSlot): boolean {
    return this.selectedSlotId === slot.id;
  }

  getTeamImageSrc(): string | null {
    const value = this.teamImage ?? null;
    if (!value) {
      return null;
    }
    if (value.startsWith('data:image/')) {
      return value;
    }
    if (value.startsWith('/')) {
      return `${this.backendBase}${value}`;
    }
    if (value.startsWith('http://') || value.startsWith('https://')) {
      return value;
    }
    return null;
  }

  getClubLogo(team: string | null | undefined): string {
    return getTeamLogo(team || '');
  }

  getJersey(team: string | null | undefined): string {
    return getTeamJersey(team || '');
  }
}
