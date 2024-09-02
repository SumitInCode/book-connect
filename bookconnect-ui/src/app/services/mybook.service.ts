import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class MybookService {
  private FIND_ALL_MYBOOK_URL: string = '/books/owner';
  private FIND_ALL_MYREADING_BOOKS: string = '/books/reading'
  private http = inject(HttpClient);

  constructor() {}

  getMyBooks(page: number = 0, size: number = 10): Observable<any> {
    let pagingParams = new HttpParams()
      .set('page', page)
      .set('size', size);
    return this.http.get(this.FIND_ALL_MYBOOK_URL, { params: pagingParams });
  }

  getMyReadingBooks(page: number = 0, size: number = 10): Observable<any> {
    let pagingParams = new HttpParams()
      .set('page', page)
      .set('size', size);
    return this.http.get(this.FIND_ALL_MYREADING_BOOKS, { params: pagingParams });
  }
}
