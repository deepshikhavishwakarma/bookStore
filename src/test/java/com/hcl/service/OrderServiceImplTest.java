package com.hcl.service;


import com.hcl.entity.Order;

import com.hcl.entity.User;
import com.hcl.exception.EmptyCartException;

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

import java.util.Collections;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

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

	@Test
	void processOrder_emptyCart_throwsEmptyCartException() {
		// Given
		Long userId = 1L;
		User user = new User();
		user.setId(userId);
		when(cartItemRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

		// When, Then
		assertThrows(EmptyCartException.class, () -> orderService.processOrder(user));
		verify(orderRepository, never()).save(any());
		verify(orderItemRepository, never()).save(any());
		verify(shoppingCartService, never()).clearCartAfterPayment(anyLong(), anyLong());
	}

}