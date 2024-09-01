package com.ssuamkiett.bookconnect.history;

import com.ssuamkiett.bookconnect.book.Book;
import com.ssuamkiett.bookconnect.book.BookMapper;
import com.ssuamkiett.bookconnect.book.PageResponse;
import com.ssuamkiett.bookconnect.exception.OperationNotPermittedException;
import com.ssuamkiett.bookconnect.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookReadingHistoryService {
    private final BookReadingHistoryRepository bookReadingHistoryRepository;
    private final BookMapper bookMapper;

    public void addBookToReading(User user, Book book) {
        bookReadingHistoryRepository.save(
                BookReadingHistory.builder()
                        .user(user)
                        .book(book)
                        .build()
        );
    }

    @Transactional
    public void removeBookFromReading(Integer userId, Integer bookId) {
        if(!bookReadingHistoryRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new OperationNotPermittedException("Book does not exist");
        }
        bookReadingHistoryRepository.deleteByUserIdAndBookId(userId, bookId);
    }

    public PageResponse<BookReadingResponse> findAllReadingBooksByOwner(int page, int size, Integer userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        Page<BookReadingHistory> allBookReadingHistories = bookReadingHistoryRepository.findAllBookReadingHistories(pageable, userId);
        List<BookReadingResponse> bookResponse = allBookReadingHistories.stream()
                .map(bookMapper::toReadingBookResponse)
                .toList();

        return new PageResponse<>(
                bookResponse,
                allBookReadingHistories.getNumber(),
                allBookReadingHistories.getSize(),
                allBookReadingHistories.getTotalElements(),
                allBookReadingHistories.getTotalPages(),
                allBookReadingHistories.isFirst(),
                allBookReadingHistories.isLast()
        );
    }
}
