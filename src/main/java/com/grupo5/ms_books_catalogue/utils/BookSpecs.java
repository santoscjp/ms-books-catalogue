package com.grupo5.ms_books_catalogue.utils;

import com.grupo5.ms_books_catalogue.entity.Book;
import com.grupo5.ms_books_catalogue.payload.BookFilter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecs {

    public static Specification<Book> withFilter(BookFilter f) {
        return (root, query, cb) -> {
            Predicate p = cb.conjunction();

            if (f.getIsbn() != null) {
                p = cb.and(p, cb.equal(root.get("isbn"), f.getIsbn()));
            }
            if (f.getTitle() != null) {
                p = cb.and(p, cb.like(cb.lower(root.get("title")), "%" + f.getTitle().toLowerCase() + "%"));
            }
            if (f.getAuthor() != null) {
                p = cb.and(p, cb.like(cb.lower(root.get("author")), "%" + f.getAuthor().toLowerCase() + "%"));
            }
            if (f.getCategory() != null) {
                p = cb.and(p, cb.equal(root.get("category"), f.getCategory()));
            }
            if (f.getMinRating() != null) {
                p = cb.and(p, cb.ge(root.get("rating"), f.getMinRating()));
            }
            if (f.getMaxRating() != null) {
                p = cb.and(p, cb.le(root.get("rating"), f.getMaxRating()));
            }
            if (f.getVisible() != null) {
                p = cb.and(p, cb.equal(root.get("visible"), f.getVisible()));
            }
            if (f.getFromDate() != null) {
                p = cb.and(p, cb.greaterThanOrEqualTo(root.get("publicationDate"), f.getFromDate()));
            }
            if (f.getToDate() != null) {
                p = cb.and(p, cb.lessThanOrEqualTo(root.get("publicationDate"), f.getToDate()));
            }
            return p;
        };
    }
}