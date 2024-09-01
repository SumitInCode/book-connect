package com.ssuamkiett.bookconnect.book;

import com.ssuamkiett.bookconnect.file.StorageService;
import com.ssuamkiett.bookconnect.history.BookReadingHistory;
import com.ssuamkiett.bookconnect.history.BookReadingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookMapper {
    private final StorageService storageService;

    public Book toBook(BookRequest bookRequest) {
        return Book.builder()
                .title(bookRequest.title())
                .authorName(bookRequest.authorName())
                .isbn(bookRequest.isbn())
                .synopsis(bookRequest.synopsis())
                .genre(bookRequest.genre())
                .archived(false)
                .shareable(bookRequest.shareable())
                .build();

    }

    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .genre(book.getGenre())
                .rate(book.getRate())
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .owner(book.getOwner().fullName())
                .coverPhoto(storageService.getFullFilePath(book.getBookCover()))
                .build();

    }

    public BookResponse toBookResponseWithOwner(Book book) {
        BookResponse bookResponse = toBookResponse(book);
        bookResponse.setIsOwner(true);
        return bookResponse;
    }

    public BookReadingResponse toReadingBookResponse(BookReadingHistory bookTransactionHistory) {
        return BookReadingResponse.builder()
                .id(bookTransactionHistory.getBook().getId())
                .authorName(bookTransactionHistory.getBook().getAuthorName())
                .title(bookTransactionHistory.getBook().getTitle())
                .isbn(bookTransactionHistory.getBook().getIsbn())
                .genre(bookTransactionHistory.getBook().getGenre())
                .rate(bookTransactionHistory.getBook().getRate())
                .build();
    }
}
