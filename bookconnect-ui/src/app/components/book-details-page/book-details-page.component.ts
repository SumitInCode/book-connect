import { Component, inject, OnInit } from '@angular/core';
import { FeedbackComponent } from '../feedback/feedback.component';
import { ActivatedRoute, Router } from '@angular/router';
import { BookDetailsService } from '../../services/book-details.service';
import { CommonModule } from '@angular/common';
import { AuthContextService } from '../../shared/auth-context.service';

@Component({
  selector: 'app-book-details-page',
  standalone: true,
  imports: [FeedbackComponent, CommonModule],
  templateUrl: './book-details-page.component.html',
  styleUrl: './book-details-page.component.css'
})
export class BookDetailsPageComponent implements OnInit{
  book: any = {};
  readStatus: boolean = false;
  isAuthenticated: boolean = false;
  coverPhotNotFound: string = "/coverNotAvailable.jpg";
  private route = inject(ActivatedRoute);
  private bookDetailsService = inject(BookDetailsService)
  private router = inject(Router);

  constructor(private authContextService: AuthContextService) {
    this.authContextService.getAuthenticationStatus().subscribe(status => {
      this.isAuthenticated = status;
    })
  }
  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      if(this.isAuthenticated) {
        this.getBookReadingStatus(+params.get('id')!);
      }
      this.getBook(+params.get('id')!);
    })
  }

  getBook(bookId: number) {
    this.bookDetailsService.getBook(bookId).subscribe({
      next: (response: any) => {
        this.book = response;
      },
      complete: () => {
      },
      error: (error: any) => {
        console.error('Failed to load books', error);
      },
    });
  }
  

  isOwnerOfBook(book: any): boolean | false {
    return book.isOwner !== undefined ? book.isOwner : false;
  }

  deleteBook(bookId: number) {
    this.bookDetailsService.deleteBook(bookId).subscribe({
      next: () => {
        this.router.navigate(['/my-books']);
      },
      complete: () => {
      },                                                                  
      error: (error: any) => {
        console.error('Failed to load books', error);
      },
    });
  }

  handleAddToReadBook(bookId: number) {
    if(!this.isAuthenticated) {
      this.router.navigate(['/login']);
      return;
    }
    this.bookDetailsService.addToReadBook(bookId).subscribe({
      next: () => {
        this.router.navigate(['/book-shelf']);
      },
      complete: () => {
      },
      error: (error: any) => {
        alert(error.error.errorDescription)
        console.error('Failed to load books', error.error.errorDescription);
      },
    });
  }

  getBookReadingStatus(bookId: number) {
    if(!this.isAuthenticated) {
      this.router.navigate(['/login']);
      return;
    }
    this.bookDetailsService.getReadingStatus(bookId).subscribe({
      next: (response) => {
        this.readStatus = response.reading;
      },
      error: (error: any) => {
        alert("Server Error")
      },
    });
  }

  readBook(bookId: number) {
    this.router.navigate(['/reader', bookId]);
  }
}

