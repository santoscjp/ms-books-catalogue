package com.grupo5.ms_books_catalogue.payload;

import com.grupo5.ms_books_catalogue.entity.Book;

import java.time.LocalDate;

@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
public class BookResponse {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private LocalDate publicationDate;
    private String category;
    private Integer rating;
    private Boolean visible;
    private Integer stock;

    public BookResponse(Book book) {
        this.id               = book.getId();
        this.isbn             = book.getIsbn();
        this.title            = book.getTitle();
        this.author           = book.getAuthor();
        this.publicationDate  = book.getPublicationDate();
        this.category         = book.getCategory();
        this.rating           = book.getRating();
        this.visible          = book.getVisible();
        this.stock            = book.getStock();
    }
}
