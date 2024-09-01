import { Component, inject, OnInit } from '@angular/core';
import { FeedbackComponent } from '../feedback/feedback.component';
import { ActivatedRoute, Router } from '@angular/router';
import { BookDetailsService } from '../../services/book-details.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-book-details-page',
  standalone: true,
  imports: [FeedbackComponent, CommonModule],
  templateUrl: './book-details-page.component.html',
  styleUrl: './book-details-page.component.css'
})
export class BookDetailsPageComponent implements OnInit{
  book: any = {};
  coverPhotNotFound: string = "/coverNotAvailable.jpg";
  private route = inject(ActivatedRoute);
  private bookDetailsService = inject(BookDetailsService)
  private router = inject(Router);
  
  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
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
}

