import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ApiService, GameState } from '../../core/services/api.service';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { FooterComponent } from '../../shared/components/footer/footer.component';

const FEATURES = [
  { icon: '🧠', title: 'Pick Your Squad', desc: 'Choose 15 players within your 100.0 budget. Balance star power with depth across all positions.', color: 'blue' },
  { icon: '⚡', title: 'Earn Points Every GW', desc: 'Goals, assists, clean sheets — every action earns points. Tune your lineup before each gameweek.', color: 'purple' },
  { icon: '🏆', title: 'Climb the Leaderboard', desc: 'Compete in global and private leagues. See where you rank against friends and the entire player base.', color: 'green' },
];

const SCORING = [
  { event: 'Goal (FWD)', pts: '+4', color: '#f87171' },
  { event: 'Goal (MID)', pts: '+5', color: '#4ade80' },
  { event: 'Goal (DEF)', pts: '+6', color: '#38bdf8' },
  { event: 'Assist', pts: '+3', color: '#818cf8' },
  { event: 'Yellow Card', pts: '-1', color: '#fbbf24' },
  { event: 'Red Card', pts: '-3', color: '#f87171' },
];

const FEATURE_ICON_BG: Record<string, string> = {
  blue: 'rgba(56,189,248,0.12)', purple: 'rgba(129,140,248,0.12)', green: 'rgba(74,222,128,0.12)',
};
const FEATURE_ICON_BORDER: Record<string, string> = {
  blue: 'rgba(56,189,248,0.2)', purple: 'rgba(129,140,248,0.2)', green: 'rgba(74,222,128,0.2)',
};
const FEATURE_BAR: Record<string, string> = {
  blue: '#38bdf8', purple: '#818cf8', green: '#4ade80',
};

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, NavbarComponent, FooterComponent],
  templateUrl: './home.component.html',
})
export class HomeComponent implements OnInit {
  features = FEATURES;
  scoring = SCORING;
  iconBg = FEATURE_ICON_BG;
  iconBorder = FEATURE_ICON_BORDER;
  featureBar = FEATURE_BAR;

  gameState: GameState | null = null;

  constructor(
    public auth: AuthService,
    private api: ApiService
  ) { }

  ngOnInit(): void {
    this.api.getGameState().subscribe({
      next: (state) => this.gameState = state,
      error: (err) => console.error('Error fetching game state for home page', err)
    });
  }
}
