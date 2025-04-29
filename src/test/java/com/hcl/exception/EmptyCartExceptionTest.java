package com.hcl.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmptyCartExceptionTest {

    @Test
    void testConstructorWithMessage() {
        EmptyCartException exception = new EmptyCartException("The shopping cart is empty.");
        assertEquals("The shopping cart is empty.", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        Throwable cause = new IllegalStateException("Cannot proceed with checkout on an empty cart.");
        EmptyCartException exception = new EmptyCartException("Checkout failed.", cause);
        assertEquals("Checkout failed.", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionType() {
        EmptyCartException exception = new EmptyCartException("Test message");
        assertTrue(exception instanceof RuntimeException);
    }
}