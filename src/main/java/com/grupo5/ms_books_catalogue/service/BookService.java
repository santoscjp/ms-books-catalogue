package com.grupo5.ms_books_catalogue.service;

import com.grupo5.ms_books_catalogue.entity.Book;
import com.grupo5.ms_books_catalogue.payload.BookFilter;
import com.grupo5.ms_books_catalogue.payload.BookRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    Page<Book> list(Pageable page);
    Book getById(Long id);
    Book create(BookRequest req);
    Book update(Long id, BookRequest req);
    Page<Book> search(BookFilter filter, Pageable page);
    void softDelete(Long id);
}
