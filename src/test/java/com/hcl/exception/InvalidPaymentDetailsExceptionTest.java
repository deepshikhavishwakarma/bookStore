package com.hcl.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidPaymentDetailsExceptionTest {

    @Test
    void testConstructorWithMessage() {
        InvalidPaymentDetailsException exception = new InvalidPaymentDetailsException("Invalid credit card number.");
        assertEquals("Invalid credit card number.", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        Throwable cause = new IllegalArgumentException("Expiration date is in the past.");
        InvalidPaymentDetailsException exception = new InvalidPaymentDetailsException("Payment processing failed.", cause);
        assertEquals("Payment processing failed.", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionType() {
        InvalidPaymentDetailsException exception = new InvalidPaymentDetailsException("Test message");
        assertTrue(exception instanceof RuntimeException);
    }
}