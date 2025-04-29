package com.hcl.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hcl.config.CustomUserDetails;
import com.hcl.entity.CartItem;
import com.hcl.entity.Order;
import com.hcl.entity.User;
import com.hcl.service.OrderService;
import com.hcl.service.ShoppingCartService;

import jakarta.servlet.http.HttpSession;

class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    @Mock
    private ShoppingCartService shoppingCartService;

    @Mock
    private HttpSession session;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    private User authenticatedUser;
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        authenticatedUser = new User();
        authenticatedUser.setId(1L);
        customUserDetails = new CustomUserDetails(authenticatedUser);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
    }

    private void setupUnauthenticatedUser() {
        when(authentication.getPrincipal()).thenReturn(null);
        when(authentication.isAuthenticated()).thenReturn(false);
    }

    @Test
    void placeOrder_authenticatedUser_cartNotEmpty_orderPlacedSuccessfully() throws Exception {
        // Arrange
        List<CartItem> cartItems = Collections.singletonList(new CartItem());
        Order placedOrder = new Order();
        placedOrder.setId(100L);

        when(orderService.placeOrder(1L, cartItems, "1234567890", "12/25", "123")).thenReturn(placedOrder);
        when(shoppingCartService.getCartItems(1L)).thenReturn(cartItems);
        doNothing().when(shoppingCartService).clearCart(1L);

        // Act
        String view = orderController.placeOrder("1234567890", "12/25", "123", session, redirectAttributes);

        // Assert
        assertEquals("redirect:/order/confirmation/100", view);
        verify(orderService, times(1)).placeOrder(1L, cartItems, "1234567890", "12/25", "123");
        verify(shoppingCartService, times(1)).getCartItems(1L);
        verify(shoppingCartService, times(1)).clearCart(1L);
        verify(redirectAttributes, never()).addFlashAttribute(any(), any());
    }

    @Test
    void placeOrder_unauthenticatedUser_shouldRedirectToLogin() {
        // Arrange
        setupUnauthenticatedUser();

        // Act
        String view = orderController.placeOrder("1234567890", "12/25", "123", session, redirectAttributes);

        // Assert
        assertEquals("redirect:/login", view);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "User is not authenticated.");
        verify(orderService, never()).placeOrder(any(), any(), any(), any(), any());
        verify(shoppingCartService, never()).getCartItems(any());
        verify(shoppingCartService, never()).clearCart(any());
    }

    @Test
    void placeOrder_authenticatedUser_cartEmpty_shouldRedirectToCart() {
        // Arrange
        when(shoppingCartService.getCartItems(1L)).thenReturn(Collections.emptyList());

        // Act
        String view = orderController.placeOrder("1234567890", "12/25", "123", session, redirectAttributes);

        // Assert
        assertEquals("redirect:/cart", view);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Your cart is empty.");
        verify(orderService, never()).placeOrder(any(), any(), any(), any(), any());
        verify(shoppingCartService, times(1)).getCartItems(1L);
        verify(shoppingCartService, never()).clearCart(any());
    }

    @Test
    void placeOrder_authenticatedUser_orderPlacementFails_shouldRedirectToCheckoutWithError() throws Exception {
        // Arrange
        List<CartItem> cartItems = Collections.singletonList(new CartItem());
        when(shoppingCartService.getCartItems(1L)).thenReturn(cartItems);
        doThrow(new RuntimeException("Payment failed")).when(orderService).placeOrder(1L, cartItems, "1234567890", "12/25", "123");

        // Act
        String view = orderController.placeOrder("1234567890", "12/25", "123", session, redirectAttributes);

        // Assert
        assertEquals("redirect:/checkout", view);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Failed to place order: Payment failed");
        verify(orderService, times(1)).placeOrder(1L, cartItems, "1234567890", "12/25", "123");
        verify(shoppingCartService, times(1)).getCartItems(1L);
        verify(shoppingCartService, never()).clearCart(any());
    }

    @Test
    void viewOrderDetails_orderFound_shouldAddToModelAndReturnView() {
        // Arrange
        Long orderId = 200L;
        Order order = new Order();
        when(orderService.findById(orderId)).thenReturn(order);

        // Act
        String view = orderController.viewOrderDetails(orderId, model);

        // Assert
        assertEquals("order-details", view);
        verify(orderService, times(1)).findById(orderId);
        verify(model, times(1)).addAttribute("order", order);
    }

    @Test
    void cancelOrder_authenticatedUser_orderFound_isOwner_isPlaced_within7Days_shouldCancelOrder() throws Exception {
        // Arrange
        Long orderId = 300L;
        Order order = new Order();
        order.setId(orderId);
        order.setUser(authenticatedUser);
        order.setStatus("PLACED");
        order.setOrderDate(LocalDate.now().minusDays(3));

        when(orderService.findById(orderId)).thenReturn(order);
        doNothing().when(orderService).cancelOrder(order);

        // Act
        String view = orderController.cancelOrder(orderId, redirectAttributes);

        // Assert
        assertEquals("redirect:/orders", view);
        verify(orderService, times(1)).findById(orderId);
        verify(orderService, times(1)).cancelOrder(order);
        verify(redirectAttributes, times(1)).addFlashAttribute("message", "Order cancelled and items returned to cart.");
    }

    @Test
    void cancelOrder_unauthenticatedUser_shouldRedirectToLogin() {
        // Arrange
        setupUnauthenticatedUser();

        // Act
        String view = orderController.cancelOrder(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/login", view);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "User is not authenticated.");
        verify(orderService, never()).findById(any());
        verify(orderService, never()).cancelOrder(any());
    }

    @Test
    void cancelOrder_authenticatedUser_orderNotFound_shouldRedirectToOrdersWithError() {
        // Arrange
        when(orderService.findById(1L)).thenReturn(null);

        // Act
        String view = orderController.cancelOrder(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/orders", view);
        verify(orderService, times(1)).findById(1L);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Order not found.");
        verify(orderService, never()).cancelOrder(any());
    }

    @Test
    void cancelOrder_authenticatedUser_notOwner_shouldRedirectToOrdersWithError() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(99L);
        Order order = new Order();
        order.setUser(otherUser);
        when(orderService.findById(1L)).thenReturn(order);

        // Act
        String view = orderController.cancelOrder(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/orders", view);
        verify(orderService, times(1)).findById(1L);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "You are not authorized to cancel this order.");
        verify(orderService, never()).cancelOrder(any());
    }

    @Test
    void cancelOrder_authenticatedUser_orderNotPlaced_shouldRedirectToOrdersWithError() {
        // Arrange
        Order order = new Order();
        order.setUser(authenticatedUser);
        order.setStatus("SHIPPED");
        when(orderService.findById(1L)).thenReturn(order);

        // Act
        String view = orderController.cancelOrder(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/orders", view);
        verify(orderService, times(1)).findById(1L);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Only placed orders can be cancelled.");
        verify(orderService, never()).cancelOrder(any());
    }

    @Test
    void cancelOrder_authenticatedUser_cancellationPeriodExpired_shouldRedirectToOrdersWithError() {
        // Arrange
        Order order = new Order();
        order.setUser(authenticatedUser);
        order.setStatus("PLACED");
        order.setOrderDate(LocalDate.now().minusDays(8));
        when(orderService.findById(1L)).thenReturn(order);

        // Act
        String view = orderController.cancelOrder(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/orders", view);
        verify(orderService, times(1)).findById(1L);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Cancellation period (7 days) has expired.");
        verify(orderService, never()).cancelOrder(any());
    }

    @Test
    void cancelOrder_authenticatedUser_cancellationFails_shouldRedirectToOrdersWithError() throws Exception {
        // Arrange
        Order order = new Order();
        order.setUser(authenticatedUser);
        order.setStatus("PLACED");
        order.setOrderDate(LocalDate.now().minusDays(3));
        when(orderService.findById(1L)).thenReturn(order);
        doThrow(new RuntimeException("Cancellation failed")).when(orderService).cancelOrder(order);

        // Act
        String view = orderController.cancelOrder(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/orders", view);
        verify(orderService, times(1)).findById(1L);
        verify(orderService, times(1)).cancelOrder(order);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Failed to cancel order: Cancellation failed");
    }

    @Test
    void orderConfirmation_orderFound_shouldAddToModelAndReturnView() {
        // Arrange
        Long orderId = 400L;
        Order order = new Order();
        when(orderService.findById(orderId)).thenReturn(order);

        // Act
        String view = orderController.orderConfirmation(orderId, model);

        // Assert
        assertEquals("order-confirmation", view);
        verify(orderService, times(1)).findById(orderId);
        verify(model, times(1)).addAttribute("order", order);
    }

    @Test
    void orderConfirmation_orderNotFound_shouldReturnErrorView() {
        // Arrange
        when(orderService.findById(1L)).thenReturn(null);

        // Act
        String view = orderController.orderConfirmation(1L, model);

        // Assert
        assertEquals("error/order-not-found", view);
        verify(orderService, times(1)).findById(1L);
        verify(model, never()).addAttribute(any(), any());
    }

    @Test
    void getAllOrder_authenticatedUser_shouldGetOrdersAndAddToMap() throws Exception {
        // Arrange
        List<Order> orders = Collections.singletonList(new Order());
        when(orderService.getOrders(1L)).thenReturn(orders);
        Map<String, Object> map = new java.util.HashMap<>();

        // Act
        Method getAllOrderMethod = OrderController.class.getDeclaredMethod("getAllOrder", Map.class);
        getAllOrderMethod.setAccessible(true);
        String view = (String) getAllOrderMethod.invoke(orderController, map);

        // Assert
        assertEquals("order-details", view);
        assertEquals(orders, map.get("orders"));
        verify(orderService, times(1)).getOrders(1L);
    }

    @Test
    void getAllOrder_unauthenticatedUser_shouldReturnNullUserIdAndPotentiallyEmptyList() throws Exception {
        // Arrange
        setupUnauthenticatedUser();
        Map<String, Object> map = new java.util.HashMap<>();
        when(orderService.getOrders(null)).thenReturn(Collections.emptyList()); // Assuming service handles null user

        // Act
        Method getAllOrderMethod = OrderController.class.getDeclaredMethod("getAllOrder", Map.class);
        getAllOrderMethod.setAccessible(true);
        String view = (String) getAllOrderMethod.invoke(orderController, map);

        // Assert
        assertEquals("order-details", view);
        assertEquals(Collections.emptyList(), map.get("orders"));
        verify(orderService, times(1)).getOrders(null);
    }
}