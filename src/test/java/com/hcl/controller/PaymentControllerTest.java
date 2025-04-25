package com.hcl.controller;

import com.hcl.config.CustomUserDetails;
import com.hcl.service.ShoppingCartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

	 @Autowired
	    private MockMvc mockMvc;

	    @MockBean
	    private ShoppingCartService shoppingCartService;

	    @Test
	    public void testShowPaymentPage_UnauthenticatedUser() throws Exception {
	        // Act and Assert
	        mockMvc.perform(get("/payment"))
	               .andExpect(status().isUnauthorized()); // Expect 401 because getCurrentUserId throws an exception
	    }
}