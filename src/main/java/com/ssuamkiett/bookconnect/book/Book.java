package com.ssuamkiett.bookconnect.book;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ssuamkiett.bookconnect.common.BaseEntity;
import com.ssuamkiett.bookconnect.feeback.Feedback;
import com.ssuamkiett.bookconnect.history.BookReadingHistory;
import com.ssuamkiett.bookconnect.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@EqualsAndHashCode(callSuper = true)
public class Book extends BaseEntity {
    private String title;
    private String authorName;
    private String isbn;
    @Column(length = 1000)
    private String synopsis;
    private String bookCover;
    private String bookPDF;
    private String genre;
    private boolean archived;
    private boolean shareable;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;

    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookReadingHistory> bookReadingHistories;

    @Transient
    public double getRate() {
        if(feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        double rate = this.feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);
        return Math.round(rate * 10.0) / 10.0;
    }
}
