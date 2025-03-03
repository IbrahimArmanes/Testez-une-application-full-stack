package com.openclassrooms.starterjwt.controllers;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Will use application-test.properties
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private final String REGISTER_URL = "/api/auth/register";
    private final String LOGIN_URL = "/api/auth/login";

    @BeforeEach
    public void setup() {
        // Clear the repository before each test
        userRepository.deleteAll();
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        // Create a signup request
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setFirstName("Test");
        signupRequest.setLastName("User");
        signupRequest.setPassword("password123");

        // Register a new user
        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("User registered successfully!")));

        // Verify the user is saved in the database
        User savedUser = userRepository.findByEmail("test@example.com").orElse(null);
        assertNotNull(savedUser);
        assertEquals("Test", savedUser.getFirstName());
        assertEquals("User", savedUser.getLastName());
        assertFalse(savedUser.isAdmin());
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
    }

    @Test
    public void testRegisterUser_DuplicateEmail() throws Exception {
        // Create and save a user first
        User existingUser = new User("test@example.com", "Existing", "User", 
                passwordEncoder.encode("existingPass"), false);
        userRepository.save(existingUser);

        // Try to register with the same email
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setFirstName("Test");
        signupRequest.setLastName("User");
        signupRequest.setPassword("password123");

        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Error: Email is already taken!")));
    }

    @Test
    public void testLoginUser_Success() throws Exception {
        // Create and save a user first
        User existingUser = new User("test@example.com", "Test", "User", 
                passwordEncoder.encode("password123"), false);
        userRepository.save(existingUser);

        // Create login request
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // Perform login
        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username", is("test@example.com")))
                .andExpect(jsonPath("$.firstName", is("User")))
                .andExpect(jsonPath("$.lastName", is("Test")))
                .andExpect(jsonPath("$.admin", is(false)));
    }

    @Test
    public void testLoginUser_InvalidCredentials() throws Exception {
        // Create and save a user first
        User existingUser = new User("test@example.com", "Test", "User", 
                passwordEncoder.encode("password123"), false);
        userRepository.save(existingUser);

        // Create login request with wrong password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");

        // Perform login
        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLoginUser_UserNotFound() throws Exception {
        // Create login request for non-existing user
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password123");

        // Perform login
        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
