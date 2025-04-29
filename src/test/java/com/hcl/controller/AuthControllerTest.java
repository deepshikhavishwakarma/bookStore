package com.hcl.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.hcl.entity.User;
import com.hcl.exception.InvalidPasswordException;
import com.hcl.exception.UserAlreadyExistsException;
import com.hcl.exception.UserNotFoundException;
import com.hcl.service.UserService;

class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testLoginSuccess() throws Exception {
        String username = "testuser";
        String password = "password";

        doNothing().when(userService).login(username, password);

        mockMvc.perform(post("/login")
                       .param("username", username)
                       .param("password", password))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/books"));

        verify(userService, times(1)).login(username, password);
    }

   
 
   

    @Test
    void testRegisterUserSuccess() throws Exception {
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("newpassword");

        doNothing().when(userService).registerUser(user);

        mockMvc.perform(post("/register")
                       .flashAttr("user", user))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/login"));

        verify(userService, times(1)).registerUser(user);
    }


    @Test
    void testAccessDenied() throws Exception {
        mockMvc.perform(get("/access-denied"))
               .andExpect(status().isOk())
               .andExpect(view().name("access_denied"));
    }
}




