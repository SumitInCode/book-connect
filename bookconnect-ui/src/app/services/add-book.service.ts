import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AddBookService {
  private CREATE_BOOK_URL = '/books/create';
  private UPLOAD_BOOK_FILE_URL = '/books/book-file/';
  private http = inject(HttpClient);
  constructor() {}

  createBook(formData: FormData): Observable<any> {
    return this.http.post(this.CREATE_BOOK_URL, formData);
  }

  uploadBookFile(bookId: number, bookFileData: FormData) {
    return this.http.post(this.UPLOAD_BOOK_FILE_URL + bookId, bookFileData);
  }
} 
