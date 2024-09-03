import { inject, Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class BookService {
  private FIND_ALL_URL: string = '/books';
  private SEARCH_BOOK_URL: string = '/books/search';
  private POPULAR_BOOK_URL: string = "/books/popular";

  private http = inject(HttpClient);

  getAllBooks(page: number = 0, size: number = 10): Observable<any> {
    let pagingParams = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get(this.FIND_ALL_URL, { params: pagingParams });
  }

  getSearchedBooks(
    searchQuery: string,
    page: number,
    size: number = 10
  ): Observable<any> {
    let searchParams = new HttpParams()
      .set('query', searchQuery)
      .set('page', page)
      .set('size', size);
    return this.http.get(this.SEARCH_BOOK_URL, { params: searchParams });
  }

  getPopularBooks(page: number = 0, size: number = 10): Observable<any> {
    let pagingParams = new HttpParams()
      .set('page', page)
      .set('size', size);
    return this.http.get(this.POPULAR_BOOK_URL, { params: pagingParams });
  }
}
