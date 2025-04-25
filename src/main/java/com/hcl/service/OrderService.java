package com.hcl.service;

import java.util.List;

import com.hcl.entity.CartItem;
import com.hcl.entity.Order;

public interface OrderService {
    Order findById(Long orderId);  
    Order save(Order order);
    void cancelOrder(Order order);
	Order placeOrder(Long userId, List<CartItem> cartItems, String cardNumber, String expiryDate, String cvv);
	List<Order> getOrders(Long currentUserId);
}