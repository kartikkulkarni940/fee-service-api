package com.school.feeservice.exception;

import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 🔹 Handles validation errors like "Card number required"
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException ex) {
        log.warn("Validation failed: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", Instant.now(),
                "status", 400,
                "error", "Bad Request",
                "message", ex.getMessage()
        ));
    }

    // 🔹 Handles when student is not found (from Student Service)
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<?> handleStudentNotFound(StudentNotFoundException ex) {
        log.warn("Student not found: {}", ex.getMessage());
        return ResponseEntity.status(404).body(Map.of(
                "timestamp", Instant.now(),
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage()
        ));
    }

    // 🔹 Handles when receipt not found
    @ExceptionHandler(ReceiptNotFoundException.class)
    public ResponseEntity<?> handleReceiptNotFound(ReceiptNotFoundException ex) {
        log.warn("Receipt not found: {}", ex.getMessage());
        return ResponseEntity.status(404).body(Map.of(
                "timestamp", Instant.now(),
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage()
        ));
    }

    // 🔹 Generic fallback for any unexpected exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(500).body(Map.of(
                "timestamp", Instant.now(),
                "status", 500,
                "error", "Internal Server Error",
                "message", ex.getMessage()
        ));
    }
    @ExceptionHandler(DuplicatePaymentException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicatePayment(DuplicatePaymentException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Duplicate Payment");
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }


}
