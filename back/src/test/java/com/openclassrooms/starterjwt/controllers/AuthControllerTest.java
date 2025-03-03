package com.openclassrooms.starterjwt.controllers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private Authentication authentication;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(authenticationManager, passwordEncoder, jwtUtils, userRepository);
    }

    @Test
    void authenticateUser_Success() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("password123");

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        User user = new User();
        user.setAdmin(true);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("test-jwt-token");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assert
        assertTrue(response.getBody() instanceof JwtResponse);
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertEquals("test-jwt-token", jwtResponse.getToken());
        assertEquals(userDetails.getId(), jwtResponse.getId());
        assertEquals(userDetails.getUsername(), jwtResponse.getUsername());
        assertEquals(userDetails.getFirstName(), jwtResponse.getFirstName());
        assertEquals(userDetails.getLastName(), jwtResponse.getLastName());
        assertTrue(jwtResponse.getAdmin());
    }

    @Test
    void registerUser_Success() {
        // Arrange
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setEmail("new@test.com");
        signUpRequest.setPassword("password123");
        signUpRequest.setFirstName("John");
        signUpRequest.setLastName("Doe");

        when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");

        // Act
        ResponseEntity<?> response = authController.registerUser(signUpRequest);

        // Assert
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertEquals("User registered successfully!", messageResponse.getMessage());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_EmailTaken() {
        // Arrange
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setEmail("existing@test.com");

        when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(true);

        // Act
        ResponseEntity<?> response = authController.registerUser(signUpRequest);

        // Assert
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertEquals("Error: Email is already taken!", messageResponse.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
