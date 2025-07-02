package com.grupo5.ms_books_catalogue.repository;

import com.grupo5.ms_books_catalogue.entity.Book;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository
        extends ElasticsearchRepository<Book, String>{
    Optional<Book> findById(String id);
    Book save(Book book);
    void delete(Book book);


}
