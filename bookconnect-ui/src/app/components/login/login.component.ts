import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AuthContextService } from '../../shared/auth-context.service';
import {ModalComponent} from "../modal/modal.component";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule, ModalComponent],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  isError = false;
  errorMsg = 'Internal Server Error!';
  authService = inject(AuthService);
  router = inject(Router);
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
          this.isError = true;
          if(error.error.errorDescription) {
            this.errorMsg = error.error.errorDescription;
          }
        }
      }
    );
  }

  onClose() {
    this.isError = false;
  }
}
