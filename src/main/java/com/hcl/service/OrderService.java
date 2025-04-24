package com.hcl.service;

import java.util.List;

import com.hcl.entity.CartItem;
import com.hcl.entity.Order;

public interface OrderService {
//    void placeOrder(Long userId, String cardNumber, String expiryDate, String cvv);
    Order findById(Long orderId);  // Add this method
    Order save(Order order);
    void cancelOrder(Order order);
	Order placeOrder(Long userId, List<CartItem> cartItems, String cardNumber, String expiryDate, String cvv);
	List<Order> getOrders(Long currentUserId);
}