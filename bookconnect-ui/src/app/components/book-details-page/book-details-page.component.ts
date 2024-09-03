import { Component, EventEmitter, inject, OnInit, Output } from '@angular/core';
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
  bookId: number | undefined;
  constructor(private authContextService: AuthContextService) {
    this.authContextService.getAuthenticationStatus().subscribe(status => {
      this.isAuthenticated = status;
    })
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.bookId = +params.get('id')!;
      if(this.isAuthenticated) {
        this.getBookReadingStatus(this.bookId);
      }
      this.getBook(this.bookId);
    })
  }

  getBook(bookId: number) {
    this.bookDetailsService.getBook(bookId).subscribe({
      next: (response: any) => {
        this.book = response;
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
    });
  }

  readBook(bookId: number) {
    this.router.navigate(['/reader', bookId]);
  }

  getStars(rating: number): number[] {
    return Array(rating).fill(1);
  }

  getEmptyStar(rate: number) {
    /*
      I don't know why i have to convert to Number type 
      it not working with number type its giving Error 
      saying Array len is not valid;
    */
    let len: Number  = new Number(5 - Math.min(rate, 5));
    return Array(len).fill(1);
  }
}

