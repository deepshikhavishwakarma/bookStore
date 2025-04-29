
package com.hcl.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hcl.config.CustomUserDetails;

import com.hcl.exception.BookNotFoundException;
import com.hcl.exception.InsufficientStockException;
import com.hcl.exception.UserNotAuthenticatedException;
import com.hcl.service.BookService;
import com.hcl.service.ShoppingCartService;

@Controller
@RequestMapping("/cart")
public class ShoppingCartController {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartController.class);
    private static final String REDIRECT_CART = "redirect:/cart";
    private static final String TOTAL_PRICE = "totalPrice";
    private static final String ERROR_MESSAGE = "errorMessage"; 

    
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private BookService bookService;

    @GetMapping
    public String viewCart(Model model) {
        Long userId = getCurrentUserId();
        logger.info("Viewing cart for user ID: {}", userId);
        model.addAttribute("cartItems", shoppingCartService.getCartItems(userId));
        model.addAttribute(TOTAL_PRICE, shoppingCartService.getTotalPrice(userId));
        return "cart";
    }


    @PostMapping("/add")
    public String addItem(@RequestParam("bookId") Long bookId, @RequestParam("quantity") int quantity,
                          RedirectAttributes redirectAttributes) {
        Long userId = getCurrentUserId();
        logger.info("Adding book ID {} (quantity {}) to cart for user ID {}", bookId, quantity, userId);
        try {
            if (bookService.getBookById(bookId).getQuantity() < quantity) {
                throw new InsufficientStockException("Insufficient stock for book ID " + bookId);
            }
            shoppingCartService.addItem(userId, bookId, quantity);
            redirectAttributes.addFlashAttribute("message", "Book added to cart successfully!");
        } catch (BookNotFoundException e) {
            logger.warn("Book not found: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, e.getMessage());
        } catch (InsufficientStockException e) {
            logger.warn("Insufficient stock: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding to cart:", e);
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Error adding book to cart.");
        }
        return "redirect:/books";
    }

    @PostMapping("/update")
    public String updateItem(@RequestParam("itemId") Long itemId, @RequestParam("quantity") int quantity) {
        Long userId = getCurrentUserId();
        logger.info("Updating item ID {} to quantity {} for user ID {}", itemId, quantity, userId);
        shoppingCartService.updateItemQuantity(userId, itemId, quantity);
        return REDIRECT_CART;
    }

    @PostMapping("/remove")
    public String removeItem(@RequestParam("itemId") Long itemId) {
        Long userId = getCurrentUserId();
        logger.info("Removing item ID {} from cart for user ID {}", itemId, userId);
        shoppingCartService.removeItem(userId, itemId);
        return REDIRECT_CART;
    }

    @GetMapping("/clear")
    public String clearCart() {
        Long userId = getCurrentUserId();
        logger.info("Clearing cart for user ID {}", userId);
        shoppingCartService.clearCart(userId);
        return REDIRECT_CART;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getId();
        }
        logger.warn("User not authenticated");
        throw new UserNotAuthenticatedException("User not authenticated");
    }

    @GetMapping("/checkout")
    public String showCheckout(Model model) {
        Long userId = getCurrentUserId();
        logger.info("Navigating to checkout for user ID {}", userId);
        model.addAttribute("cartItems", shoppingCartService.getCartItems(userId));
        model.addAttribute(TOTAL_PRICE, shoppingCartService.getTotalPrice(userId));
        return "checkout";
    }

    @GetMapping("/cart/payment")
    public String showPaymentPage(Model model) {
        Long userId = getCurrentUserId();
        logger.info("Navigating to payment page for user ID {}", userId);
        double totalPrice = shoppingCartService.getTotalPrice(userId);
        model.addAttribute(TOTAL_PRICE, totalPrice);
        model.addAttribute("paymentDate", java.time.LocalDate.now());
        return "payment";
    }

    @PostMapping("/clear-frontend")
    public ResponseEntity<String> clearCartFrontend() {
        Long userId = getCurrentUserId();
        logger.info("Clearing cart via frontend request for user ID {}", userId);
        shoppingCartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared successfully");
    }

}





