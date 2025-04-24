package com.hcl.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class OrderItemTest {

    @Test
    void testGettersAndSetters() {
        OrderItem orderItem = new OrderItem();
        Order mockOrder = mock(Order.class);
        Book mockBook = mock(Book.class);

        orderItem.setId(1L);
        orderItem.setBookName("The Hitchhiker's Guide to the Galaxy");
        orderItem.setPrice(19.99);
        orderItem.setQuantity(2);
        orderItem.setOrder(mockOrder);
        orderItem.setBook(mockBook);

        assertEquals(1L, orderItem.getId());
        assertEquals("The Hitchhiker's Guide to the Galaxy", orderItem.getBookName());
        assertEquals(19.99, orderItem.getPrice(), 0.001);
        assertEquals(2, orderItem.getQuantity());
        assertEquals(mockOrder, orderItem.getOrder());
        assertEquals(mockBook, orderItem.getBook());
    }

    @Test
    void testDefaultConstructor() {
        OrderItem orderItem = new OrderItem();
        assertNull(orderItem.getId());
        assertNull(orderItem.getBookName());
        assertNull(orderItem.getPrice());
        assertEquals(0, orderItem.getQuantity());
        assertNull(orderItem.getOrder());
        assertNull(orderItem.getBook());
    }

    @Test
    void testSettersWithNullValues() {
        OrderItem orderItem = new OrderItem();

        orderItem.setId(null);
        orderItem.setBookName(null);
        orderItem.setPrice(null);
        orderItem.setOrder(null);
        orderItem.setBook(null);

        assertNull(orderItem.getId());
        assertNull(orderItem.getBookName());
        assertNull(orderItem.getPrice());
        assertEquals(0, orderItem.getQuantity());
        assertNull(orderItem.getOrder());
        assertNull(orderItem.getBook());
    }

    @Test
    void testSettersWithDifferentValues() {
        OrderItem orderItem = new OrderItem();
        Order mockOrder1 = mock(Order.class);
        Order mockOrder2 = mock(Order.class);
        Book mockBook1 = mock(Book.class);
        Book mockBook2 = mock(Book.class);

        orderItem.setId(5L);
        orderItem.setBookName("Foundation");
        orderItem.setPrice(15.50);
        orderItem.setQuantity(1);
        orderItem.setOrder(mockOrder2);
        orderItem.setBook(mockBook1);

        assertEquals(5L, orderItem.getId());
        assertEquals("Foundation", orderItem.getBookName());
        assertEquals(15.50, orderItem.getPrice(), 0.001);
        assertEquals(1, orderItem.getQuantity());
        assertEquals(mockOrder2, orderItem.getOrder());
        assertEquals(mockBook1, orderItem.getBook());
    }
}