package com.hcl.config;

import com.hcl.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    @Test
    void testGetId() {
        User user = new User();
        user.setId(1L);
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        assertEquals(1L, customUserDetails.getId());
    }

    @Test
    void testGetEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        assertEquals("test@example.com", customUserDetails.getEmail());
    }

    @Test
    void testGetAuthorities_SingleRole() {
        User user = new User();
        user.setRole("user");
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testGetAuthorities_AdminRole() {
        User user = new User();
        user.setRole("ADMIN");
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testGetPassword() {
        User user = new User();
        user.setPassword("password123");
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        assertEquals("password123", customUserDetails.getPassword());
    }

    @Test
    void testGetUsername() {
        User user = new User();
        user.setUsername("testuser");
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        assertEquals("testuser", customUserDetails.getUsername());
    }

    @Test
    void testIsAccountNonExpired() {
        User user = new User();
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        assertTrue(customUserDetails.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        User user = new User();
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        assertTrue(customUserDetails.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        User user = new User();
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        assertTrue(customUserDetails.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        User user = new User();
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        assertTrue(customUserDetails.isEnabled());
    }
}