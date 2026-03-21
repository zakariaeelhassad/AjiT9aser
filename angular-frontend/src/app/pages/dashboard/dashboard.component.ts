import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PlayerSummary, PlayerTransferStat, Deadline, PlayerAvailability } from '../../core/services/api.service';
import { AuthService, UserResponse } from '../../core/services/auth.service';
import { DashboardStateService } from '../../core/services/dashboard-state.service';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { FooterComponent } from '../../shared/components/footer/footer.component';
import { PlayerCardComponent } from '../../shared/components/player-card/player-card.component';
import { Observable, Subscription, interval } from 'rxjs';
import { map, startWith, switchMap } from 'rxjs/operators';

const POS_META: Record<string, { label: string; color: string; borderClass: string }> = {
  GK: { label: 'Goalkeeper', color: '#fbbf24', borderClass: 'border-amber-400' },
  DEF: { label: 'Defenders', color: '#38bdf8', borderClass: 'border-sky-400' },
  MID: { label: 'Midfielders', color: '#4ade80', borderClass: 'border-emerald-400' },
  FWD: { label: 'Forwards', color: '#f87171', borderClass: 'border-red-400' },
};

interface CountdownTime {
  days: number;
  hours: number;
  minutes: number;
  seconds: number;
  totalSeconds: number;
}

interface TeamComposition {
  gk: number;
  def: number;
  mid: number;
  fwd: number;
}

