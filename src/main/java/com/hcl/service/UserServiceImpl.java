package com.hcl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hcl.entity.User;
import com.hcl.exception.InvalidPasswordException;
import com.hcl.exception.UserAlreadyExistsException;
import com.hcl.exception.UserNotFoundException;
import com.hcl.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
  private UserRepository userRepository;

	@Autowired
  private PasswordEncoder passwordEncoder;

	@Override
	public void registerUser(User user) throws UserAlreadyExistsException {
		logger.info("Attempting registration for user: {}", user.getUsername());
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			logger.warn("Registration failed: Username already exists for {}", user.getUsername());
			throw new UserAlreadyExistsException("Username already exists.");
		}
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			logger.warn("Registration failed: Email already exists for {}", user.getEmail());
			throw new UserAlreadyExistsException("Email already exists.");
		}

		if (user.getRole() == null || user.getRole().isEmpty()) {
			user.setRole("USER");
			logger.info("User role not specified, setting default role to USER for {}", user.getUsername());
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
		logger.info("User registered successfully: {}", user.getUsername());
	}
	
	@Override
  public void login(String username, String password) throws UserNotFoundException, InvalidPasswordException {
		logger.info("Attempting login for user: {}", username);
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> {
					logger.warn("Login failed: User not found for username {}", username);
					return new UserNotFoundException("Invalid username or password.");
				});
		
		if (!passwordEncoder.matches(password, user.getPassword())) {
			logger.warn("Login failed: Invalid password for user {}", username);
			throw new InvalidPasswordException("Invalid username or password.");
			}// Authentication success is handled by Spring Security after this point
		logger.info("Login successful for user: {}", username);
}
}








//package com.hcl.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import com.hcl.entity.User;
//import com.hcl.exception.InvalidPasswordException;
//import com.hcl.exception.UserAlreadyExistsException;
//import com.hcl.exception.UserNotFoundException;
//import com.hcl.repository.UserRepository;
//
//@Service
//public class UserServiceImpl implements UserService {
//
//	@Autowired
//    private UserRepository userRepository;
//
//	@Autowired
//    private PasswordEncoder passwordEncoder;
//
//	@Override
//	public void registerUser(User user) throws UserAlreadyExistsException {
//		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
//			throw new UserAlreadyExistsException("Username already exists.");
//		}
//		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
//			throw new UserAlreadyExistsException("Email already exists.");
//		}
//
//		if (user.getRole() == null || user.getRole().isEmpty()) {
//			user.setRole("USER");
//		}
//
//		user.setPassword(passwordEncoder.encode(user.getPassword()));
//		userRepository.save(user);
//	}
//	
//	@Override
//    public void login(String username, String password) throws UserNotFoundException, InvalidPasswordException {
//		User user = userRepository.findByUsername(username)
//				.orElseThrow(() -> new UserNotFoundException("Invalid username or password.")); // Keep this for user not found
//		
//		if (!passwordEncoder.matches(password, user.getPassword())) {
//			throw new InvalidPasswordException("Invalid username or password.");
//			}// Authentication success is handled by Spring Security after this point
//}
//}