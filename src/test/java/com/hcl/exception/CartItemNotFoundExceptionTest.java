package com.hcl.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CartItemNotFoundExceptionTest {

    @Test
    void testConstructorWithMessage() {
        CartItemNotFoundException exception = new CartItemNotFoundException("Cart item with ID 5 not found.");
        assertEquals("Cart item with ID 5 not found.", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        Throwable cause = new IllegalArgumentException("Invalid cart item ID format.");
        CartItemNotFoundException exception = new CartItemNotFoundException("Failed to retrieve cart item.", cause);
        assertEquals("Failed to retrieve cart item.", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionType() {
        CartItemNotFoundException exception = new CartItemNotFoundException("Test message");
        assertTrue(exception instanceof RuntimeException);
    }
}