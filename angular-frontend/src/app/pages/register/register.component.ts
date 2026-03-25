import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService, RegisterRequest } from '../../core/services/auth.service';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink, NavbarComponent],
  templateUrl: 'register.component.html'
})
export class RegisterComponent {
  form: RegisterRequest = { username: '', email: '', password: '' };
  error = '';
  success = false;
  loading = false;

  constructor(private auth: AuthService, private router: Router) { }

  onSubmit(isValid?: boolean | null): void {
    if (isValid === false) {
      this.error = 'Please fill out all fields correctly.';
      return;
    }
    this.error = '';
    this.loading = true;
    this.auth.register(this.form).subscribe({
      next: () => {
        this.success = true;
        this.loading = false;
        setTimeout(() => this.router.navigate(['/dashboard']), 1200);
      },
      error: (err) => {
        if (err.error && typeof err.error === 'object' && !err.error.message && !err.error.error) {
          // Flatten validation error map
          this.error = Object.values(err.error).join(' ');
        } else {
          this.error = err.error?.message || err.error?.error || 'Registration failed. Please try again.';
        }
        this.loading = false;
      }
    });
  }
}
