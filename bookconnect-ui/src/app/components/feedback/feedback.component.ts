import { CommonModule } from '@angular/common';
import { Component, inject, Input, OnInit } from '@angular/core';
import { FeedbackService } from '../../services/feedback.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-feedback',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.css']
})
export class FeedbackComponent implements OnInit {
  feedbacks: any[] = [];
  commentToPost: string = '';
  note: number = 0;
  stars = [1, 2, 3, 4, 5];
  currentRating = 0;
  private feedbackService = inject(FeedbackService);

  @Input() bookId: number | undefined;

  ngOnInit(): void {
    this.loadFeedbacks();
  }

  loadFeedbacks() {
    if (this.bookId === undefined) {
      return;
    }
    this.feedbackService.getAllFeedbacks(this.bookId, 0, 10).subscribe({
      next: (response) => {
        this.feedbacks = response.content;
      }
    });
  }

  setRating(rating: number) {
    this.currentRating = rating;
    this.note = rating; 
  }

  onCommentChange(event: Event) {
    this.commentToPost = (event.target as HTMLTextAreaElement).value;
  }

  postComment() {
    const feedbackToPost = {
      note: this.note,
      comment: this.commentToPost
    };
    console.log(feedbackToPost  )!
    if (this.bookId === undefined) {
      return;
    }

    this.feedbackService.postFeedback(this.bookId, feedbackToPost).subscribe({
      next: () => {
        this.commentToPost = '';
        this.currentRating = 0;
        this.loadFeedbacks();
      }
    });
  }


  delete() {
    if (this.bookId === undefined) {
      return;
    }

    this.feedbackService.deleteFeedback(this.bookId).subscribe(() => {
      this.loadFeedbacks();
    });
  }

  getStars(rating: number) {
    return Array(rating).fill(1);
  }

  getEmptyStars(rating: number) {
    return Array(5 - rating).fill(1);
  }
}
