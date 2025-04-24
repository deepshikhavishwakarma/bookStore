package com.hcl.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testGettersAndSetters() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setCountry("USA");
        user.setRole("ADMIN");

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("USA", user.getCountry());
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    void testDefaultConstructor() {
        User user = new User();
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getEmail());
        assertNull(user.getCountry());
        assertNull(user.getRole());
    }

    @Test
    void testSettersWithNullValues() {
        User user = new User();
        user.setId(null);
        user.setUsername(null);
        user.setPassword(null);
        user.setEmail(null);
        user.setCountry(null);
        user.setRole(null);

        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getEmail());
        assertNull(user.getCountry());
        assertNull(user.getRole());
    }

    @Test
    void testSettersWithDifferentValues() {
        User user = new User();
        user.setId(10L);
        user.setUsername("anotheruser");
        user.setPassword("securePass");
        user.setEmail("another@test.com");
        user.setCountry("Canada");
        user.setRole("USER");

        assertEquals(10L, user.getId());
        assertEquals("anotheruser", user.getUsername());
        assertEquals("securePass", user.getPassword());
        assertEquals("another@test.com", user.getEmail());
        assertEquals("Canada", user.getCountry());
        assertEquals("USER", user.getRole());
    }
}