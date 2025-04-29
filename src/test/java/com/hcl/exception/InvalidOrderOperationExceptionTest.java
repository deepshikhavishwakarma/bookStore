package com.hcl.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidOrderOperationExceptionTest {

    @Test
    void testConstructorWithMessage() {
        InvalidOrderOperationException exception = new InvalidOrderOperationException("Cannot cancel an already shipped order.");
        assertEquals("Cannot cancel an already shipped order.", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        Throwable cause = new IllegalStateException("Order status does not allow this operation.");
        InvalidOrderOperationException exception = new InvalidOrderOperationException("Failed to perform order operation.", cause);
        assertEquals("Failed to perform order operation.", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionType() {
        InvalidOrderOperationException exception = new InvalidOrderOperationException("Test message");
        assertTrue(exception instanceof RuntimeException);
    }
}