import { CommonModule } from '@angular/common';
import { Component, ViewChild, ElementRef, inject } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { AddBookService } from '../../services/add-book.service';
import { Router } from '@angular/router';
import { AuthContextService } from '../../shared/auth-context.service';

@Component({
  selector: 'app-add-book',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-book.component.html',
  styleUrls: ['./add-book.component.css'], // Fixed the typo from styleUrl to styleUrls
})
export class AddBookComponent {
  isAuthenticated: boolean  = false;
  coverPhotoName: string | null = null;
  bookFileName: string | null = null;
  coverPhotoSrc: string | ArrayBuffer | null = null;
  bookFile: File | null = null;
  private addBookService = inject(AddBookService);
  private router = inject(Router);
  @ViewChild('coverPhotoInput') coverPhotoInput!: ElementRef;
  @ViewChild('bookFileInput') bookFileInput!: ElementRef;

  constructor(private authContextService: AuthContextService) {
    this.authContextService.getAuthenticationStatus().subscribe(status => {
      this.isAuthenticated = status;
    })
  }

  onCoverPhotoChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file) {
      this.coverPhotoName = file.name;

      const reader = new FileReader();
      reader.onload = () => {
        this.coverPhotoSrc = reader.result;
      };
      reader.readAsDataURL(file);
    } 
    else {
      this.coverPhotoName = null;
      this.coverPhotoSrc = null;
    }
  }

  onBookFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    this.bookFile = file || null;
    if (file) {
      this.bookFileName = file.name;
    } 
    else {
      this.bookFileName = null;
    }
  }

  removeCoverPhoto(): void {
    this.coverPhotoName = null;
    this.coverPhotoSrc = null;
    if (this.coverPhotoInput) {
      this.coverPhotoInput.nativeElement.value = '';
    }
  }

  removeBookFile(): void {
    this.bookFileName = null;
    if (this.bookFileInput) {
      this.bookFileInput.nativeElement.value = '';
    }
  }

  onSubmit(form: NgForm): void {
    form.value.genre = form.value.genre.split(" ").join(", ");
    const formData = new FormData();
    formData.append(
      'body',
      new Blob([JSON.stringify(form.value)], { type: 'application/json' })
    );
    if (this.coverPhotoInput.nativeElement.files.length) {
      formData.append(
        'coverPhoto',
        this.coverPhotoInput.nativeElement.files[0]
      );
    }      
    this.createBook(formData);
  }

  createBook(formData: FormData) {
    this.addBookService.createBook(formData).subscribe({
      next: (response: any) => {
        this.uploadBookFile(response.id);
      },
      complete: () => {},
      error: (error: any) => {
        console.error('Failed to load books', error);
      },
    });
  }

  uploadBookFile(bookId: number) {
    const bookFileForm = new FormData();

    if(this.bookFileInput.nativeElement.files.length) {
      bookFileForm.append('file', this.bookFileInput.nativeElement.files[0]);
    }

    if(!this.bookFile?.size) {
      return;
    }
    this.addBookService.uploadBookFile(bookId, bookFileForm).subscribe({
      next: (response: any) => {
        this.router.navigate(['/book-details', bookId]);
      },
      complete: () => {},
      error: (error: any) => {
        console.error('Failed to load books', error);
      },
    });
  }
}
