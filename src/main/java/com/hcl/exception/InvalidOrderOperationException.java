package com.hcl.exception;

public class InvalidOrderOperationException extends RuntimeException {
    public InvalidOrderOperationException(String message) {
        super(message);
    }

    public InvalidOrderOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}