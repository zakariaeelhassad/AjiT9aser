import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PlayerSummary, PlayerTransferStat, Deadline, PlayerAvailability } from '../../core/services/api.service';
import { AuthService, UserResponse } from '../../core/services/auth.service';
import { DashboardStateService } from '../../core/services/dashboard-state.service';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { FooterComponent } from '../../shared/components/footer/footer.component';
import { PlayerCardComponent } from '../../shared/components/player-card/player-card.component';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

const POS_META: Record<string, { label: string; color: string; borderClass: string }> = {
  GK: { label: 'Goalkeeper', color: '#fbbf24', borderClass: 'border-amber-400' },
  DEF: { label: 'Defenders', color: '#38bdf8', borderClass: 'border-sky-400' },
  MID: { label: 'Midfielders', color: '#4ade80', borderClass: 'border-emerald-400' },
  FWD: { label: 'Forwards', color: '#f87171', borderClass: 'border-red-400' },
};

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, NavbarComponent, FooterComponent],
  templateUrl: './dashboard.component.html',
  styles: [`
    .custom-scrollbar::-webkit-scrollbar { height: 6px; }
    .custom-scrollbar::-webkit-scrollbar-track { background: rgba(255,255,255,0.02); border-radius: 4px; }
    .custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 4px; }
    .custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(255,255,255,0.2); }
  `]
})
export class DashboardComponent implements OnInit {
  user: UserResponse | null = null;
  posMeta = POS_META;

  get nextDeadline(): Deadline | undefined {
    let stats: any;
    this.state.dashboardStats$.subscribe(s => stats = s).unsubscribe();
    return stats?.deadlines?.find((d: Deadline) => d.isNext);
  }

  get upcomingDeadlines(): Deadline[] {
    let stats: any;
    this.state.dashboardStats$.subscribe(s => stats = s).unsubscribe();
    return stats?.deadlines?.filter((d: Deadline) => !d.isNext) || [];
  }

  get topTransfersIn(): PlayerTransferStat[] {
    let stats: any;
    this.state.dashboardStats$.subscribe(s => stats = s).unsubscribe();
    return stats?.topTransfersIn || [];
  }

  get topTransfersOut(): PlayerTransferStat[] {
    let stats: any;
    this.state.dashboardStats$.subscribe(s => stats = s).unsubscribe();
    return stats?.topTransfersOut || [];
  }

  get potw(): PlayerSummary[] {
    let stats: any;
    this.state.dashboardStats$.subscribe(s => stats = s).unsubscribe();
    return stats?.potw || [];
  }

  get totw(): PlayerSummary[] {
    let stats: any;
    this.state.dashboardStats$.subscribe(s => stats = s).unsubscribe();
    return stats?.totw || [];
  }

  get availability(): PlayerAvailability[] {
    let stats: any;
    this.state.dashboardStats$.subscribe(s => stats = s).unsubscribe();
    return stats?.availability || [];
  }

  constructor(
    public auth: AuthService,
    public state: DashboardStateService,
    private router: Router
  ) { }

  ngOnInit(): void {
    if (!this.auth.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.user = this.auth.getUser();

    // Trigger initial data load via State Service
    this.state.loadDashboardData();
  }
}
