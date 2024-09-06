import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import {ModalComponent} from "../modal/modal.component";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule, ModalComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  isError = false;
  errorMsg = 'Internal Server Error!';
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
        this.isError = true;
        if(error.error.errorDescription) {
          this.errorMsg = error.error.errorDescription;
        }
      }
    })
  }

  onClose(): void {
    this.isError = false;
  }
}
