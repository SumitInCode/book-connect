package com.ssuamkiett.bookconnect.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BorrowedBookResponse {
    private Integer id;
    private String title;
    private String authorName;
    private String isbn;
    private String genre;
    private double rate;
    private boolean returned;
    private boolean returnApproved;
}
