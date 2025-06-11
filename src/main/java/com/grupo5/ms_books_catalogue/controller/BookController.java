package com.grupo5.ms_books_catalogue.controller;

import com.grupo5.ms_books_catalogue.payload.ApiResponse;
import com.grupo5.ms_books_catalogue.payload.BookRequest;
import com.grupo5.ms_books_catalogue.payload.BookResponse;
import com.grupo5.ms_books_catalogue.service.BookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/book")
@Validated
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("all")
    public ResponseEntity<ApiResponse<Page<BookResponse>>> list(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size
    ) {
        Page<BookResponse> result = bookService.list(PageRequest.of(page, size))
                .map(BookResponse::new);
        return ResponseEntity.ok(
                new ApiResponse<>(200,"SUCCESS" ,"Books retrieved", result)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> get(
            @PathVariable Long id
    ) {
        BookResponse r = new BookResponse(bookService.getById(id));
        return ResponseEntity.ok(
                new ApiResponse<>(200, "SUCCESS","Book found", r)
        );
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
            @PathVariable Long id,
            @Valid @RequestBody BookRequest req
    ) {
        BookResponse r = new BookResponse(bookService.update(id, req));
        return ResponseEntity.ok(
                new ApiResponse<>(200,"SUCCESS", "Book updated", r)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {
        bookService.softDelete(id);
        return ResponseEntity.ok(
                new ApiResponse<>(200,"SUCCESS", "Book marked as invisible", null)
        );
    }
}
