import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BookDetailsService {
  constructor() {}

  private BOOK_URL: string = '/books/book/';
  private DELETE_BOOK_URL: string = '/books/delete/';
  private ADD_TO_READ: string = "/books/read/"
  private READ_STATUS: string = "/books/read/status/"
  private http = inject(HttpClient);

  getBook(bookId: number): Observable<any> {
    return this.http.get(this.BOOK_URL + bookId);
  }

  deleteBook(bookId: number): Observable<any> {
    return this.http.delete(this.DELETE_BOOK_URL + bookId);
  }

  addToReadBook(bookId: number): Observable<any> {
    return this.http.patch(this.ADD_TO_READ + bookId, null);
  }
  
  getReadingStatus(bookId: number): Observable<any> {
    return this.http.get(this.READ_STATUS + bookId);
  }
}
