package com.ssuamkiett.bookconnect.feeback;

import com.ssuamkiett.bookconnect.book.Book;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {
    public Feedback toFeedback(FeedbackRequest feedbackRequest, Integer bookId) {
        return Feedback.builder()
                .note(feedbackRequest.note())
                .comment(feedbackRequest.comment())
                .book(Book.builder()
                        .id(bookId)
                        .build()
                )
                .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer id) {
        return FeedbackResponse.builder()
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(), id))
                .userName(feedback.getUser().fullName())
                .creationDate((feedback.getLastModifiedDate() == null) ?
                                feedback.getCreationDate() : feedback.getLastModifiedDate())
                .build();
    }
}
