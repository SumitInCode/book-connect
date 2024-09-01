import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class MybookService {
  private FIND_ALL_MYBOOK_URL: string = '/books/owner';
  private http = inject(HttpClient);

  constructor() {}

  getMyBooks(page: number = 0, size: number = 10): Observable<any> {
    let pagingParams = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get(this.FIND_ALL_MYBOOK_URL, { params: pagingParams });
  }
}
