package com.hcl.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        if (cartItems.isEmpty()) {
            throw new EmptyCartException("Your cart is empty. Cannot place order.");
        }

        System.out.println("Processing payment with card number: " + cardNumber + ", expiry: " + expiryDate + ", cvv: " + cvv);

        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getBook().getPrice() * item.getQuantity())
                .sum();

        Order order = new Order();
        order.setUser(userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User Not Found")));
        order.setTotalPrice(totalPrice);
        order.setOrderDate(LocalDate.now());
        order.setPaymentDate(LocalDate.now());
        order.setStatus("PLACED");

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Book book = bookRepository.findById(cartItem.getBook().getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found: " + cartItem.getBook().getBookId()));

            if (book.getQuantity() >= cartItem.getQuantity()) {
                book.setQuantity(book.getQuantity() - cartItem.getQuantity());
                bookRepository.save(book);

                OrderItem orderItem = new OrderItem();
                orderItem.setBookName(book.getBookName());
                orderItem.setPrice(cartItem.getBook().getPrice());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setBook(book);
                orderItem.setOrder(savedOrder);

                orderItemRepository.save(orderItem);
                orderItems.add(orderItem);
            } else {
                throw new RuntimeException("Insufficient stock for " + book.getBookName());
            }
        }

        savedOrder.setItems(orderItems);
        return orderRepository.save(savedOrder);
    }

    @Override
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public void cancelOrder(Order order) {
        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }

    @Transactional
    public Order processOrder(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUser_id(user.getId());

        if (cartItems.isEmpty()) {
            throw new EmptyCartException("Cart is empty");
        }

        double totalPrice = cartItems.stream().mapToDouble(item -> item.getBook().getPrice() * item.getQuantity()).sum();

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setPaymentDate(LocalDate.now());
        order.setStatus("PLACED");
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Book book = bookRepository.findById(cartItem.getBook().getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found: " + cartItem.getBook().getBookId()));

            if (book.getQuantity() >= cartItem.getQuantity()) {
                book.setQuantity(book.getQuantity() - cartItem.getQuantity());
                bookRepository.save(book);

                OrderItem orderItem = new OrderItem();
                orderItem.setBookName(book.getBookName());
                orderItem.setPrice(cartItem.getBook().getPrice());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setBook(book);
                orderItem.setOrder(savedOrder);

                orderItemRepository.save(orderItem);
                orderItems.add(orderItem);
            } else {
                throw new RuntimeException("Insufficient stock for " + book.getBookName());
            }
        }

        savedOrder.setItems(orderItems);
        orderRepository.save(savedOrder);

        shoppingCartService.clearCartAfterPayment(user.getId(), savedOrder.getId());

        return savedOrder;
    }
    @Override
    public List<Order> getOrders(Long currentUserId) {
    	return orderRepository.findByUser_id(currentUserId);
    }
}
