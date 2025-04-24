package com.hcl.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InsufficientStockExceptionTest {

    @Test
    void testConstructorWithMessage() {
        InsufficientStockException exception = new InsufficientStockException("Insufficient stock for book ID 10.");
        assertEquals("Insufficient stock for book ID 10.", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        Throwable cause = new IllegalArgumentException("Requested quantity exceeds available stock.");
        InsufficientStockException exception = new InsufficientStockException("Order cannot be placed.", cause);
        assertEquals("Order cannot be placed.", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionType() {
        InsufficientStockException exception = new InsufficientStockException("Test message");
        assertTrue(exception instanceof RuntimeException);
    }
}