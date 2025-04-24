package com.hcl.service;
import java.util.List;

import com.hcl.entity.CartItem;

public interface ShoppingCartService {
    List<CartItem> getCartItems(Long userId);
    void addItem(Long userId, Long bookId, int quantity);
    void updateItemQuantity(Long userId, Long itemId, int quantity);
    void removeItem(Long userId, Long itemId);
    void clearCart(Long userId);
    double getTotalPrice(Long userId);
    //void restoreItems(Long userId, List<CartItem> items);
    void clearCartAfterPayment(Long userId, Long orderId); // Add this line

}