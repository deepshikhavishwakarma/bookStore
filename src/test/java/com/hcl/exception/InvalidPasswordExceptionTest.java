package com.hcl.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InvalidPasswordExceptionTest {

    @Test
    void testConstructorWithMessage() {
        InvalidPasswordException exception = new InvalidPasswordException("The password you entered is incorrect.");
        assertEquals("The password you entered is incorrect.", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        Throwable cause = new IllegalArgumentException("Password does not meet complexity requirements.");
        InvalidPasswordException exception = new InvalidPasswordException("Failed to update password.", cause);
        assertEquals("Failed to update password.", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionType() {
        InvalidPasswordException exception = new InvalidPasswordException("Test message");
        assertTrue(exception instanceof RuntimeException);
    }
}