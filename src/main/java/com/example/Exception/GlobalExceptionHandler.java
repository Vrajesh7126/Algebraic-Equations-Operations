package com.example.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Handles exceptions globally for all controllers.
 * Provides custom responses for specific and generic exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles EquationNotFoundException and returns a NOT_FOUND response.
     *
     * @param ex the EquationNotFoundException thrown
     * @return a ResponseEntity with error details and NOT_FOUND status
     */
    @ExceptionHandler(EquationNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEquationNotFoundException(EquationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    /**
     * Handles generic RuntimeExceptions and returns a BAD_REQUEST response.
     *
     * @param ex the RuntimeException thrown
     * @return a ResponseEntity with error details and BAD_REQUEST status
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }
}