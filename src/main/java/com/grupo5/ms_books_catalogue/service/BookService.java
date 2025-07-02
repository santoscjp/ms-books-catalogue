package com.grupo5.ms_books_catalogue.service;

import com.grupo5.ms_books_catalogue.entity.Book;
import com.grupo5.ms_books_catalogue.payload.BookFilter;
import com.grupo5.ms_books_catalogue.payload.BookQueryResponse;
import com.grupo5.ms_books_catalogue.payload.BookRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    Book getById(String id);
    Book create(BookRequest request);
    Book update(String id, BookRequest req);
    BookQueryResponse search(String title,
                             String author,
                             String category,
                             String isbn,
                             String description);
    Boolean delete(String id);
}
