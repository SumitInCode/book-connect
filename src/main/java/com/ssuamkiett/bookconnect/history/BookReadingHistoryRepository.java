package com.ssuamkiett.bookconnect.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BookReadingHistoryRepository extends JpaRepository<BookReadingHistory, Integer> {
    void deleteByUserIdAndBookId(Integer userId, Integer bookId);
    boolean existsByUserIdAndBookId(Integer userId, Integer bookId);
    @Query("""
                    SELECT history
                    From BookReadingHistory history
                    WHERE history.user.id = :userId
            """)
    Page<BookReadingHistory> findAllBookReadingHistories(Pageable pageable, Integer userId);
}
