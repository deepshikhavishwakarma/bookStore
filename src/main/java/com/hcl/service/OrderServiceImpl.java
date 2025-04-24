package com.hcl.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Transactional
    @Override
    public Order placeOrder(Long userId, List<CartItem> cartItems, String cardNumber, String expiryDate, String cvv) {
        logger.info("Placing order for user ID: {}", userId);
        if (cartItems.isEmpty()) {
            logger.warn("Cart is empty for user ID: {}", userId);
            throw new EmptyCartException("Your cart is empty. Cannot place order.");
        }

        logger.info("Processing payment for user ID: {} with card details (masked)", userId);

        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getBook().getPrice() * item.getQuantity())
                .sum();
        logger.info("Total price for order of user ID {}: {}", userId, totalPrice);

        Order order = new Order();
        User user = userRepository.findById(userId).orElseThrow(() -> {
            logger.error("User not found with ID: {}", userId);
            return new UserNotFoundException("User Not Found");
        });
        order.setUser(user);
        order.setTotalPrice(totalPrice);
        order.setOrderDate(LocalDate.now());
        order.setPaymentDate(LocalDate.now());
        order.setStatus("PLACED");

        Order savedOrder = orderRepository.save(order);
        logger.info("Order saved with ID: {}", savedOrder.getId());

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Long bookId = cartItem.getBook().getBookId();
            int quantity = cartItem.getQuantity();
            logger.info("Processing cart item: book ID {}, quantity {}", bookId, quantity);
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> {
                        logger.error("Book not found with ID: {}", bookId);
                        return new RuntimeException("Book not found: " + bookId);
                    });

            if (book.getQuantity() >= quantity) {
                book.setQuantity(book.getQuantity() - quantity);
                bookRepository.save(book);
                logger.info("Updated stock for book ID {}: remaining quantity {}", bookId, book.getQuantity());

                OrderItem orderItem = new OrderItem();
                orderItem.setBookName(book.getBookName());
                orderItem.setPrice(cartItem.getBook().getPrice());
                orderItem.setQuantity(quantity);
                orderItem.setBook(book);
                orderItem.setOrder(savedOrder);

                orderItemRepository.save(orderItem);
                orderItems.add(orderItem);
                logger.info("Order item saved: book '{}', quantity {}", book.getBookName(), quantity);
            } else {
                logger.error("Insufficient stock for book '{}'. Requested: {}, Available: {}", book.getBookName(), quantity, book.getQuantity());
                throw new RuntimeException("Insufficient stock for " + book.getBookName());
            }
        }

        savedOrder.setItems(orderItems);
        Order finalSavedOrder = orderRepository.save(savedOrder);
        logger.info("Order processing completed for order ID: {}", finalSavedOrder.getId());
        return finalSavedOrder;
    }

    @Override
    public Order findById(Long orderId) {
        logger.info("Finding order by ID: {}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.warn("Order not found with ID: {}", orderId);
                    return new RuntimeException("Order not found with ID: " + orderId);
                });
    }

    @Override
    public Order save(Order order) {
        logger.info("Saving order: {}", order);
        return orderRepository.save(order);
    }

    @Override
    public void cancelOrder(Order order) {
        logger.info("Cancelling order with ID: {}", order.getId());
        order.setStatus("CANCELLED");
        orderRepository.save(order);
        logger.info("Order with ID {} cancelled", order.getId());
    }

    @Transactional
    public Order processOrder(User user) {
        logger.info("Processing order for user: {}", user.getUsername());
        List<CartItem> cartItems = cartItemRepository.findByUser_id(user.getId());

        if (cartItems.isEmpty()) {
            logger.warn("Cart is empty for user: {}", user.getUsername());
            throw new EmptyCartException("Cart is empty");
        }

        double totalPrice = cartItems.stream().mapToDouble(item -> item.getBook().getPrice() * item.getQuantity()).sum();
        logger.info("Total price for order of user {}: {}", user.getUsername(), totalPrice);

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setPaymentDate(LocalDate.now());
        order.setStatus("PLACED");
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);
        logger.info("Order saved with ID: {}", savedOrder.getId());

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Long bookId = cartItem.getBook().getBookId();
            int quantity = cartItem.getQuantity();
            logger.info("Processing cart item: book ID {}, quantity {}", bookId, quantity);
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> {
                        logger.error("Book not found with ID: {}", bookId);
                        return new RuntimeException("Book not found: " + bookId);
                    });

            if (book.getQuantity() >= quantity) {
                book.setQuantity(book.getQuantity() - quantity);
                bookRepository.save(book);
                logger.info("Updated stock for book ID {}: remaining quantity {}", bookId, book.getQuantity());

                OrderItem orderItem = new OrderItem();
                orderItem.setBookName(book.getBookName());
                orderItem.setPrice(cartItem.getBook().getPrice());
                orderItem.setQuantity(quantity);
                orderItem.setBook(book);
                orderItem.setOrder(savedOrder);

                orderItemRepository.save(orderItem);
                orderItems.add(orderItem);
                logger.info("Order item saved: book '{}', quantity {}", book.getBookName(), quantity);
            } else {
                logger.error("Insufficient stock for book '{}'. Requested: {}, Available: {}", book.getBookName(), quantity, book.getQuantity());
                throw new RuntimeException("Insufficient stock for " + book.getBookName());
            }
        }

        savedOrder.setItems(orderItems);
        orderRepository.save(savedOrder);
        logger.info("Order items attached to order ID: {}", savedOrder.getId());

        shoppingCartService.clearCartAfterPayment(user.getId(), savedOrder.getId());
        logger.info("Cart cleared for user ID {} after successful order {}", user.getId(), savedOrder.getId());

        logger.info("Order processing completed for order ID: {}", savedOrder.getId());
        return savedOrder;
    }

    @Override
    public List<Order> getOrders(Long currentUserId) {
        logger.info("Fetching orders for user ID: {}", currentUserId);
        List<Order> orders = orderRepository.findByUser_id(currentUserId);
        logger.info("Retrieved {} orders for user ID: {}", orders.size(), currentUserId);
        return orders;
    }
}







