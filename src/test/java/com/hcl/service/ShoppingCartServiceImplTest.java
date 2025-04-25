package com.hcl.service;

import com.hcl.entity.Book;
import com.hcl.entity.CartItem;
import com.hcl.entity.User;
import com.hcl.exception.BookNotFoundException;
import com.hcl.exception.CartItemNotFoundException;
import com.hcl.exception.InsufficientStockException;

import com.hcl.repository.BookRepository;
import com.hcl.repository.CartItemRepository;
import com.hcl.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceImplTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    void getCartItems_existingUserId_returnsListOfCartItems() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Book book1 = new Book();
        book1.setBookId(10L);
        Book book2 = new Book();
        book2.setBookId(20L);

        CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);
        cartItem1.setQuantity(2);
        cartItem1.setBook(book1);
        cartItem1.setUser(user);

        CartItem cartItem2 = new CartItem();
        cartItem2.setId(2L);
        cartItem2.setQuantity(1);
        cartItem2.setBook(book2);
        cartItem2.setUser(user);

        List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2);
        when(cartItemRepository.findByUser_id(userId)).thenReturn(cartItems);

        // When
        List<CartItem> result = shoppingCartService.getCartItems(userId);

        // Then
        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).getBook().getBookId());
        assertEquals(20L, result.get(1).getBook().getBookId());
        verify(cartItemRepository, times(1)).findByUser_id(userId);
    }

    @Test
    void getCartItems_nonExistingUserId_returnsEmptyList() {
        // Given
        Long userId = 1L;
        when(cartItemRepository.findByUser_id(userId)).thenReturn(Collections.emptyList());

        // When
        List<CartItem> result = shoppingCartService.getCartItems(userId);

        // Then
        assertTrue(result.isEmpty());
        verify(cartItemRepository, times(1)).findByUser_id(userId);
    }

    @Test
    void addItem_existingUserAndBook_createsNewCartItem() {
        // Given
        Long userId = 1L;
        Long bookId = 10L;
        int quantity = 3;
        User user = new User();
        user.setId(userId);
        Book book = new Book("Test Book", "Author", 5, 10.0);
        book.setBookId(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartItemRepository.findByUser_idAndBook_BookId(userId, bookId)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(new CartItem());

        // When
        shoppingCartService.addItem(userId, bookId, quantity);

        // Then
        assertEquals(2, book.getQuantity()); // Stock reduced
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verify(bookRepository, times(1)).save(book);
    }

   /* @Test
    void addItem_existingUserAndBook_updatesExistingCartItem() {
        // Given
        Long userId = 1L;
        Long bookId = 10L;
        int quantityToAdd = 2;
        User user = new User();
        user.setId(userId);
        Book book = new Book("Test Book", "Author", 5, 10.0);
        book.setBookId(bookId);
        CartItem existingItem = new CartItem();
        existingItem.setId(1L);
        existingItem.setQuantity(1);
        existingItem.setBook(book);
        existingItem.setUser(user);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartItemRepository.findByUser_idAndBook_BookId(userId, bookId)).thenReturn(Optional.of(existingItem));
        when(cartItemRepository.save(existingItem)).thenReturn(existingItem);

        // When
        shoppingCartService.addItem(userId, bookId, quantityToAdd);

        // Then
        assertEquals(3, existingItem.getQuantity());
        assertEquals(3, book.getQuantity()); // Stock reduced
        verify(cartItemRepository, times(1)).save(existingItem);
        verify(bookRepository, times(1)).save(book);
    }*/

    @Test
    void addItem_nonExistingBook_throwsBookNotFoundException() {
        // Given
        Long userId = 1L;
        Long bookId = 10L;
        int quantity = 2;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When, Then
        assertThrows(BookNotFoundException.class, () -> shoppingCartService.addItem(userId, bookId, quantity));
        verify(cartItemRepository, never()).save(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void addItem_insufficientStock_throwsInsufficientStockException() {
        // Given
        Long userId = 1L;
        Long bookId = 10L;
        int quantityToAdd = 6;
        Book book = new Book("Test Book", "Author", 5, 10.0);
        book.setBookId(bookId);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When, Then
        assertThrows(InsufficientStockException.class, () -> shoppingCartService.addItem(userId, bookId, quantityToAdd));
        verify(cartItemRepository, never()).save(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void updateItemQuantity_existingItemAndCorrectUser_updatesQuantityAndSaves() {
        // Given
        Long userId = 1L;
        Long itemId = 2L;
        int newQuantity = 5;
        User user = new User();
        user.setId(userId);
        Book book = new Book("Test Book", "Author", 10, 10.0);
        book.setBookId(10L);
        CartItem existingItem = new CartItem();
        existingItem.setId(itemId);
        existingItem.setQuantity(2);
        existingItem.setBook(book);
        existingItem.setUser(user);

        when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(cartItemRepository.save(existingItem)).thenReturn(existingItem);

        // When
        shoppingCartService.updateItemQuantity(userId, itemId, newQuantity);

        // Then
        assertEquals(newQuantity, existingItem.getQuantity());
        assertEquals(7, book.getQuantity()); // Stock updated
        verify(cartItemRepository, times(1)).save(existingItem);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void updateItemQuantity_nonExistingItem_throwsCartItemNotFoundException() {
        // Given
        Long userId = 1L;
        Long itemId = 2L;
        int newQuantity = 5;
        when(cartItemRepository.findById(itemId)).thenReturn(Optional.empty());

        // When, Then
        assertThrows(CartItemNotFoundException.class, () -> shoppingCartService.updateItemQuantity(userId, itemId, newQuantity));
        verify(cartItemRepository, never()).save(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void updateItemQuantity_wrongUserForItem_throwsSecurityException() {
        // Given
        Long userId = 1L;
        Long itemId = 2L;
        int newQuantity = 5;
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        Book book = new Book();
        book.setBookId(10L);
        CartItem existingItem = new CartItem();
        existingItem.setId(itemId);
        existingItem.setQuantity(2);
        existingItem.setBook(book);
        existingItem.setUser(user2);

        when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        // When, Then
        assertThrows(SecurityException.class, () -> shoppingCartService.updateItemQuantity(userId, itemId, newQuantity));
        verify(cartItemRepository, never()).save(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void updateItemQuantity_insufficientStock_throwsInsufficientStockException() {
        // Given
        Long userId = 1L;
        Long itemId = 2L;
        int newQuantity = 15;
        User user = new User();
        user.setId(userId);
        Book book = new Book("Test Book", "Author", 10, 10.0);
        book.setBookId(10L);
        CartItem existingItem = new CartItem();
        existingItem.setId(itemId);
        existingItem.setQuantity(2);
        existingItem.setBook(book);
        existingItem.setUser(user);

        when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        // When, Then
        assertThrows(InsufficientStockException.class, () -> shoppingCartService.updateItemQuantity(userId, itemId, newQuantity));
        verify(cartItemRepository, never()).save(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void removeItem_existingItemAndCorrectUser_deletesItemAndUpdatesStock() {
        // Given
        Long userId = 1L;
        Long itemId = 2L;
        User user = new User();
        user.setId(userId);
        Book book = new Book("Test Book", "Author", 5, 10.0);
        book.setBookId(10L);
        CartItem existingItem = new CartItem();
        existingItem.setId(itemId);
        existingItem.setQuantity(2);
        existingItem.setBook(book);
        existingItem.setUser(user);

        when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        // When
        shoppingCartService.removeItem(userId, itemId);

        // Then
        assertEquals(7, book.getQuantity()); // Stock increased
        verify(cartItemRepository, times(1)).deleteById(itemId);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void removeItem_nonExistingItem_throwsCartItemNotFoundException() {
        // Given
        Long userId = 1L;
        Long itemId = 2L;
        when(cartItemRepository.findById(itemId)).thenReturn(Optional.empty());

        // When, Then
        assertThrows(CartItemNotFoundException.class, () -> shoppingCartService.removeItem(userId, itemId));
        verify(cartItemRepository, never()).deleteById(anyLong());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void removeItem_wrongUserForItem_throwsSecurityException() {
        // Given
        Long userId = 1L;
        Long itemId = 2L;
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        Book book = new Book();
        book.setBookId(10L);
        CartItem existingItem = new CartItem();
        existingItem.setId(itemId);
        existingItem.setQuantity(2);
        existingItem.setBook(book);
        existingItem.setUser(user2);

        when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        // When, Then
        assertThrows(SecurityException.class, () -> shoppingCartService.removeItem(userId, itemId));
        verify(cartItemRepository, never()).deleteById(anyLong());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void clearCart_existingUserId_deletesAllItemsForUserAndUpdateStock() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Book book1 = new Book("Book 1", "Author", 5, 10.0);
        book1.setBookId(10L);
        Book book2 = new Book("Book 2", "Author", 3, 20.0);
        book2.setBookId(20L);

        CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);
        cartItem1.setQuantity(2);
        cartItem1.setBook(book1);
        cartItem1.setUser(user);

        CartItem cartItem2 = new CartItem();
        cartItem2.setId(2L);
        cartItem2.setQuantity(1);
        cartItem2.setBook(book2);
        cartItem2.setUser(user);

        List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2);
        when(cartItemRepository.findByUser_id(userId)).thenReturn(cartItems);
        when(bookRepository.save(book1)).thenReturn(book1);
        when(bookRepository.save(book2)).thenReturn(book2);

        // When
        shoppingCartService.clearCart(userId);

        // Then
        assertEquals(7, book1.getQuantity()); // Stock restored
        assertEquals(4, book2.getQuantity()); // Stock restored
        verify(cartItemRepository, times(1)).deleteByUser_id(userId);
        verify(bookRepository, times(2)).save(any(Book.class));
    }

    @Test
    void clearCart_nonExistingUserId_doesNothing() {
        // Given
        Long userId = 1L;
        when(cartItemRepository.findByUser_id(userId)).thenReturn(Collections.emptyList());

        // When
        shoppingCartService.clearCart(userId);

        // Then
        verify(cartItemRepository, times(1)).deleteByUser_id(userId);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void clearCartAfterPayment_existingUserId_deletesAllItemsForUser() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Book book1 = new Book();
        book1.setBookId(10L);
        Book book2 = new Book();
        book2.setBookId(20L);

        CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);
        cartItem1.setQuantity(2);
        cartItem1.setBook(book1);
        cartItem1.setUser(user);

        CartItem cartItem2 = new CartItem();
        cartItem2.setId(2L);
        cartItem2.setQuantity(1);
        cartItem2.setBook(book2);
        cartItem2.setUser(user);

        List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2);
        when(cartItemRepository.findByUser_id(userId)).thenReturn(cartItems);

        // When
        shoppingCartService.clearCartAfterPayment(userId, 123L);

        // Then
        verify(cartItemRepository, times(1)).deleteAll(cartItems);
    }

   // @Test
    //void clearCartAfterPayment_nonExistingUserId_doesNothing() {
        // Given
      //  Long userId = 1L;
        //when(cartItemRepository.findByUser_id(userId)).thenReturn(Collections.emptyList());

    }
