package com.hcl.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class OrderTest {

    @Test
    void testGettersAndSetters() {
        Order order = new Order();
        User mockUser = mock(User.class);
        LocalDate orderDate = LocalDate.now();
        LocalDate paymentDate = LocalDate.now().plusDays(1);
        List<OrderItem> mockItems = Arrays.asList(mock(OrderItem.class), mock(OrderItem.class));

        order.setId(1L);
        order.setUser(mockUser);
        order.setTotalPrice(75.50);
        order.setOrderDate(orderDate);
        order.setPaymentDate(paymentDate);
        order.setStatus("PROCESSING");
        order.setItems(mockItems);

        assertEquals(1L, order.getId());
        assertEquals(mockUser, order.getUser());
        assertEquals(75.50, order.getTotalPrice(), 0.001);
        assertEquals(orderDate, order.getOrderDate());
        assertEquals(paymentDate, order.getPaymentDate());
        assertEquals("PROCESSING", order.getStatus());
        assertEquals(mockItems, order.getItems());
    }

    @Test
    void testDefaultConstructor() {
        Order order = new Order();
        assertNull(order.getId());
        assertNull(order.getUser());
        assertEquals(0.0, order.getTotalPrice(), 0.001);
        assertNull(order.getOrderDate());
        assertNull(order.getPaymentDate());
        assertNull(order.getStatus());
        assertNull(order.getItems());
    }

    @Test
    void testSettersWithNullValues() {
        Order order = new Order();

        order.setId(null);
        order.setUser(null);
        order.setOrderDate(null);
        order.setPaymentDate(null);
        order.setStatus(null);
        order.setItems(null);

        assertNull(order.getId());
        assertNull(order.getUser());
        assertEquals(0.0, order.getTotalPrice(), 0.001);
        assertNull(order.getOrderDate());
        assertNull(order.getPaymentDate());
        assertNull(order.getStatus());
        assertNull(order.getItems());
    }

    @Test
    void testSettersWithDifferentValues() {
        Order order = new Order();
        User mockUser1 = mock(User.class);
        User mockUser2 = mock(User.class);
        LocalDate date1 = LocalDate.of(2025, 4, 24);
        LocalDate date2 = LocalDate.of(2025, 4, 25);
        List<OrderItem> items1 = Arrays.asList(mock(OrderItem.class));
        List<OrderItem> items2 = Arrays.asList(mock(OrderItem.class), mock(OrderItem.class), mock(OrderItem.class));

        order.setId(100L);
        order.setUser(mockUser2);
        order.setTotalPrice(120.75);
        order.setOrderDate(date2);
        order.setPaymentDate(date1);
        order.setStatus("SHIPPED");
        order.setItems(items2);

        assertEquals(100L, order.getId());
        assertEquals(mockUser2, order.getUser());
        assertEquals(120.75, order.getTotalPrice(), 0.001);
        assertEquals(date2, order.getOrderDate());
        assertEquals(date1, order.getPaymentDate());
        assertEquals("SHIPPED", order.getStatus());
        assertEquals(items2, order.getItems());
    }
}