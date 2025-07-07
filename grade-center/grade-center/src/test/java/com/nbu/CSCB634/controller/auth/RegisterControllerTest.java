package com.nbu.CSCB634.controller.auth;

import com.nbu.CSCB634.model.auth.User;
import com.nbu.CSCB634.service.auth.UserService;
import com.nbu.CSCB634.service.exceptions.UserAlreadyExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RegisterControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterController registerController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(registerController).build();
    }

    @Test
    void testProcessRegister_Success() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        when(userService.registerUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/register")
                .param("username", "testuser")
                .param("password", "password123")
                .param("confirmPassword", "password123")
                .param("email", "test@example.com")
                .param("firstName", "John")
                .param("lastName", "Doe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService, times(1)).registerUser(any(User.class));
    }

    @Test
    void testProcessRegister_WithLongPassword() throws Exception {
        String longPassword = "a".repeat(100);
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(longPassword);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        when(userService.registerUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/register")
                .param("username", "testuser")
                .param("password", longPassword)
                .param("confirmPassword", longPassword)
                .param("email", "test@example.com")
                .param("firstName", "John")
                .param("lastName", "Doe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService, times(1)).registerUser(any(User.class));
    }

    @Test
    void testProcessRegister_WithSpecialCharacters() throws Exception {
        User user = new User();
        user.setUsername("test@user#123");
        user.setPassword("p@ssw0rd!123");
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        when(userService.registerUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/register")
                .param("username", "test@user#123")
                .param("password", "p@ssw0rd!123")
                .param("confirmPassword", "p@ssw0rd!123")
                .param("email", "test@example.com")
                .param("firstName", "John")
                .param("lastName", "Doe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService, times(1)).registerUser(any(User.class));
    }
} 