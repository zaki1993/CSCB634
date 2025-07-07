package com.nbu.CSCB634.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    private Model model;

    @InjectMocks
    private HomeController homeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
    }

    @Test
    void testHome_Success() {
        // When
        String result = homeController.home();

        // Then
        assertEquals("home", result);
    }

    @Test
    void testError_WithException() {
        // Given
        Exception exception = new RuntimeException("Test error");

        // When
        String result = homeController.error(model, exception);

        // Then
        assertEquals("error", result);
        verify(model).addAttribute("error", "Test error");
    }

    @Test
    void testError_WithNullException() {
        // When
        String result = homeController.error(model, null);

        // Then
        assertEquals("error", result);
        verify(model).addAttribute("error", "Unknown error");
    }

    @Test
    void testError_WithExceptionMessage() {
        // Given
        Exception exception = new IllegalArgumentException("Invalid argument");

        // When
        String result = homeController.error(model, exception);

        // Then
        assertEquals("error", result);
        verify(model).addAttribute("error", "Invalid argument");
    }

    @Test
    void testError_WithExceptionWithNullMessage() {
        // Given
        Exception exception = new RuntimeException((String) null);

        // When
        String result = homeController.error(model, exception);

        // Then
        assertEquals("error", result);
        verify(model).addAttribute("error", "Unknown error");
    }

    @Test
    void testError_WithNestedExceptionMessage() {
        // Given
        Exception cause = new IllegalStateException("Root cause");
        Exception exception = new RuntimeException("Wrapper exception", cause);

        // When
        String result = homeController.error(model, exception);

        // Then
        assertEquals("error", result);
        verify(model).addAttribute("error", "Wrapper exception");
    }

    @Test
    void testHandleError_Success() {
        // When
        String result = homeController.handleError(model);

        // Then
        assertEquals("error", result);
        verify(model).addAttribute("error", "An error occurred");
    }

    @Test
    void testGetErrorPath() {
        // When
        String result = homeController.getErrorPath();

        // Then
        assertEquals("/error", result);
    }

    @Test
    void testLogin_Success() {
        // When
        String result = homeController.login();

        // Then
        assertEquals("login", result);
    }

    @Test
    void testRegister_Success() {
        // When
        String result = homeController.register();

        // Then
        assertEquals("register", result);
    }

    @Test
    void testAccessDenied_Success() {
        // When
        String result = homeController.accessDenied();

        // Then
        assertEquals("403", result);
    }

    @Test
    void testNotFound_Success() {
        // When
        String result = homeController.notFound();

        // Then
        assertEquals("404", result);
    }

    @Test
    void testInternalServerError_Success() {
        // When
        String result = homeController.internalServerError();

        // Then
        assertEquals("500", result);
    }
} 