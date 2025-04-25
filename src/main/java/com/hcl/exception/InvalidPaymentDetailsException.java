package com.hcl.exception;

@SuppressWarnings("serial")
public class InvalidPaymentDetailsException extends RuntimeException {
    public InvalidPaymentDetailsException(String message) {
        super(message);
    }

    public InvalidPaymentDetailsException(String message, Throwable cause) {
        super(message, cause);
    }
}
