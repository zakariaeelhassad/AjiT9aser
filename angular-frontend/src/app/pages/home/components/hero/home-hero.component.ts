import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home-hero',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './home-hero.component.html'
})
export class HomeHeroComponent {
  @Input() isAuthenticated = false;
  @Input() currentGameweek = 38;
}
