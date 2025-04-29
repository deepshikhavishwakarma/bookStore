package com.hcl.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderNotFoundExceptionTest {

    @Test
    void testConstructorWithMessage() {
        OrderNotFoundException exception = new OrderNotFoundException("Order with ID 101 not found.");
        assertEquals("Order with ID 101 not found.", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        Throwable cause = new IllegalArgumentException("Invalid order ID format.");
        OrderNotFoundException exception = new OrderNotFoundException("Failed to retrieve order details.", cause);
        assertEquals("Failed to retrieve order details.", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionType() {
        OrderNotFoundException exception = new OrderNotFoundException("Test message");
        assertTrue(exception instanceof RuntimeException);
    }
}