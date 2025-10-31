package com.school.feeservice.exception;

public class ReceiptNotFoundException extends RuntimeException {

    public ReceiptNotFoundException(String message){
        super(message);
    }
}
