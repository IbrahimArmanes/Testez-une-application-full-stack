package com.openclassrooms.starterjwt.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "yourTestSecretKey");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400000);
    }

    @Test
    void generateJwtToken_ShouldReturnValidToken() {
        // Arrange
        UserDetailsImpl userPrincipal = UserDetailsImpl.builder()
            .username("testuser")
            .build();
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        // Act
        String token = jwtUtils.generateJwtToken(authentication);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void getUserNameFromJwtToken_ShouldReturnCorrectUsername() {
        // Arrange
        UserDetailsImpl userPrincipal = UserDetailsImpl.builder()
            .username("testuser")
            .build();
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        String token = jwtUtils.generateJwtToken(authentication);

        // Act
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // Assert
        assertEquals("testuser", username);
    }

    @Test
    void validateJwtToken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        UserDetailsImpl userPrincipal = UserDetailsImpl.builder()
            .username("testuser")
            .build();
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        String token = jwtUtils.generateJwtToken(authentication);

        // Act & Assert
        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtToken_WithInvalidToken_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(jwtUtils.validateJwtToken("invalidToken"));
    }

    @Test
    void validateJwtToken_WithEmptyToken_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(jwtUtils.validateJwtToken(""));
    }

    @Test
    void validateJwtToken_WithNullToken_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(jwtUtils.validateJwtToken(null));
    }
}
