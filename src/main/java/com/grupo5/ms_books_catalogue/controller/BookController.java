package com.grupo5.ms_books_catalogue.controller;

import java.util.Map;

import com.grupo5.ms_books_catalogue.payload.*;
import com.grupo5.ms_books_catalogue.service.BookService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/books")
@Validated
@Slf4j
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> get(
            @PathVariable String id
    ) {
        BookResponse r = new BookResponse(bookService.getById(id));
        return ResponseEntity.ok(
                new ApiResponse<>(200, "SUCCESS","Book found", r)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<BookQueryResponse>> search(
            @RequestHeader Map<String, String> headers,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String description
    ) {
        log.info("headers: {}", headers);
        BookQueryResponse pageResult = bookService.search(title, author, category, isbn, description);

        ApiResponse<BookQueryResponse> resp = new ApiResponse<>(
                HttpStatus.OK.value(),
                "SUCCESS",
                "Books search results",
                pageResult
        );
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<BookResponse>> create(
            @Valid @RequestBody BookRequest req
    ) {
        BookResponse r = new BookResponse(bookService.create(req));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "SUCCESS", "Book created", r));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody BookRequest req
    ) {
        BookResponse r = new BookResponse(bookService.update(id, req));
        return ResponseEntity.ok(
                new ApiResponse<>(200,"SUCCESS", "Book updated", r)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String id
    ) {
        bookService.delete(id);
        return ResponseEntity.ok(
                new ApiResponse<>(200,"SUCCESS", "Book marked as invisible", null)
        );
    }

    @GetMapping("/fulltext")
    public ResponseEntity<ApiResponse<?>> fullTextSearch(@RequestParam String q) {
        return ResponseEntity.ok(
                new ApiResponse<>(200, "SUCCESS", "Full-text search result",
                        bookService.fullTextSearch(q))
        );
    }

    @GetMapping("/suggest")
    public ResponseEntity<ApiResponse<?>> suggest(@RequestParam String q) {
        return ResponseEntity.ok(
                new ApiResponse<>(200, "SUCCESS", "Autocomplete suggestions",
                        bookService.suggest(q))
        );
    }

    @GetMapping("/correct")
    public ResponseEntity<ApiResponse<?>> correct(@RequestParam String q) {
        return ResponseEntity.ok(
                new ApiResponse<>(200, "SUCCESS", "Spellcheck suggestions",
                        bookService.correct(q))
        );
    }
}
