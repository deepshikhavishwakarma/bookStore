package com.hcl.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hcl.entity.User;
import com.hcl.exception.InvalidPasswordException;

import com.hcl.exception.UserAlreadyExistsException;
import com.hcl.exception.UserNotFoundException;
import com.hcl.service.UserService;

@Controller
public class AuthController {
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	   
	@Autowired
	private UserService userService;

	@GetMapping("/login")
	public String showLoginForm(Model model) {
		logger.info("Displaying login form");
	       
		model.addAttribute("user", new User());
		return "login";
	}

	@PostMapping("/login")
	public String login(@RequestParam("username") String username, @RequestParam("password") String password,
			Model model) {
		 logger.info("Attempting login for user: {}", username);
	       
		try {
			userService.login(username, password);
			logger.info("Login successful for user: {}", username);
            
			return "redirect:/books"; // Redirect to the book list
		} catch (UserNotFoundException | InvalidPasswordException e) {
			logger.warn("Login failed for user: {}. Error: {}", username, e.getMessage());
            
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("user", new User());
			return "login";
		}
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		 logger.info("Displaying registration form");
		model.addAttribute("user", new User());
		return "register";
	}

	@PostMapping("/register")
	public String registerUser(@ModelAttribute("user") User user, Model model) {
		logger.info("Attempting registration for user: {}", user.getUsername());
        
		try {
			userService.registerUser(user);
			logger.info("Registration successful for user: {}", user.getUsername());
            
			return "redirect:/login";
		} catch (UserAlreadyExistsException e) {
			logger.warn("Registration failed for user: {}. Error: {}", user.getUsername(), e.getMessage());
            
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("user", user);
			return "register";
		}
	}

	@GetMapping("/access-denied")
	public String accessDenied() {
		logger.warn("Access denied attempt");
		return "access_denied"; // Thymeleaf template name
	}

}