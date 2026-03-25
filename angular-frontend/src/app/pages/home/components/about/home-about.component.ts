import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

export interface HomeScoringItem {
  event: string;
  pts: string;
  color: string;
}

@Component({
  selector: 'app-home-about',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './home-about.component.html'
})
export class HomeAboutComponent {
  @Input() isAuthenticated = false;
  @Input() scoring: HomeScoringItem[] = [];
}
