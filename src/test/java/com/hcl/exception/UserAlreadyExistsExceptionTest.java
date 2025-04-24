package com.hcl.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserAlreadyExistsExceptionTest {

    @Test
    void testConstructorWithMessage() {
        UserAlreadyExistsException exception = new UserAlreadyExistsException("Username 'existinguser' is already taken.");
        assertEquals("Username 'existinguser' is already taken.", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        Throwable cause = new IllegalArgumentException("Attempted to create a user with a duplicate email.");
        UserAlreadyExistsException exception = new UserAlreadyExistsException("Registration failed.", cause);
        assertEquals("Registration failed.", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionType() {
        UserAlreadyExistsException exception = new UserAlreadyExistsException("Test message");
        assertTrue(exception instanceof RuntimeException);
    }
}