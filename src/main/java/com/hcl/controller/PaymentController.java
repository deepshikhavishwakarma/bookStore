package com.hcl.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hcl.config.CustomUserDetails;
import com.hcl.entity.CartItem;
import com.hcl.entity.Order;
import com.hcl.entity.User;
import com.hcl.service.ShoppingCartService;

@Controller
@RequestMapping("/payment")
public class PaymentController {
	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class); // Added Logger here

	@Autowired
	private ShoppingCartService shoppingCartService;

	@GetMapping
	public String showPaymentPage(Model model) {
		Long userId = getCurrentUserId();
		logger.info("Displaying payment page for user ID: {}", userId); // Added logging
		double totalPrice = shoppingCartService.getTotalPrice(userId);
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("paymentDate", LocalDate.now());
		return "payment";
	}

	private Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
			logger.debug("User ID from CustomUserDetails: {}",
					((CustomUserDetails) authentication.getPrincipal()).getId()); // Added

			return ((CustomUserDetails) authentication.getPrincipal()).getId();
		}
		logger.warn("User not authenticated in getCurrentUserId()"); // Added
        throw new RuntimeException("User not authenticated");
	}
}
