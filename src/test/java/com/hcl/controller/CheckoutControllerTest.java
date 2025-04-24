package com.hcl.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import com.hcl.config.CustomUserDetails;
import com.hcl.entity.CartItem;
import com.hcl.entity.User;
import com.hcl.service.ShoppingCartService;

public class CheckoutControllerTest {

    @InjectMocks
    private CheckoutController checkoutController;

    @Mock
    private ShoppingCartService shoppingCartService;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void checkout_authenticatedUser_shouldReturnCheckoutView() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem());
        when(shoppingCartService.getCartItems(userId)).thenReturn(cartItems);
        when(shoppingCartService.getTotalPrice(userId)).thenReturn(100.0);

        // Act
        String viewName = checkoutController.checkout(model);

        // Assert
        assertEquals("checkout", viewName);
        verify(model).addAttribute("cartItems", cartItems);
        verify(model).addAttribute("totalPrice", 100.0);
    }

    @Test
    void checkout_unauthenticatedUser_shouldRedirectToLogin() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act
        String viewName = checkoutController.checkout(model);

        // Assert
        assertEquals("redirect:/login", viewName);
    }

    @Test
    void getCurrentUserId_authenticatedWithCustomUserDetails_shouldReturnUserId() throws Exception {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        java.lang.reflect.Method getCurrentUserIdMethod = CheckoutController.class.getDeclaredMethod("getCurrentUserId");
        getCurrentUserIdMethod.setAccessible(true);
        Long currentUserId = (Long) getCurrentUserIdMethod.invoke(checkoutController);

        // Assert
        assertEquals(userId, currentUserId);
    }

    @Test
    void getCurrentUserId_authenticatedWithDifferentPrincipal_shouldReturnNull() throws Exception {
        // Arrange
        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User("testuser", "password", new ArrayList<>());
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        java.lang.reflect.Method getCurrentUserIdMethod = CheckoutController.class.getDeclaredMethod("getCurrentUserId");
        getCurrentUserIdMethod.setAccessible(true);
        Long currentUserId = (Long) getCurrentUserIdMethod.invoke(checkoutController);

        // Assert
        assertNull(currentUserId);
    }

    @Test
    void getCurrentUserId_notAuthenticated_shouldReturnNull() throws Exception {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(false);

        java.lang.reflect.Method getCurrentUserIdMethod = CheckoutController.class.getDeclaredMethod("getCurrentUserId");
        getCurrentUserIdMethod.setAccessible(true);
        Long currentUserId = (Long) getCurrentUserIdMethod.invoke(checkoutController);

        // Assert
        assertNull(currentUserId);
    }

    @Test
    void getCurrentUserId_authenticationIsNull_shouldReturnNull() throws Exception {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(null);

        java.lang.reflect.Method getCurrentUserIdMethod = CheckoutController.class.getDeclaredMethod("getCurrentUserId");
        getCurrentUserIdMethod.setAccessible(true);
        Long currentUserId = (Long) getCurrentUserIdMethod.invoke(checkoutController);

        // Assert
        assertNull(currentUserId);
    }
}