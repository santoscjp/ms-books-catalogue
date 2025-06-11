package com.grupo5.ms_books_catalogue.exception;

import com.grupo5.ms_books_catalogue.payload.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        ApiResponse<Void> resp = new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(),
                "ERROR",
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String fullMessage = Optional.ofNullable(ex.getMostSpecificCause())
                .map(Throwable::toString)
                .orElse(ex.toString());
        ApiResponse<Void> resp = new ApiResponse<>(
                HttpStatus.CONFLICT.value(),
                "ERROR",
                fullMessage,
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(resp);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        err -> err.getField(),
                        err -> err.getDefaultMessage(),
                        (existing, replacement) -> existing + "; " + replacement
                ));
        String fullMessage = ex.toString();
        ApiResponse<Map<String, String>> resp = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "ERROR",
                fullMessage,
                errors
        );
        return ResponseEntity.badRequest().body(resp);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAll(Exception ex) {
        String fullMessage = ex.toString();
        ApiResponse<Void> resp = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "ERROR",
                fullMessage,
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }
}
