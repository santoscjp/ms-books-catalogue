package com.grupo5.ms_books_catalogue.service;

import com.grupo5.ms_books_catalogue.entity.Book;
import com.grupo5.ms_books_catalogue.exception.ResourceNotFoundException;
import com.grupo5.ms_books_catalogue.payload.BookFilter;
import com.grupo5.ms_books_catalogue.payload.BookRequest;
import com.grupo5.ms_books_catalogue.repository.BookRepository;
import com.grupo5.ms_books_catalogue.utils.BookSpecs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository repo) {
        this.bookRepository = repo;
    }

    @Override
    public Page<Book> list(Pageable page) {
        return bookRepository.findAll(page);
    }

    @Override
    public Book getById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id "+id));
    }

    @Override
    public Book create(BookRequest req) {
        Book b = mapToEntity(req);
        return bookRepository.save(b);
    }

    @Override
    public Book update(Long id, BookRequest req) {
        Book existing = getById(id);
        applyUpdates(existing, req);
        return bookRepository.save(existing);
    }

    @Override
    public Page<Book> search(BookFilter filter, Pageable page) {
        return bookRepository.findAll(BookSpecs.withFilter(filter), page);
    }

    @Override
    public void softDelete(Long id) {
        Book b = getById(id);
        b.setVisible(false);
        bookRepository.save(b);
    }

    // --- helpers ---
    private Book mapToEntity(BookRequest r) {
        Book b = new Book();
        b.setIsbn(r.getIsbn());
        b.setTitle(r.getTitle());
        b.setAuthor(r.getAuthor());
        b.setPublicationDate(r.getPublicationDate());
        b.setCategory(r.getCategory());
        b.setRating(r.getRating());
        b.setVisible(r.getVisible());
        b.setStock(r.getStock());
        return b;
    }

    private void applyUpdates(Book b, BookRequest r) {
        b.setIsbn(r.getIsbn());
        b.setTitle(r.getTitle());
        b.setAuthor(r.getAuthor());
        b.setPublicationDate(r.getPublicationDate());
        b.setCategory(r.getCategory());
        b.setRating(r.getRating());
        b.setVisible(r.getVisible());
        b.setStock(r.getStock());
    }
}

