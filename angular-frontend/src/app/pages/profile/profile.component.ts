import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfileContentComponent } from './components/content/profile-content.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ProfileContentComponent],
  templateUrl: './profile.component.html'
})
export class ProfileComponent {}
