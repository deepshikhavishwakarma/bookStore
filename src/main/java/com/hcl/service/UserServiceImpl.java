package com.hcl.service;

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

	@Autowired
    private UserRepository userRepository;

	@Autowired
    private PasswordEncoder passwordEncoder;

	@Override
	public void registerUser(User user) throws UserAlreadyExistsException {
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			throw new UserAlreadyExistsException("Username already exists.");
		}
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new UserAlreadyExistsException("Email already exists.");
		}

		if (user.getRole() == null || user.getRole().isEmpty()) {
			user.setRole("USER");
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
	}
	
	@Override
    public void login(String username, String password) throws UserNotFoundException, InvalidPasswordException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("Invalid username or password.")); // Keep this for user not found
		
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new InvalidPasswordException("Invalid username or password.");
			}// Authentication success is handled by Spring Security after this point
}
}