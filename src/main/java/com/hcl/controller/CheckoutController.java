package com.hcl.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hcl.config.CustomUserDetails;
import com.hcl.service.ShoppingCartService;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping
    public String checkout(Model model) {
        logger.info("Entering checkout method");
        Long userId = getCurrentUserId();
        if (userId != null) {
            logger.info("User ID found: {}", userId);
            model.addAttribute("cartItems", shoppingCartService.getCartItems(userId));
            model.addAttribute("totalPrice", shoppingCartService.getTotalPrice(userId));
            logger.info("Retrieved cart items and total price for user ID: {}", userId);
            logger.info("Exiting checkout method, returning 'checkout' view");
            return "checkout";
        } else {
            logger.warn("User not authenticated, redirecting to login");
            
            return "redirect:/login";
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails) {
            Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
            logger.debug("Current user ID retrieved from CustomUserDetails: {}", userId);
            return userId;
        }
        logger.debug("No authenticated user found.");
        return null; 
    }
}







