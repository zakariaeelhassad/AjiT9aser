import { Component, OnDestroy } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter, Subscription } from 'rxjs';
import { FooterComponent } from './shared/components/footer/footer.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, FooterComponent],
  template: `
    <router-outlet />
    @if (showFooter) {
      <app-footer />
    }
  `,
})
export class App implements OnDestroy {
  showFooter = true;
  private readonly routerSub: Subscription;

  constructor(private router: Router) {
    this.updateFooterVisibility(this.router.url);
    this.routerSub = this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event) => {
        const nav = event as NavigationEnd;
        this.updateFooterVisibility(nav.urlAfterRedirects);
      });
  }

  ngOnDestroy(): void {
    this.routerSub.unsubscribe();
  }

  private updateFooterVisibility(url: string): void {
    this.showFooter = !url.startsWith('/profile');
  }
}
