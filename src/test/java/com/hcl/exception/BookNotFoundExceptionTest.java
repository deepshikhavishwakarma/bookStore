package com.hcl.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BookNotFoundExceptionTest {

    @Test
    void testConstructorWithMessage() {
        BookNotFoundException exception = new BookNotFoundException("Book with ID 123 not found.");
        assertEquals("Book with ID 123 not found.", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        Throwable cause = new IllegalArgumentException("Invalid book ID format.");
        BookNotFoundException exception = new BookNotFoundException("Failed to retrieve book.", cause);
        assertEquals("Failed to retrieve book.", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionType() {
        BookNotFoundException exception = new BookNotFoundException("Test message");
        assertTrue(exception instanceof RuntimeException);
    }
}