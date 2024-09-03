package com.ssuamkiett.bookconnect.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {
    @Query("""
                  SELECT book
                  FROM Book book
                  WHERE book.archived = false
                  AND book.shareable = true
                  AND book.owner.id != :userId
            """)
    Page<Book> findAllDisplayableBooks(Pageable pageable, Integer userId);

    @Query("""
                  SELECT book
                  FROM Book book
                  WHERE book.archived = false
                  AND book.shareable = true
            """)
    Page<Book> findAllDisplayableBooks(Pageable pageable);

    @Query("""
                 SELECT book
                 FROM Book book
                 LEFT JOIN book.feedbacks feedback
                 WHERE book.archived = false
                 AND book.shareable = true
                 GROUP BY book
                 ORDER BY COUNT(feedback) DESC
            """)
    Page<Book> findPopularBooks(Pageable pageable);

    @Query("""
                 SELECT book
                 FROM Book book
                 LEFT JOIN book.feedbacks feedback
                 WHERE book.archived = false
                 AND book.shareable = true
                 AND book.owner.id != :userId
                 GROUP BY book
                 ORDER BY COUNT(feedback) DESC
            """)
    Page<Book> findPopularBooks(Pageable pageable, Integer userId);
}
