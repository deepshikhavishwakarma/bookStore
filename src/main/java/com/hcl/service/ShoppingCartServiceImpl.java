package com.hcl.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcl.entity.Book;
import com.hcl.entity.CartItem;
import com.hcl.exception.BookNotFoundException;
import com.hcl.exception.CartItemNotFoundException;
import com.hcl.exception.InsufficientStockException;
import com.hcl.exception.UserNotFoundException;
import com.hcl.repository.BookRepository;
import com.hcl.repository.CartItemRepository;
import com.hcl.repository.UserRepository;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<CartItem> getCartItems(Long userId) {
        return cartItemRepository.findByUser_id(userId);
    }

    @Override
    @Transactional
    public void addItem(Long userId, Long bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));

        if (book.getQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for book: " + book.getBookName() + ". Available: " + book.getQuantity());
        }

        book.setQuantity(book.getQuantity() - quantity);
        bookRepository.save(book);

        Optional<CartItem> existingItem = cartItemRepository.findByUser_idAndBook_BookId(userId, bookId);

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUser(userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId)));
            newItem.setBook(book);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }
    }


    @Override
    @Transactional
    public void updateItemQuantity(Long userId, Long itemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found with ID: " + itemId));

        if (!cartItem.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized access to cart item.");
        }

        Book book = bookRepository.findById(cartItem.getBook().getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + cartItem.getBook().getBookId()));

        int oldQuantity = cartItem.getQuantity();
        int quantityDifference = quantity - oldQuantity;

        if (book.getQuantity() < quantityDifference) {
            throw new InsufficientStockException("Insufficient stock for book: " + book.getBookName() + ". Available: " + book.getQuantity());
        }

        book.setQuantity(book.getQuantity() - quantityDifference);
        bookRepository.save(book);

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void removeItem(Long userId, Long itemId) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found with ID: " + itemId));

        if (!cartItem.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized access to cart item.");
        }

        Book book = cartItem.getBook();
        book.setQuantity(book.getQuantity() + cartItem.getQuantity());
        bookRepository.save(book);

        cartItemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUser_id(userId);

        for (CartItem item : cartItems) {
            Book book = item.getBook();
            book.setQuantity(book.getQuantity() + item.getQuantity());
            bookRepository.save(book);
        }

        cartItemRepository.deleteByUser_id(userId);
    }

    @Override
    @Transactional
    public void clearCartAfterPayment(Long userId, Long orderId) {
        List<CartItem> cartItems = cartItemRepository.findByUser_id(userId);
        cartItemRepository.deleteAll(cartItems);
    }

    @Override
    public double getTotalPrice(Long userId) {
        return cartItemRepository.findByUser_id(userId)
                .stream()
                .mapToDouble(item -> item.getBook().getPrice() * item.getQuantity())
                .sum();
    }

    
}
