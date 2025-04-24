package com.hcl.controller;

import java.util.List;

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
import com.hcl.entity.CartItem;
import com.hcl.exception.BookNotFoundException;
import com.hcl.exception.InsufficientStockException;
import com.hcl.service.BookService;
import com.hcl.service.ShoppingCartService;

@Controller
@RequestMapping("/cart")
public class ShoppingCartController {

	@Autowired
	private ShoppingCartService shoppingCartService;

	 @Autowired
	    private BookService bookService;
	 
	@GetMapping
	public String viewCart(Model model) {
		Long userId = getCurrentUserId();
		model.addAttribute("cartItems", shoppingCartService.getCartItems(userId));
		model.addAttribute("totalPrice", shoppingCartService.getTotalPrice(userId));
		return "cart";
	}

	/*
	 * @GetMapping public String viewCart(Model model) { Long userId =
	 * getCurrentUserId(); if (userId != null) { List<CartItem> cartItems =
	 * shoppingCartService.getCartItems(userId); model.addAttribute("cartItems",
	 * cartItems); } else { model.addAttribute("error", "User not authenticated.");
	 * } return "cart"; // Name of your Thymeleaf cart page }
	 */
	/*
	 * @PostMapping("/add") public String addItem(@RequestParam("bookId") Long
	 * bookId, @RequestParam("quantity") int quantity) { Long userId =
	 * getCurrentUserId(); shoppingCartService.addItem(userId, bookId, quantity);
	 * return "redirect:/cart"; }
	 */

	@PostMapping("/add")
	public String addItem(@RequestParam("bookId") Long bookId, @RequestParam("quantity") int quantity,
			RedirectAttributes redirectAttributes) {
		Long userId = getCurrentUserId();

		if (userId == null) {
			return "redirect:/login"; // Or handle unauthenticated access appropriately
		}

		try {
			if (bookService.getBookById(bookId).getQuantity() < quantity) {
                throw new InsufficientStockException("Insufficient stock for this book. Available quantity: " + bookService.getBookById(bookId).getQuantity());
            }
			shoppingCartService.addItem(userId, bookId, quantity);
			redirectAttributes.addFlashAttribute("message", "Book added to cart successfully!");
		} catch (BookNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		} catch (InsufficientStockException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error adding book to cart.");
			// Log the error: e.printStackTrace();
		}

		return "redirect:/books"; // Redirect back to the book list
	}

	@PostMapping("/update")
	public String updateItem(@RequestParam("itemId") Long itemId, @RequestParam("quantity") int quantity) {
		Long userId = getCurrentUserId();
		shoppingCartService.updateItemQuantity(userId, itemId, quantity);
		return "redirect:/cart";
	}

	@PostMapping("/remove")
	public String removeItem(@RequestParam("itemId") Long itemId) {
		Long userId = getCurrentUserId();
		shoppingCartService.removeItem(userId, itemId);
		return "redirect:/cart";
	}

	@GetMapping("/clear")
	public String clearCart() {
		Long userId = getCurrentUserId();
		shoppingCartService.clearCart(userId);
		return "redirect:/cart";
	}

	// Helper method to get the currently logged-in user's ID
	private Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			return userDetails.getId();
		}
		throw new RuntimeException("User not authenticated");
	}

	@GetMapping("/checkout")
	public String showCheckout(Model model) {
		Long userId = getCurrentUserId();
		List<CartItem> cartItems = shoppingCartService.getCartItems(userId);
		double totalPrice = shoppingCartService.getTotalPrice(userId);
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("totalPrice", totalPrice);
		return "checkout";
	}

	@GetMapping("/cart/payment")
	public String showPaymentPage(Model model) {
		Long userId = getCurrentUserId(); // however you're fetching current user
		double totalPrice = shoppingCartService.getTotalPrice(userId);

		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("paymentDate", java.time.LocalDate.now());

		return "payment";
		// loads payment.html from templates
	}
	
	@PostMapping("/clear-frontend")
    public ResponseEntity<String> clearCartFrontend() {
        Long userId = getCurrentUserId();
        shoppingCartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared successfully");
    }

}