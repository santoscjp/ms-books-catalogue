package com.grupo5.ms_books_catalogue.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor
public class BookFilter {
    private String isbn;
    private String title;
    private String author;
    private String category;
    private Integer minRating;
    private Integer maxRating;
    private Boolean visible;
    private LocalDate fromDate;
    private LocalDate toDate;
}