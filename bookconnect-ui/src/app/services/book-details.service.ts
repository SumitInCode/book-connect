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
  private http = inject(HttpClient);

  getBook(bookId: number): Observable<any> {
    return this.http.get(this.BOOK_URL + bookId);
  }

  deleteBook(bookId: number): Observable<any> {
    return this.http.delete   (this.DELETE_BOOK_URL + bookId);
  }
}