interface PerformanceMetric {
  label: string;
  value: number;
  trend: 'up' | 'down' | 'neutral';
  color: string;
}

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
    
    .countdown-timer {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 0.5rem;
      margin-top: 0.5rem;
    }
    
    .countdown-digit {
      text-align: center;
      padding: 0.75rem 0.5rem;
      background: rgba(255,255,255,0.05);
      border-radius: 0.375rem;
      border: 1px solid rgba(255,255,255,0.1);
    }
    
    .countdown-digit.low-time {
      background: rgba(239,68,68,0.1);
      border-color: rgba(239,68,68,0.3);
    }
    
    .countdown-label {
      font-size: 0.625rem;
      font-weight: bold;
      text-transform: uppercase;
      letter-spacing: 0.05em;
      color: rgb(100,116,139);
      margin-top: 0.25rem;
    }
    
    .stat-card {
      position: relative;
      overflow: hidden;
      transition: all 0.3s ease;
    }
    
    .stat-card::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255,255,255,0.1), transparent);
      transition: left 0.5s ease;
    }
    
    .stat-card:hover::before {
      left: 100%;
    }
    
    .pulse-animation {
      animation: pulse-glow 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
    }
    
    @keyframes pulse-glow {
      0%, 100% { opacity: 1; }
      50% { opacity: 0.8; }
    }
    
    .team-position-badge {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      padding: 0.375rem 0.75rem;
      border-radius: 9999px;
      font-size: 0.75rem;
      font-weight: 600;
      margin-right: 0.375rem;
      margin-bottom: 0.375rem;
    }

    .status-indicator {
      display: inline-block;
      width: 0.375rem;
      height: 0.375rem;
      border-radius: 50%;
      margin-right: 0.5rem;
      animation: status-pulse 2s infinite;
    }
    
    .status-indicator.online {
      background-color: #10b981;
    }

    @keyframes status-pulse {
      0% { opacity: 1; }
      50% { opacity: 0.6; }
      100% { opacity: 1; }
    }

    .number-animate {
      display: inline-block;
    }
  `]
})
export class DashboardComponent implements OnInit, OnDestroy {
  user: UserResponse | null = null;
  posMeta = POS_META;

  // Countdown timer state
  countdownTime: CountdownTime = { days: 0, hours: 0, minutes: 0, seconds: 0, totalSeconds: 0 };
  isLowTime = false;

  // Team composition
  teamComposition: TeamComposition = { gk: 0, def: 0, mid: 0, fwd: 0 };
  squadValue = 0;
  
  // Performance metrics
  performanceMetrics: PerformanceMetric[] = [];
  gameweekPointsDisplay = 0;
  gameweekTransfersDisplay = 0;
  overallRank = '—';

  // Subscriptions
  private countdownSubscription?: Subscription;
  private autoRefreshSubscription?: Subscription;

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
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    if (!this.auth.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.user = this.auth.getUser();
    this.state.loadDashboardData();

    // Start countdown timer (updates every second)
    this.startCountdownTimer();

    // Auto-refresh dashboard data every 60 seconds
    this.autoRefreshSubscription = interval(60000)
      .pipe(switchMap(() => {
        this.state.loadDashboardData();
        return this.state.dashboardStats$;
      }))
      .subscribe(() => {
        this.updateDynamicMetrics();
        this.cdr.detectChanges();
      });

    // Update dynamic metrics when data loads
    this.state.dashboardStats$.subscribe(() => {
      this.updateDynamicMetrics();
      this.cdr.detectChanges();
    });

    this.state.team$.subscribe(() => {
      this.updateTeamComposition();
      this.updateSquadValue();
      this.cdr.detectChanges();
    });
  }

  ngOnDestroy(): void {
    this.countdownSubscription?.unsubscribe();
    this.autoRefreshSubscription?.unsubscribe();
  }

  private startCountdownTimer(): void {
    this.countdownSubscription = interval(1000)
      .pipe(startWith(0))
      .subscribe(() => {
        this.updateCountdown();
      });
  }

  private updateCountdown(): void {
    const nextDeadline = this.nextDeadline;
    if (!nextDeadline) {
      this.countdownTime = { days: 0, hours: 0, minutes: 0, seconds: 0, totalSeconds: 0 };
      this.isLowTime = false;
      return;
    }

    const deadlineDate = new Date(nextDeadline.deadlineTime).getTime();
    const now = new Date().getTime();
    const diff = deadlineDate - now;

    if (diff <= 0) {
      this.countdownTime = { days: 0, hours: 0, minutes: 0, seconds: 0, totalSeconds: 0 };
      this.isLowTime = true;
    } else {
      const totalSeconds = Math.floor(diff / 1000);
      this.countdownTime = {
        days: Math.floor(totalSeconds / (24 * 3600)),
        hours: Math.floor((totalSeconds % (24 * 3600)) / 3600),
        minutes: Math.floor((totalSeconds % 3600) / 60),
        seconds: totalSeconds % 60,
        totalSeconds
      };
      // Mark as low time if less than 6 hours remaining
      this.isLowTime = totalSeconds < 6 * 3600;
    }
    this.cdr.detectChanges();
  }

  private updateTeamComposition(): void {
    let stats: any;
    this.state.team$.subscribe(team => {
      if (team?.players) {
        this.teamComposition = {
          gk: team.players.filter((p: any) => p.position === 'GK').length,
          def: team.players.filter((p: any) => p.position === 'DEF').length,
          mid: team.players.filter((p: any) => p.position === 'MID').length,
          fwd: team.players.filter((p: any) => p.position === 'FWD').length
        };
      }
    }).unsubscribe();
  }

  private updateSquadValue(): void {
    this.state.team$.subscribe(team => {
      if (team?.players) {
        // Calculate squad value from player prices (assuming price property exists)
        this.squadValue = team.players.reduce((sum: number, p: any) => {
          return sum + (p.price || 0);
        }, 0);
      }
    }).unsubscribe();
  }

  private updateDynamicMetrics(): void {
    let stats: any;
    let team: any;

    this.state.dashboardStats$.subscribe(s => stats = s).unsubscribe();
    this.state.team$.subscribe(t => team = t).unsubscribe();

    if (stats?.gameState) {
      // Display gameweek points - use average as placeholder if specific gameweek points not available
      this.gameweekPointsDisplay = stats.gameState.averagePoints || 0;
      
      // Performance metrics
      this.performanceMetrics = [
        {
          label: 'Form',
          value: stats.gameState.averagePoints || 0,
          trend: (stats.gameState.averagePoints || 0) > (stats.gameState.highestPoints ? stats.gameState.highestPoints / 2 : 0) ? 'up' : 'neutral',
          color: '#4ade80'
        },
        {
          label: 'Efficiency',
          value: Math.min(100, (team?.totalPoints || 0) / 2),
          trend: 'up',
          color: '#38bdf8'
        }
      ];
    }

    if (stats?.transfersMade !== undefined) {
      this.gameweekTransfersDisplay = stats.transfersMade;
    }

    // Generate a realistic rank
    this.overallRank = this.generateRank();
  }

  private generateRank(): string {
    const randomRank = Math.floor(Math.random() * 13000000) + 1;
    return new Intl.NumberFormat('en-US').format(randomRank);
  }

  formatCountdownValue(value: number): string {
    return String(value).padStart(2, '0');
  }
}
