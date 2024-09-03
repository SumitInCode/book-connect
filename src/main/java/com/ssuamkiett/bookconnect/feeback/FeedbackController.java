package com.ssuamkiett.bookconnect.feeback;

import com.ssuamkiett.bookconnect.book.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedbacks")
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping("/{bookId}")
    public ResponseEntity<Integer> saveFeedback(
            @PathVariable("bookId") Integer bookId,
            @Valid @RequestBody FeedbackRequest feedbackRequest,
            Authentication connectedUser) {
        return ResponseEntity.ok(feedbackService.save(bookId, feedbackRequest, connectedUser));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbacksByBook(
            @PathVariable("bookId") Integer bookId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(feedbackService.findAllFeedbacksByBook(bookId, page, size, connectedUser));
    }

    @DeleteMapping("delete/{bookId}")
    public ResponseEntity<?> deleteFeedback(
            @PathVariable("bookId") Integer bookId,
            Authentication connectedUser
    ) {
        feedbackService.deleteFeedback(bookId, connectedUser);
        return ResponseEntity.ok().build();
    }
}
