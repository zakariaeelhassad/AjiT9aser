import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SubstitutionsContentComponent } from './components/content/substitutions-content.component';

@Component({
  selector: 'app-substitutions',
  standalone: true,
  imports: [CommonModule, SubstitutionsContentComponent],
  templateUrl: './substitutions.component.html'
})
export class SubstitutionsComponent {}
