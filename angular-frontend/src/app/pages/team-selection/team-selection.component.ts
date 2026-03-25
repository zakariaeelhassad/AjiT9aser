import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TeamSelectionContentComponent } from './components/content/team-selection-content.component';

@Component({
  selector: 'app-team-selection',
  standalone: true,
  imports: [CommonModule, TeamSelectionContentComponent],
  templateUrl: './team-selection.component.html'
})
export class TeamSelectionComponent {}
