package com.hcl.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserNotFoundExceptionTest {

    @Test
    void testConstructorWithMessage() {
        UserNotFoundException exception = new UserNotFoundException("User with username 'nonexistent' not found.");
        assertEquals("User with username 'nonexistent' not found.", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        Throwable cause = new IllegalArgumentException("Invalid username format provided.");
        UserNotFoundException exception = new UserNotFoundException("Failed to retrieve user.", cause);
        assertEquals("Failed to retrieve user.", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionType() {
        UserNotFoundException exception = new UserNotFoundException("Test message");
        assertTrue(exception instanceof RuntimeException);
    }
}