//package com.hcl.service;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.hcl.entity.Book;
//import com.hcl.entity.CartItem;
//import com.hcl.entity.Order;
//import com.hcl.entity.OrderItem;
//import com.hcl.entity.User;
//import com.hcl.exception.EmptyCartException;
//import com.hcl.exception.UserNotFoundException;
//import com.hcl.repository.BookRepository;
//import com.hcl.repository.CartItemRepository;
//import com.hcl.repository.OrderItemRepository;
//import com.hcl.repository.OrderRepository;
//import com.hcl.repository.UserRepository;
//
//@Service
//public class OrderServiceImpl implements OrderService {
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private BookRepository bookRepository;
//
//    @Autowired
//    private CartItemRepository cartItemRepository;
//
//    @Autowired
//    private OrderItemRepository orderItemRepository;
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ShoppingCartService shoppingCartService;
//
//    @Transactional
//    @Override
//    public Order placeOrder(Long userId, List<CartItem> cartItems, String cardNumber, String expiryDate, String cvv) {
//        if (cartItems.isEmpty()) {
//            throw new EmptyCartException("Your cart is empty. Cannot place order.");
//        }
//
//        System.out.println("Processing payment with card number: " + cardNumber + ", expiry: " + expiryDate + ", cvv: " + cvv);
//
//        double totalPrice = cartItems.stream()
//                .mapToDouble(item -> item.getBook().getPrice() * item.getQuantity())
//                .sum();
//
//        Order order = new Order();
//        order.setUser(userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User Not Found")));
//        order.setTotalPrice(totalPrice);
//        order.setOrderDate(LocalDate.now());
//        order.setPaymentDate(LocalDate.now());
//        order.setStatus("PLACED");
//
//        Order savedOrder = orderRepository.save(order);
//
//        List<OrderItem> orderItems = new ArrayList<>();
//        for (CartItem cartItem : cartItems) {
//            Book book = bookRepository.findById(cartItem.getBook().getBookId())
//                    .orElseThrow(() -> new RuntimeException("Book not found: " + cartItem.getBook().getBookId()));
//
//            if (book.getQuantity() >= cartItem.getQuantity()) {
//                book.setQuantity(book.getQuantity() - cartItem.getQuantity());
//                bookRepository.save(book);
//
//                OrderItem orderItem = new OrderItem();
//                orderItem.setBookName(book.getBookName());
//                orderItem.setPrice(cartItem.getBook().getPrice());
//                orderItem.setQuantity(cartItem.getQuantity());
//                orderItem.setBook(book);
//                orderItem.setOrder(savedOrder);
//
//                orderItemRepository.save(orderItem);
//                orderItems.add(orderItem);
//            } else {
//                throw new RuntimeException("Insufficient stock for " + book.getBookName());
//            }
//        }
//
//        savedOrder.setItems(orderItems);
//        return orderRepository.save(savedOrder);
//    }
//
//    @Override
//    public Order findById(Long orderId) {
//        return orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
//    }
//
//    @Override
//    public Order save(Order order) {
//        return orderRepository.save(order);
//    }
//
//    @Override
//    public void cancelOrder(Order order) {
//        order.setStatus("CANCELLED");
//        orderRepository.save(order);
//    }
//
//    @Transactional
//    public Order processOrder(User user) {
//        List<CartItem> cartItems = cartItemRepository.findByUser_id(user.getId());
//
//        if (cartItems.isEmpty()) {
//            throw new EmptyCartException("Cart is empty");
//        }
//
//        double totalPrice = cartItems.stream().mapToDouble(item -> item.getBook().getPrice() * item.getQuantity()).sum();
//
//        Order order = new Order();
//        order.setUser(user);
//        order.setOrderDate(LocalDate.now());
//        order.setPaymentDate(LocalDate.now());
//        order.setStatus("PLACED");
//        order.setTotalPrice(totalPrice);
//
//        Order savedOrder = orderRepository.save(order);
//
//        List<OrderItem> orderItems = new ArrayList<>();
//        for (CartItem cartItem : cartItems) {
//            Book book = bookRepository.findById(cartItem.getBook().getBookId())
//                    .orElseThrow(() -> new RuntimeException("Book not found: " + cartItem.getBook().getBookId()));
//
//            if (book.getQuantity() >= cartItem.getQuantity()) {
//                book.setQuantity(book.getQuantity() - cartItem.getQuantity());
//                bookRepository.save(book);
//
//                OrderItem orderItem = new OrderItem();
//                orderItem.setBookName(book.getBookName());
//                orderItem.setPrice(cartItem.getBook().getPrice());
//                orderItem.setQuantity(cartItem.getQuantity());
//                orderItem.setBook(book);
//                orderItem.setOrder(savedOrder);
//
//                orderItemRepository.save(orderItem);
//                orderItems.add(orderItem);
//            } else {
//                throw new RuntimeException("Insufficient stock for " + book.getBookName());
//            }
//        }
//
//        savedOrder.setItems(orderItems);
//        orderRepository.save(savedOrder);
//
//        shoppingCartService.clearCartAfterPayment(user.getId(), savedOrder.getId());
//
//        return savedOrder;
//    }
//    @Override
//    public List<Order> getOrders(Long currentUserId) {
//    	return orderRepository.findByUser_id(currentUserId);
//    }
//}
