import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  private REGISTER_URL = "/auth/register";
  private http = inject(HttpClient);
  private router = inject(Router);
  onSubmit(formData: NgForm) {
    this.register(formData.value);
  }

  register(registerData: any) {
    this.http.post(this.REGISTER_URL, registerData).subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.log(error.error.errorDescription);
      }
    })
  }
}
