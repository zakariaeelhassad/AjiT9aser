import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardContentComponent } from './components/content/dashboard-content.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, DashboardContentComponent],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent {}
