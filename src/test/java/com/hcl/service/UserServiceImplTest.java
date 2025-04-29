package com.hcl.service;

import com.hcl.entity.User;
import com.hcl.exception.InvalidPasswordException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUser_newUser_userIsSavedWithEncodedPasswordAndDefaultRole() throws UserAlreadyExistsException {
        // Given
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("password");
        newUser.setEmail("test@example.com");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // When
        userService.registerUser(newUser);

        // Then
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(argThat(user ->
                user.getUsername().equals("testuser") &&
                user.getPassword().equals("encodedPassword") &&
                user.getRole().equals("USER")
        ));
    }

    @Test
    void registerUser_newUserWithRole_userIsSavedWithEncodedPasswordAndProvidedRole() throws UserAlreadyExistsException {
        // Given
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("password");
        newUser.setEmail("test@example.com");
        newUser.setRole("ADMIN");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // When
        userService.registerUser(newUser);

        // Then
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(argThat(user ->
                user.getUsername().equals("testuser") &&
                user.getPassword().equals("encodedPassword") &&
                user.getRole().equals("ADMIN")
        ));
    }

    @Test
    void registerUser_existingUsername_throwsUserAlreadyExistsException() {
        // Given
        User existingUser = new User();
        existingUser.setUsername("existinguser");
        User newUser = new User();
        newUser.setUsername("existinguser");
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));

        // When, Then
        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(newUser));
        verify(userRepository, times(1)).findByUsername("existinguser");
        verify(userRepository, never()).findByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_existingEmail_throwsUserAlreadyExistsException() {
        // Given
        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("existing@example.com");
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        // When, Then
        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(newUser));
        verify(userRepository, times(1)).findByUsername("newuser");
        verify(userRepository, times(1)).findByEmail("existing@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_validCredentials_doesNotThrowException() throws UserNotFoundException, InvalidPasswordException {
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

    @Test
    void login_invalidPassword_throwsInvalidPasswordException() {
        // Given
        String username = "testuser";
        String password = "wrongpassword";
        User existingUser = new User();
        existingUser.setUsername(username);
        existingUser.setPassword("encodedPassword");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);

        // When, Then
        assertThrows(InvalidPasswordException.class, () -> userService.login(username, password));
        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, "encodedPassword");
    }
}