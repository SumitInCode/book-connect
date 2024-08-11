package com.ssuamkiett.BookConnect.file;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table
public class File {
    @Id
    private Integer id;
    private String name;
    private String type;
    @Lob
    @Column(length=100000)
    private byte[] data;
}
