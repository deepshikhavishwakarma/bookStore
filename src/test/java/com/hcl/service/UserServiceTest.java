package com.hcl.service;

import com.hcl.entity.User;
import com.hcl.exception.UserAlreadyExistsException;
import com.hcl.exception.UserNotFoundException;
import com.hcl.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserServiceImpl userService;

	@Test
	void registerUser_newUser_userIsSaved() throws UserAlreadyExistsException {
		// Given
		User newUser = new User();
		newUser.setUsername("testuser");
		newUser.setPassword("password");
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
		when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(newUser);

		// When
		userService.registerUser(newUser);

		// Then
		verify(userRepository, times(1)).findByUsername("testuser");
		verify(passwordEncoder, times(1)).encode("password");
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void registerUser_existingUser_throwsUserAlreadyExistsException() {
		// Given
		User existingUser = new User();
		existingUser.setUsername("existinguser");
		User newUser = new User();
		newUser.setUsername("existinguser");
		when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));

		// When, Then
		assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(newUser));
		verify(userRepository, times(1)).findByUsername("existinguser");
		verify(passwordEncoder, never()).encode(anyString());
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void login_validCredentials_doesNotThrowException() throws UserNotFoundException {
		// Given
		String username = "testuser";
		String password = "password";
		User existingUser = new User();
		existingUser.setUsername(username);
		existingUser.setPassword("encodedPassword");
		when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
		when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);

		// When
		userService.login(username, password);

		// Then
		verify(userRepository, times(1)).findByUsername(username);
		verify(passwordEncoder, times(1)).matches(password, "encodedPassword");
		// No exception should be thrown
	}

	@Test
	void login_invalidUsername_throwsUserNotFoundException() {
		// Given
		String username = "nonexistentuser";
		String password = "password";
		when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

		// When, Then
		assertThrows(UserNotFoundException.class, () -> userService.login(username, password));
		verify(userRepository, times(1)).findByUsername(username);
		verify(passwordEncoder, never()).matches(anyString(), anyString());
	}

}