package com.hcl.controller;

import com.hcl.config.CustomUserDetails;
import com.hcl.entity.Book;
import com.hcl.entity.CartItem;
import com.hcl.exception.BookNotFoundException;
import com.hcl.exception.InsufficientStockException;
import com.hcl.service.BookService;
import com.hcl.service.ShoppingCartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ShoppingCartControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ShoppingCartService shoppingCartService;

    @Mock
    private BookService bookService;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails customUserDetails;

    @InjectMocks
    private ShoppingCartController shoppingCartController;

    private final Long testUserId = 1L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(shoppingCartController)
                .setViewResolvers(new InternalResourceViewResolver("", ""))
                .build();

        // Mock the getCurrentUserId method
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getId()).thenReturn(testUserId);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

   /* @Test
    public void testViewCart_AuthenticatedUser_CartNotEmpty() throws Exception {
        // Arrange
        List<CartItem> cartItems = Arrays.asList(new CartItem(), new CartItem());
        double totalPrice = 100.0;
        when(shoppingCartService.getCartItems(testUserId)).thenReturn(cartItems);
        when(shoppingCartService.getTotalPrice(testUserId)).thenReturn(totalPrice);

        // Act and Assert
        mockMvc.perform(get("/cart"))
               .andExpect(status().isOk())
               .andExpect(view().name("cart"))
               .andExpect(model().attribute("cartItems", cartItems))
               .andExpect(model().attribute("totalPrice", totalPrice));

        verify(shoppingCartService, times(1)).getCartItems(testUserId);
        verify(shoppingCartService, times(1)).getTotalPrice(testUserId);
    }*/

    /*@Test
    public void testViewCart_AuthenticatedUser_CartEmpty() throws Exception {
        // Arrange
        when(shoppingCartService.getCartItems(testUserId)).thenReturn(Collections.emptyList());
        when(shoppingCartService.getTotalPrice(testUserId)).thenReturn(0.0);

        // Act and Assert
        mockMvc.perform(get("/cart"))
               .andExpect(status().isOk())
               .andExpect(view().name("cart"))
               .andExpect(model().attribute("cartItems", Collections.emptyList()))
               .andExpect(model().attribute("totalPrice", 0.0));

        verify(shoppingCartService, times(1)).getCartItems(testUserId);
        verify(shoppingCartService, times(1)).getTotalPrice(testUserId);
    }*/

    @Test
    public void testAddItem_AuthenticatedUser_Success() throws Exception {
        Long bookId = 2L;
        int quantity = 1;
        Book mockBook = new Book();
        mockBook.setQuantity(5);
        when(bookService.getBookById(bookId)).thenReturn(mockBook);
        doNothing().when(shoppingCartService).addItem(testUserId, bookId, quantity);

        mockMvc.perform(post("/cart/add")
                       .param("bookId", String.valueOf(bookId))
                       .param("quantity", String.valueOf(quantity)))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/books"))
               .andExpect(flash().attribute("message", "Book added to cart successfully!"));

        verify(shoppingCartService, times(1)).addItem(testUserId, bookId, quantity);
        verify(bookService, times(1)).getBookById(bookId);
    }

   /* @Test
    public void testAddItem_AuthenticatedUser_InsufficientStock() throws Exception {
        Long bookId = 2L;
        int quantity = 10;
        Book mockBook = new Book();
        mockBook.setQuantity(5);
        when(bookService.getBookById(bookId)).thenReturn(mockBook);

        mockMvc.perform(post("/cart/add")
                       .param("bookId", String.valueOf(bookId))
                       .param("quantity", String.valueOf(quantity)))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/books"))
               .andExpect(flash().attribute("errorMessage", "Insufficient stock for this book. Available quantity: 5"));

        verify(shoppingCartService, never()).addItem(anyLong(), anyLong(), anyInt());
        verify(bookService, times(1)).getBookById(bookId);
    }*/

    @Test
    public void testAddItem_AuthenticatedUser_BookNotFound() throws Exception {
        Long bookId = 2L;
        int quantity = 1;
        when(bookService.getBookById(bookId)).thenThrow(new BookNotFoundException("Book not found"));

        mockMvc.perform(post("/cart/add")
                       .param("bookId", String.valueOf(bookId))
                       .param("quantity", String.valueOf(quantity)))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/books"))
               .andExpect(flash().attribute("errorMessage", "Book not found"));

        verify(shoppingCartService, never()).addItem(anyLong(), anyLong(), anyInt());
        verify(bookService, times(1)).getBookById(bookId);
    }

    @Test
    public void testUpdateItem_AuthenticatedUser() throws Exception {
        Long itemId = 3L;
        int quantity = 2;
        doNothing().when(shoppingCartService).updateItemQuantity(testUserId, itemId, quantity);

        mockMvc.perform(post("/cart/update")
                       .param("itemId", String.valueOf(itemId))
                       .param("quantity", String.valueOf(quantity)))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/cart"));

        verify(shoppingCartService, times(1)).updateItemQuantity(testUserId, itemId, quantity);
    }

    @Test
    public void testRemoveItem_AuthenticatedUser() throws Exception {
        Long itemId = 3L;
        doNothing().when(shoppingCartService).removeItem(testUserId, itemId);

        mockMvc.perform(post("/cart/remove")
                       .param("itemId", String.valueOf(itemId)))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/cart"));

        verify(shoppingCartService, times(1)).removeItem(testUserId, itemId);
    }

    @Test
    public void testClearCart_AuthenticatedUser() throws Exception {
        doNothing().when(shoppingCartService).clearCart(testUserId);

        mockMvc.perform(get("/cart/clear"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/cart"));

        verify(shoppingCartService, times(1)).clearCart(testUserId);
    }

    /*@Test
    public void testShowCheckout_AuthenticatedUser_CartNotEmpty() throws Exception {
        List<CartItem> cartItems = Arrays.asList(new CartItem(), new CartItem());
        double totalPrice = 150.0;
        when(shoppingCartService.getCartItems(testUserId)).thenReturn(cartItems);
        when(shoppingCartService.getTotalPrice(testUserId)).thenReturn(totalPrice);

        mockMvc.perform(get("/cart/checkout"))
               .andExpect(status().isOk())
               .andExpect(view().name("checkout"))
               .andExpect(model().attribute("cartItems", cartItems))
               .andExpect(model().attribute("totalPrice", totalPrice));

        verify(shoppingCartService, times(1)).getCartItems(testUserId);
        verify(shoppingCartService, times(1)).getTotalPrice(testUserId);
    }*/

   /* @Test
    public void testShowCheckout_AuthenticatedUser_CartEmpty() throws Exception {
        when(shoppingCartService.getCartItems(testUserId)).thenReturn(Collections.emptyList());
        when(shoppingCartService.getTotalPrice(testUserId)).thenReturn(0.0);

        mockMvc.perform(get("/cart/checkout"))
               .andExpect(status().isOk())
               .andExpect(view().name("checkout"))
               .andExpect(model().attribute("cartItems", Collections.emptyList()))
               .andExpect(model().attribute("totalPrice", 0.0));

        verify(shoppingCartService, times(1)).getCartItems(testUserId);
        verify(shoppingCartService, times(1)).getTotalPrice(testUserId);
    }*/

   /* @Test
    public void testShowPaymentPage_AuthenticatedUser() throws Exception {
        double totalPrice = 200.0;
        when(shoppingCartService.getTotalPrice(testUserId)).thenReturn(totalPrice);

        mockMvc.perform(get("/cart/payment"))
               .andExpect(status().isOk())
               .andExpect(view().name("payment"))
               .andExpect(model().attribute("totalPrice", totalPrice))
               .andExpect(model().attribute("paymentDate", LocalDate.now()));

        verify(shoppingCartService, times(1)).getTotalPrice(testUserId);
    }*/

    @Test
    public void testClearCartFrontend_AuthenticatedUser() throws Exception {
        doNothing().when(shoppingCartService).clearCart(testUserId);

        mockMvc.perform(post("/cart/clear-frontend"))
                // .contentType(MediaType.APPLICATION_JSON)
             //  .accept(MediaType.APPLICATION_JSON)
               .andExpect(status().isOk())
               .andExpect(content().string("Cart cleared successfully"));

        verify(shoppingCartService, times(1)).clearCart(testUserId);
    }
}