package com.grupo5.ms_books_catalogue.payload;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {
    @NotBlank
    private String isbn;

    @NotBlank
    private String title;

    @Size(max=255)
    private String author;

    @PastOrPresent
    private LocalDate publicationDate;

    @Size(max=100)
    private String category;

    @Min(1) @Max(5)
    private Integer rating;

    @NotNull
    private Boolean visible;

    @Min(0)
    private Integer stock;

}
