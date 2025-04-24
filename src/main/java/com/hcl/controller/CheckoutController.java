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
            // Handle the case where the user is not authenticated
            return "redirect:/login"; // Or some other appropriate action
        }
    }


    // Helper method to get the currently logged-in user's ID
    /*
     * private Long getCurrentUserId() { Authentication authentication =
     * SecurityContextHolder.getContext().getAuthentication(); if (authentication !=
     * null && authentication.isAuthenticated() && authentication.getPrincipal()
     * instanceof org.springframework.security.core.userdetails.UserDetails) {
     * String username =
     * ((org.springframework.security.core.userdetails.UserDetails)
     * authentication.getPrincipal()).getUsername(); // You'll need to fetch the
     * actual User entity to get the ID // For now, placeholder: return 1L; } return
     * null; }
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails) {
            Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
            logger.debug("Current user ID retrieved from CustomUserDetails: {}", userId);
            return userId;
        }
        logger.debug("No authenticated user found.");
        return null; // Or handle the unauthenticated case as needed
    }
}









//package com.hcl.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import com.hcl.config.CustomUserDetails;
//import com.hcl.service.ShoppingCartService;
//
//@Controller
//@RequestMapping("/checkout")
//public class CheckoutController {
//
//    @Autowired
//    private ShoppingCartService shoppingCartService;
//
//    @GetMapping
//    public String checkout(Model model) {
//        Long userId = getCurrentUserId();
//        if (userId != null) {
//            model.addAttribute("cartItems", shoppingCartService.getCartItems(userId));
//            model.addAttribute("totalPrice", shoppingCartService.getTotalPrice(userId));
//            return "checkout";
//        } else {
//            // Handle the case where the user is not authenticated
//            return "redirect:/login"; // Or some other appropriate action
//        }
//    }
//
//
//    // Helper method to get the currently logged-in user's ID
//	/*
//	 * private Long getCurrentUserId() { Authentication authentication =
//	 * SecurityContextHolder.getContext().getAuthentication(); if (authentication !=
//	 * null && authentication.isAuthenticated() && authentication.getPrincipal()
//	 * instanceof org.springframework.security.core.userdetails.UserDetails) {
//	 * String username =
//	 * ((org.springframework.security.core.userdetails.UserDetails)
//	 * authentication.getPrincipal()).getUsername(); // You'll need to fetch the
//	 * actual User entity to get the ID // For now, placeholder: return 1L; } return
//	 * null; }
//	 */
//    private Long getCurrentUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails) {
//            return ((CustomUserDetails) authentication.getPrincipal()).getId();
//        }
//        return null; // Or handle the unauthenticated case as needed
//    }
//}