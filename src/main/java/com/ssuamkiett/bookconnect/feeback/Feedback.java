package com.ssuamkiett.bookconnect.feeback;

import com.ssuamkiett.bookconnect.book.Book;
import com.ssuamkiett.bookconnect.common.BaseEntity;
import com.ssuamkiett.bookconnect.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Feedback extends BaseEntity {
    private Double note; // 1-5 stars
    private String comment;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
