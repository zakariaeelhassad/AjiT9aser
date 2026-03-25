import { Component, Input } from '@angular/core';

export interface HomeFeatureItem {
  icon: string;
  title: string;
  desc: string;
  color: string;
}

@Component({
  selector: 'app-home-features',
  standalone: true,
  templateUrl: './home-features.component.html'
})
export class HomeFeaturesComponent {
  @Input() features: HomeFeatureItem[] = [];
  @Input() iconBg: Record<string, string> = {};
  @Input() iconBorder: Record<string, string> = {};
  @Input() featureBar: Record<string, string> = {};
}
