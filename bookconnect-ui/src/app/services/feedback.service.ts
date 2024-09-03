import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FeedbackService {
  private FEEDBACK_URL = "/feedbacks/";
  private http = inject(HttpClient);
  constructor() { }

  postFeedback(bookdId: number, feedbackRequest: any): Observable<any> {
    return this.http.post(this.FEEDBACK_URL + bookdId, feedbackRequest);
  }

  getAllFeedbacks(
    bookId:      number,
    page: number,
    size: number = 10
  ): Observable<any> {
    let Pageparams = new HttpParams()
      .set('page', page)
      .set('size', size);
    return this.http.get(this.FEEDBACK_URL + bookId, { params: Pageparams });
  }


  deleteFeedback(bookdId: number): Observable<any> {
    return this.http.delete(this.FEEDBACK_URL + bookdId);
  }
}
        