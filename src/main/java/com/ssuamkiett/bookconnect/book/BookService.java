package com.ssuamkiett.bookconnect.book;

import com.ssuamkiett.bookconnect.exception.OperationNotPermittedException;
import com.ssuamkiett.bookconnect.file.StorageService;
import com.ssuamkiett.bookconnect.file.FileType;
import com.ssuamkiett.bookconnect.history.BookReadStatus;
import com.ssuamkiett.bookconnect.history.BookReadingHistoryService;
import com.ssuamkiett.bookconnect.history.BookReadingResponse;
import com.ssuamkiett.bookconnect.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;

import static com.ssuamkiett.bookconnect.Constants.MAX_FILE_SIZE;
import static com.ssuamkiett.bookconnect.book.BookSpecification.*;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookReadingHistoryService bookReadingHistoryService;
    private final StorageService storageService;

    public BookResponse save(BookRequest bookRequest, MultipartFile coverPhoto, Authentication connectedUser) {
        if(coverPhoto.getSize() > MAX_FILE_SIZE) {
            throw new OperationNotPermittedException("File size should not exceed 1MB.");
        }
        String contentType = coverPhoto.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            throw new OperationNotPermittedException("File type is not supported. Only JPG or PNG are supported.");
        }

        User user = (User) connectedUser.getPrincipal();
        Book book = bookMapper.toBook(bookRequest);
        book.setOwner(user);
        Integer bookId = bookRepository.save(book).getId();
        String coverPhotoPath = storageService.saveFile(coverPhoto, user.getId(), bookId, FileType.BOOK_COVER_PHOTO);
        book.setBookCover(coverPhotoPath);
        bookRepository.save(book);
        return BookResponse.builder()
                .id(bookId)
                .build();
    }

    public BookResponse findById(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID : " + bookId));
        if(connectedUser == null) {
            return findById(book);
        }

        User user = (User) connectedUser.getPrincipal();
        if(Objects.equals(book.getOwner().getId(), user.getId())) {
            return bookMapper.toBookResponseWithOwner(book);
        }

        if(book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("Operation not permitted!");
        }

        return bookMapper.toBookResponse(book);
    }

    private BookResponse findById(Book book) {
        if(book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("Operation not permitted!");
        }
        return bookMapper.toBookResponse(book);
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        if(connectedUser == null) {
            return findAllBooks(pageable);
        }
        User user = (User) connectedUser.getPrincipal();
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    private PageResponse<BookResponse> findAllBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable);
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }
    public void addReadBook(Integer bookId, Authentication connectedUser) {
        Book book = getBookFromDB(bookId);
        if(book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("Operation not permitted to retrieve book");
        }
        User user = (User) connectedUser.getPrincipal();
        bookReadingHistoryService.addBookToReading(user, book);
    }

    public PageResponse<BookReadingResponse> findAllReadingBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        return bookReadingHistoryService.findAllReadingBooksByOwner(page, size, user.getId());
    }

    public void removeReadingBook(int bookId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        bookReadingHistoryService.removeBookFromReading(user.getId(), bookId);
    }



    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = getBookFromDB(bookId);
        User user = (User) connectedUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot updated book other shareable status");
        }
        book.setShareable(book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = getBookFromDB(bookId);
        User user = (User) connectedUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot updated other book archived status");
        }
        book.setArchived(book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    public void uploadBookCoverPicture(Integer bookId, MultipartFile file, Authentication connectedUser) {
        Book book = getBookFromDB(bookId);
        User user = (User) connectedUser.getPrincipal();
        var bookCover = storageService.saveFile(file, user.getId(), bookId, FileType.BOOK_COVER_PHOTO);
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }

    @Transactional
    public void delete(Integer bookId, Authentication connectedUser) {
        Book book = getBookFromDB(bookId);
        User user = (User) connectedUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Operation Not permitted to delete book");
        }
        bookRepository.delete(book);
    }

    private Book getBookFromDB(Integer bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID : " + bookId));
    }

    public void uploadBookPDF(MultipartFile file, Integer bookId, Authentication connectedUser) {
        Book book = getBookFromDB(bookId);
        User user = (User) connectedUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Operation Not permitted!!");
        }

        String bookPDFFilePath = storageService.saveFile(file, user.getId(), bookId, FileType.BOOK_PDF);
        book.setBookPDF(bookPDFFilePath);
        bookRepository.save(book);
    }

    public InputStreamResource streamBookFile(Integer bookId, Authentication connectedUser) {
        Book book = getBookFromDB(bookId);
        User user = (User) connectedUser.getPrincipal();
        if(Objects.equals(book.getOwner().getId(), user.getId())) {
            storageService.readFileFromLocation(book.getBookPDF());
        }
        if(book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("Operation not permitted to retrieve book");

        }

        try {
            FileInputStream resource = new FileInputStream(book.getBookPDF());
            return new InputStreamResource(resource);
        }
        catch (FileNotFoundException e) {
            throw new EntityNotFoundException("Book file not found");
        }
    }

    public PageResponse<BookResponse> findAllBooksByTitle(int page, int size, String searchQuery) {
        Specification<Book> specification = hasKeywordInTitle(searchQuery).and(isNotArchived()).and(isSharable());
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        Page<Book> books = bookRepository.findAll(specification, pageable);
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public BookReadStatus getReadingStatus(Integer bookId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        return bookReadingHistoryService.getReadingStatus(user.getId(), bookId);
    }
}
