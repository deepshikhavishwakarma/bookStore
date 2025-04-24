package com.hcl.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class CartItemTest {

    @Test
    void testGettersAndSetters() {
        CartItem cartItem = new CartItem();

        // Mock related entities
        Book mockBook = mock(Book.class);
        User mockUser = mock(User.class);

        // Set values using setters
        cartItem.setId(1L);
        cartItem.setQuantity(3);
        cartItem.setBook(mockBook);
        cartItem.setUser(mockUser);

        // Verify values using getters
        assertEquals(1L, cartItem.getId());
        assertEquals(3, cartItem.getQuantity());
        assertEquals(mockBook, cartItem.getBook());
        assertEquals(mockUser, cartItem.getUser());
    }

    @Test
    void testDefaultConstructor() {
        CartItem cartItem = new CartItem();
        assertNull(cartItem.getId());
        assertEquals(0, cartItem.getQuantity()); // Default int value
        assertNull(cartItem.getBook());
        assertNull(cartItem.getUser());
    }

    @Test
    void testSettersWithNullValues() {
        CartItem cartItem = new CartItem();

        cartItem.setId(null);
        cartItem.setBook(null);
        cartItem.setUser(null);

        assertNull(cartItem.getId());
        assertNull(cartItem.getBook());
        assertNull(cartItem.getUser());
        assertEquals(0, cartItem.getQuantity()); // Quantity is a primitive int, so it defaults to 0
    }
}