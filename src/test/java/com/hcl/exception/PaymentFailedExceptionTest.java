package com.hcl.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentFailedExceptionTest {

    @Test
    void testConstructorWithMessage() {
        PaymentFailedException exception = new PaymentFailedException("Payment processing failed for order ID 202.");
        assertEquals("Payment processing failed for order ID 202.", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        Throwable cause = new RuntimeException("Connection to payment gateway timed out.");
        PaymentFailedException exception = new PaymentFailedException("Payment could not be completed.", cause);
        assertEquals("Payment could not be completed.", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionType() {
        PaymentFailedException exception = new PaymentFailedException("Test message");
        assertTrue(exception instanceof RuntimeException);
    }
}