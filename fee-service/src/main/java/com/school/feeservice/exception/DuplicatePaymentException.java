package com.school.feeservice.exception;

public class DuplicatePaymentException extends RuntimeException {
    public DuplicatePaymentException(String message) {
        super(message);
    }
}
