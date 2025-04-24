package com.hcl.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hcl.config.CustomUserDetails;
import com.hcl.entity.CartItem;
import com.hcl.entity.Order;
import com.hcl.service.OrderService;
import com.hcl.service.ShoppingCartService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/place")
    public String placeOrder(@RequestParam String cardNumber, // Receive card number
                                     @RequestParam String expiryDate, // Receive expiry date
                                     @RequestParam String cvv, // Receive CVV
                                     HttpSession session, // You might still need the session for temporary data
                                     RedirectAttributes redirectAttributes) {

        Long userId = getCurrentUserId();
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "User is not authenticated.");
            return "redirect:/login";
        }

        List<CartItem> cartItems = shoppingCartService.getCartItems(userId);
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Your cart is empty.");
            return "redirect:/cart";
        }

		/*
		 * try { // Delegate the order placement to the OrderService with payment
		 * details Order order = orderService.placeOrder(userId, cartItems, cardNumber,
		 * expiryDate, cvv); shoppingCartService.clearCart(userId); return
		 * "redirect:/order/confirmation/" + order.getId(); // Redirect with order ID }
		 * catch (Exception e) { redirectAttributes.addFlashAttribute("error",
		 * "Failed to place order: " + e.getMessage()); return "redirect:/checkout"; //
		 * Redirect back to checkout with an error }
		 */
        try {
            Order order = orderService.placeOrder(userId, cartItems, cardNumber, expiryDate, cvv);
            System.out.println("Order placed successfully with ID: " + order.getId());
            System.out.println("Attempting to clear cart...");
            shoppingCartService.clearCart(userId);
            System.out.println("Cart cleared successfully.");
            return "redirect:/order/confirmation/" + order.getId();//  order-confirmation
            //return "order-confirmation";//  order-confirmation
            
        } catch (Exception e) {
            System.err.println("Exception caught in placeOrder: " + e.getMessage());
            e.printStackTrace(); // This is crucial!
            redirectAttributes.addFlashAttribute("error", "Failed to place order: " + e.getMessage());
            return "redirect:/checkout";
        }
    }
    
    @GetMapping("/{orderId}")
    public String viewOrderDetails(@PathVariable Long orderId, Model model) {
        Order order = orderService.findById(orderId);
        model.addAttribute("order", order);
        return "order-details"; // Assuming you have an order-details.html template
    }
      
    

 

    @GetMapping("/cancel/{orderId}")
    public String cancelOrder(@PathVariable Long orderId,
                              RedirectAttributes redirectAttributes) {

        Long userId = getCurrentUserId();
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "User is not authenticated.");
            return "redirect:/login";
        }

        Order order = orderService.findById(orderId);
        if (order == null) {
            redirectAttributes.addFlashAttribute("error", "Order not found.");
            return "redirect:/orders";
        }

        if (!order.getUser().getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to cancel this order.");
            return "redirect:/orders";
        }

        if (!order.getStatus().equals("PLACED")) {
            redirectAttributes.addFlashAttribute("error", "Only placed orders can be cancelled.");
            return "redirect:/orders";
        }

        if (ChronoUnit.DAYS.between(order.getOrderDate(), LocalDate.now()) > 7) {
            redirectAttributes.addFlashAttribute("error", "Cancellation period (7 days) has expired.");
            return "redirect:/orders";
        }

        try {
            orderService.cancelOrder(order);
            //shoppingCartService.restoreItems(userId, order.getItems());
            redirectAttributes.addFlashAttribute("message", "Order cancelled and items returned to cart.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to cancel order: " + e.getMessage());
        }

        return "redirect:/orders";
    }

    @GetMapping("/confirmation/{orderId}")
    public String orderConfirmation(@PathVariable Long orderId, Model model) {
        Order order = orderService.findById(orderId);
        if (order == null) {
            return "error/order-not-found"; // Create an appropriate error page
        }
        model.addAttribute("order", order);
        return "order-confirmation"; // Create an order confirmation Thymeleaf page
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getId();
        }
        return null;
    }
    @GetMapping("/get/orders")
    private String getAllOrder(Map<String,Object> map)
    {
    	Long currentUserId = getCurrentUserId();
    	List<Order> allOrder = orderService.getOrders(currentUserId);
    	map.put("orders", allOrder);
    	return "order-details";
    }
}