import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthContextService } from '../../shared/auth-context.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent {
  menuOpen = false;
  isAuthenticated: boolean  = false;
  constructor(
    private router: Router,
    private authContextService: AuthContextService,
    private authService: AuthService
  ) {
    this.authContextService.getAuthenticationStatus().subscribe(status => {
      this.isAuthenticated = status;
    })
  }

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  handleGetStarted() {
    this.router.navigate(['/get-started']);
    this.menuOpen = false;
  }

  handleLogout() {
    this.authService.onLogout();
    this.router.navigate(['/home']);
    this.menuOpen = false;
  }
}
