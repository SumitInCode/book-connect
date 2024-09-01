package com.ssuamkiett.bookconnect.book;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {
    private final BookService bookService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookResponse> saveBook(
            @Valid @RequestPart("formData") BookRequest bookRequest,
            @NotNull @RequestPart("coverPhoto") MultipartFile coverPhoto,
            Authentication connectUser) {
        return ResponseEntity.ok(bookService.save(bookRequest, coverPhoto, connectUser));
    }

    @DeleteMapping("/delete/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable Integer bookId, Authentication connectedUser) {
        bookService.delete(bookId, connectedUser);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/book/{book-id}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable("book-id") Integer bookId, Authentication connectedUser) {
        return ResponseEntity.ok(bookService.findById(bookId, connectedUser));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectUser) {
        return ResponseEntity.ok(bookService.findAllBooks(page, size, connectUser));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByTitle(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @RequestParam(name = "query") String searchQuery) {
        return ResponseEntity.ok(bookService.findAllBooksByTitle(page, size, searchQuery));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectUser) {
        return ResponseEntity.ok(bookService.findAllBooksByOwner(page, size, connectUser));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectUser) {
        return ResponseEntity.ok(bookService.findAllBorrowedBooks(page, size, connectUser));
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectUser) {
        return ResponseEntity.ok(bookService.findAllReturnedBooks(page, size, connectUser));
    }

    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateShareableStatus(
            @PathVariable("book-id") Integer bookId, Authentication connectedUser) {
        return ResponseEntity.ok(bookService.updateShareableStatus(bookId, connectedUser));
    }

    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Integer> updateArchivedStatus(
            @PathVariable("book-id") Integer bookId, Authentication connectedUser) {
        return ResponseEntity.ok(bookService.updateArchivedStatus(bookId, connectedUser));
    }

    @PostMapping("/borrow/{book-id}")
    public ResponseEntity<Integer> borrowBook(@PathVariable("book-id") Integer bookId, Authentication connectedUser) {
        return ResponseEntity.ok(bookService.borrowBook(bookId, connectedUser));
    }

    @PatchMapping("/borrow/return/{book-id}")
    public ResponseEntity<Integer> returnBorrowBook(@PathVariable("book-id") Integer bookId, Authentication connectedUser) {
        return ResponseEntity.ok(bookService.returnBorrowBook(bookId, connectedUser));
    }

    @PatchMapping("/borrow/return/approve/{book-id}")
    public ResponseEntity<Integer> approveReturnBorrowBook(@PathVariable("book-id") Integer bookId, Authentication connectedUser) {
        return ResponseEntity.ok(bookService.approveReturnBorrowBook(bookId, connectedUser));
    }

    @PostMapping(value = "/cover/{book-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadBookCoverPicture(
            @PathVariable("book-id") Integer bookId,
            @Parameter()
            @RequestPart("coverPhoto") MultipartFile coverPhoto,
            Authentication connectedUser) {
        bookService.uploadBookCoverPicture(bookId, coverPhoto, connectedUser);
        return ResponseEntity.accepted().build();
    }

    @PostMapping(value = "/pdf/{bookId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @NotNull @PathVariable("bookId") Integer bookId,
            @RequestPart("file") MultipartFile file,
            Authentication connectedUser) {
        bookService.uploadBookPDF(file, bookId, connectedUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pdf/{bookId}")
    public ResponseEntity<?> getFile(@PathVariable("bookId") Integer bookId, Authentication connectedUser) {
        byte[] file = bookService.getBookPDF(bookId, connectedUser);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.OK).
                contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }
}
