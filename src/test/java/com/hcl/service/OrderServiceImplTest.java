package com.hcl.service;

import com.hcl.entity.Book;
import com.hcl.entity.CartItem;
import com.hcl.entity.Order;
import com.hcl.entity.OrderItem;
import com.hcl.entity.User;
import com.hcl.exception.EmptyCartException;
import com.hcl.exception.UserNotFoundException;
import com.hcl.repository.BookRepository;
import com.hcl.repository.CartItemRepository;
import com.hcl.repository.OrderItemRepository;
import com.hcl.repository.OrderRepository;
import com.hcl.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShoppingCartService shoppingCartService;

    @InjectMocks
    private OrderServiceImpl orderService;

   /* @Test
    void placeOrder_emptyCart_throwsEmptyCartException() {
        // Given
        Long userId = 1L;
        when(cartItemRepository.findByUser_id(userId)).thenReturn(Collections.emptyList());

        // When, Then
        assertThrows(EmptyCartException.class, () ->
                orderService.placeOrder(userId, Collections.emptyList(), "1111-2222-3333-4444", "12/25", "123")
        );
        verify(userRepository, never()).findById(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
        verify(bookRepository, never()).findById(anyLong());
        verify(orderItemRepository, never()).save(any(OrderItem.class));
    }*/

   /* @Test
    void placeOrder_userNotFound_throwsUserNotFoundException() {
        // Given
        Long userId = 1L;
        List<CartItem> cartItems = Collections.singletonList(new CartItem());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When, Then
        assertThrows(UserNotFoundException.class, () ->
                orderService.placeOrder(userId, cartItems, "1111-2222-3333-4444", "12/25", "123")
        );
        verify(orderRepository, never()).save(any(Order.class));
        verify(bookRepository, never()).findById(anyLong());
        verify(orderItemRepository, never()).save(any(OrderItem.class));
    }*/

   /* @Test
    void placeOrder_bookNotFound_throwsRuntimeException() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Book book = new Book();
        book.setBookId(10L);
        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(1);
        List<CartItem> cartItems = Collections.singletonList(cartItem);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.empty());

        // When, Then
        assertThrows(RuntimeException.class, () ->
                orderService.placeOrder(userId, cartItems, "1111-2222-3333-4444", "12/25", "123")
        );
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).save(any(OrderItem.class));
    }*/

	/*
	 * @Test void placeOrder_insufficientStock_throwsRuntimeException() { // Given
	 * Long userId = 1L; User user = new User(); user.setId(userId); Book book = new
	 * Book("Test Book", "Test Author", 0, 10.0); book.setBookId(10L); CartItem
	 * cartItem = new CartItem(); cartItem.setBook(book); cartItem.setQuantity(1);
	 * List<CartItem> cartItems = Collections.singletonList(cartItem);
	 * 
	 * when(userRepository.findById(userId)).thenReturn(Optional.of(user));
	 * when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
	 * 
	 * // When, Then assertThrows(RuntimeException.class, () ->
	 * orderService.placeOrder(userId, cartItems, "1111-2222-3333-4444", "12/25",
	 * "123") ); verify(orderRepository, times(1)).save(any(Order.class)); // Order
	 * is saved initially verify(bookRepository, times(1)).findById(10L);
	 * verify(orderItemRepository, never()).save(any()); verify(shoppingCartService,
	 * never()).clearCartAfterPayment(anyLong(), anyLong()); }
	 */
    
	/*
	 * @Test void placeOrder_insufficientStock_throwsRuntimeException() { // Given
	 * Long userId = 1L; User user = new User(); user.setId(userId); Book book = new
	 * Book("Test Book", "Test Author", 0, 10.0); book.setBookId(10L); CartItem
	 * cartItem = new CartItem(); cartItem.setBook(book); cartItem.setQuantity(1);
	 * List<CartItem> cartItems = Collections.singletonList(cartItem);
	 * 
	 * when(userRepository.findById(userId)).thenReturn(Optional.of(user));
	 * when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
	 * 
	 * // When, Then assertThrows(RuntimeException.class, () ->
	 * orderService.placeOrder(userId, cartItems, "1111-2222-3333-4444", "12/25",
	 * "123") ); verify(orderRepository, times(1)).save(any(Order.class)); // Order
	 * is saved initially verify(bookRepository, times(1)).findById(10L);
	 * verify(orderItemRepository, never()).save(any()); verify(shoppingCartService,
	 * never()).clearCartAfterPayment(anyLong(), anyLong()); }
	 */
    
	/*
	 * @Test void processOrder_insufficientStock_throwsRuntimeException1() { //
	 * Given Long userId = 1L; User user = new User(); user.setId(userId); Book book
	 * = new Book("Test Book", "Test Author", 0, 10.0); book.setBookId(10L);
	 * CartItem cartItem = new CartItem(); cartItem.setBook(book);
	 * cartItem.setQuantity(1); List<CartItem> cartItems =
	 * Collections.singletonList(cartItem);
	 * 
	 * when(cartItemRepository.findByUser_id(userId)).thenReturn(cartItems);
	 * when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
	 * 
	 * // When, Then assertThrows(RuntimeException.class, () ->
	 * orderService.processOrder(user)); verify(orderRepository,
	 * times(1)).save(any(Order.class)); verify(bookRepository,
	 * times(1)).findById(10L); // Now this should be invoked
	 * verify(orderItemRepository, never()).save(any(OrderItem.class));
	 * 
	 * }
	 */

   /* @Test
    void placeOrder_successfulOrderPlacement() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Book book = new Book("Test Book", "Test Author", 5, 10.0);
        book.setBookId(10L);
        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(2);
        List<CartItem> cartItems = Collections.singletonList(cartItem);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        Order savedOrder = new Order();
        savedOrder.setId(100L);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(new OrderItem());

        // When
        Order placedOrder = orderService.placeOrder(userId, cartItems, "1111-2222-3333-4444", "12/25", "123");

        // Then
        assertNotNull(placedOrder);
        assertEquals(100L, placedOrder.getId());
        assertEquals(20.0, placedOrder.getTotalPrice(), 0.001);
        assertEquals("PLACED", placedOrder.getStatus());
        assertEquals(userId, placedOrder.getUser().getId());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(bookRepository, times(1)).save(book);
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }*/

    @Test
    void findById_existingOrder_returnsOrder() {
        // Given
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When
        Order foundOrder = orderService.findById(orderId);

        // Then
        assertEquals(orderId, foundOrder.getId());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void findById_nonExistingOrder_throwsRuntimeException() {
        // Given
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When, Then
        assertThrows(RuntimeException.class, () -> orderService.findById(orderId));
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void save_validOrder_returnsSavedOrder() {
        // Given
        Order orderToSave = new Order();
        Order savedOrder = new Order();
        savedOrder.setId(1L);
        when(orderRepository.save(orderToSave)).thenReturn(savedOrder);

        // When
        Order result = orderService.save(orderToSave);

        // Then
        assertEquals(1L, result.getId());
        verify(orderRepository, times(1)).save(orderToSave);
    }

    @Test
    void cancelOrder_validOrder_updatesStatus() {
        // Given
        Order orderToCancel = new Order();
        orderToCancel.setStatus("PLACED");

        // When
        orderService.cancelOrder(orderToCancel);

        // Then
        assertEquals("CANCELLED", orderToCancel.getStatus());
        verify(orderRepository, times(1)).save(orderToCancel);
    }

   /* @Test
    void getOrders_existingUserId_returnsListOfOrders() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Order order1 = new Order();
        order1.setUser(user);
        Order order2 = new Order();
        order2.setUser(user);
        List<Order> orders = Arrays.asList(order1, order2);
        when(orderRepository.findByUser_id(userId)).thenReturn(orders);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        List<Order> result = orderService.getOrders(userId);

        // Then
        assertEquals(2, result.size());
        assertEquals(userId, result.get(0).getUser().getId());
        assertEquals(userId, result.get(1).getUser().getId());
        verify(orderRepository, times(1)).findByUser_id(userId);
    }*/

  /*  @Test
    void getOrders_nonExistingUserId_returnsEmptyList() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        List<Order> result = orderService.getOrders(userId);

        // Then
        assertTrue(result.isEmpty());
        verify(orderRepository, times(1)).findByUser_id(userId);
    }*/

    @Test
    void processOrder_emptyCart_throwsEmptyCartException() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(cartItemRepository.findByUser_id(userId)).thenReturn(Collections.emptyList());

        // When, Then
        assertThrows(EmptyCartException.class, () -> orderService.processOrder(user));
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).save(any());
        verify(shoppingCartService, never()).clearCartAfterPayment(anyLong(), anyLong());
    }

   /* @Test
    void processOrder_successfulOrderProcessing() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Book book = new Book("Test Book", "Test Author", 5, 10.0);
        book.setBookId(10L);
        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(2);
        List<CartItem> cartItems = Collections.singletonList(cartItem);

        when(cartItemRepository.findByUser_id(userId)).thenReturn(cartItems);
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        Order savedOrder = new Order();
        savedOrder.setId(100L);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(new OrderItem());

        // When
        Order processedOrder = orderService.processOrder(user);

        // Then
        assertNotNull(processedOrder);
        assertEquals(100L, processedOrder.getId());
        assertEquals(20.0, processedOrder.getTotalPrice(), 0.001);
        assertEquals("PLACED", processedOrder.getStatus());
        assertEquals(userId, processedOrder.getUser().getId());
        verify(orderRepository, times(2)).save(any(Order.class)); // Once for initial save, once after setting items
        verify(bookRepository, times(1)).save(book);
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        verify(shoppingCartService, times(1)).clearCartAfterPayment(userId, 100L);
    }*/

	/*
	 * @Test void processOrder_insufficientStock_throwsRuntimeException() { // Given
	 * Long userId = 1L; User user = new User(); user.setId(userId); Book book = new
	 * Book("Test Book", "Test Author", 0, 10.0); book.setBookId(10L); CartItem
	 * cartItem = new CartItem(); cartItem.setBook(book); cartItem.setQuantity(1);
	 * List<CartItem> cartItems = Collections.singletonList(cartItem);
	 * 
	 * when(cartItemRepository.findByUser_id(userId)).thenReturn(cartItems);
	 * when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
	 * 
	 * // When, Then assertThrows(RuntimeException.class, () ->
	 * orderService.processOrder(user)); verify(orderRepository,
	 * times(1)).save(any(Order.class)); verify(bookRepository,
	 * times(1)).findById(10L); // Now this should be invoked
	 * verify(orderItemRepository, never()).save(any(OrderItem.class));
	 * 
	 * }
	 */
}