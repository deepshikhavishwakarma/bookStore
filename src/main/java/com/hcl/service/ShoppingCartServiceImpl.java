package com.hcl.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartServiceImpl.class);

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<CartItem> getCartItems(Long userId) {
        logger.info("Getting cart items for user ID: {}", userId);
        return cartItemRepository.findByUser_id(userId);
    }

    @Override
    @Transactional
    public void addItem(Long userId, Long bookId, int quantity) {
        logger.info("Adding book ID {} (quantity {}) to cart for user ID {}", bookId, quantity, userId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    logger.warn("Book not found with ID: {}", bookId);
                    return new BookNotFoundException("Book not found with ID: " + bookId);
                });

        if (book.getQuantity() < quantity) {
            logger.warn("Insufficient stock for book '{}' (ID {}). Requested: {}, Available: {}", book.getBookName(), bookId, quantity, book.getQuantity());
            throw new InsufficientStockException("Insufficient stock for book: " + book.getBookName() + ". Available: " + book.getQuantity());
        }

        book.setQuantity(book.getQuantity() - quantity);
        bookRepository.save(book);
        logger.info("Decreased stock for book ID {}. Remaining quantity: {}", bookId, book.getQuantity());

        Optional<CartItem> existingItem = cartItemRepository.findByUser_idAndBook_BookId(userId, bookId);

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
            logger.info("Increased quantity for existing cart item ID {} to {}", cartItem.getId(), cartItem.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setUser(userRepository.findById(userId)
                    .orElseThrow(() -> {
                        logger.warn("User not found with ID: {}", userId);
                        return new UserNotFoundException("User not found with ID: " + userId);
                    }));
            newItem.setBook(book);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
            logger.info("Added new cart item for book ID {} and user ID {}", bookId, userId);
        }
    }


    @Override
    @Transactional
    public void updateItemQuantity(Long userId, Long itemId, int quantity) {
        logger.info("Updating quantity for cart item ID {} to {} for user ID {}", itemId, quantity, userId);
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> {
                    logger.warn("Cart item not found with ID: {}", itemId);
                    return new CartItemNotFoundException("Cart item not found with ID: " + itemId);
                });

        if (!cartItem.getUser().getId().equals(userId)) {
            logger.warn("Unauthorized access to cart item ID {} by user ID {}", itemId, userId);
            throw new SecurityException("Unauthorized access to cart item.");
        }

        Book book = bookRepository.findById(cartItem.getBook().getBookId())
                .orElseThrow(() -> {
                    logger.warn("Book not found with ID: {}", cartItem.getBook().getBookId());
                    return new BookNotFoundException("Book not found with ID: " + cartItem.getBook().getBookId());
                });

        int oldQuantity = cartItem.getQuantity();
        int quantityDifference = quantity - oldQuantity;

        if (book.getQuantity() < quantityDifference) {
            logger.warn("Insufficient stock for book '{}' (ID {}). Requested difference: {}, Available: {}", book.getBookName(), book.getBookId(), quantityDifference, book.getQuantity());
            throw new InsufficientStockException("Insufficient stock for book: " + book.getBookName() + ". Available: " + book.getQuantity());
        }

        book.setQuantity(book.getQuantity() - quantityDifference);
        bookRepository.save(book);
        logger.info("Updated stock for book ID {}. Remaining quantity: {}", book.getBookId(), book.getQuantity());

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        logger.info("Updated quantity for cart item ID {} to {}", itemId, quantity);
    }

    @Override
    @Transactional
    public void removeItem(Long userId, Long itemId) {
        logger.info("Removing cart item ID {} for user ID {}", itemId, userId);
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> {
                    logger.warn("Cart item not found with ID: {}", itemId);
                    return new CartItemNotFoundException("Cart item not found with ID: " + itemId);
                });

        if (!cartItem.getUser().getId().equals(userId)) {
            logger.warn("Unauthorized access to cart item ID {} by user ID {}", itemId, userId);
            throw new SecurityException("Unauthorized access to cart item.");
        }

        Book book = cartItem.getBook();
        book.setQuantity(book.getQuantity() + cartItem.getQuantity());
        bookRepository.save(book);
        logger.info("Increased stock for book ID {} by {}", book.getBookId(), cartItem.getQuantity());

        cartItemRepository.deleteById(itemId);
        logger.info("Removed cart item with ID {}", itemId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        logger.info("Clearing cart for user ID: {}", userId);
        List<CartItem> cartItems = cartItemRepository.findByUser_id(userId);

        for (CartItem item : cartItems) {
            Book book = item.getBook();
            book.setQuantity(book.getQuantity() + item.getQuantity());
            bookRepository.save(book);
            logger.info("Increased stock for book ID {} by {} while clearing cart for user ID {}", book.getBookId(), item.getQuantity(), userId);
        }

        cartItemRepository.deleteByUser_id(userId);
        logger.info("Cart cleared for user ID: {}", userId);
    }

    @Override
    @Transactional
    public void clearCartAfterPayment(Long userId, Long orderId) {
        logger.info("Clearing cart for user ID {} after payment for order ID {}", userId, orderId);
        List<CartItem> cartItems = cartItemRepository.findByUser_id(userId);
        cartItemRepository.deleteAll(cartItems);
        logger.info("Cart cleared for user ID {} after payment for order ID {}", userId, orderId);
    }

    @Override
    public double getTotalPrice(Long userId) {
        logger.info("Calculating total price for cart of user ID: {}", userId);
        double totalPrice = cartItemRepository.findByUser_id(userId)
                .stream()
                .mapToDouble(item -> item.getBook().getPrice() * item.getQuantity())
                .sum();
        logger.info("Total price for cart of user ID {}: {}", userId, totalPrice);
        return totalPrice;
    }


}





