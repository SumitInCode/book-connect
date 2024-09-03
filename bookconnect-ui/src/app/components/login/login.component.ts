import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AuthContextService } from '../../shared/auth-context.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'], // Changed from styleUrl to styleUrls
})
export class LoginComponent {
  authService = inject(AuthService);
  router = inject(Router);
  errorMessage: string | null = null;
  private authContext = inject(AuthContextService);
  onSubmit(f: NgForm) {
    if (f.valid) {
      this.login(f.value);
    } 
    else {
      console.error('Form is invalid');
    }
  }

  login(loginData: any) {
    this.authService.onLogin(loginData).subscribe(
      {
        complete:  () => {
          this.authContext.setAuthenticationStatus(true);
          this.router.navigate(['/home']);
        },
        error: (error: any) => {
          console.log(error.error.errorDescription)
          alert(error.error.errorDescription)
        }
      }
    );
  }
}
