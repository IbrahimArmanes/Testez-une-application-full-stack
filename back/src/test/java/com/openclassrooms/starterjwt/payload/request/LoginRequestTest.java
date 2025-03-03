package com.openclassrooms.starterjwt.payload.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class LoginRequestTest {

    @Test
    void testLoginRequestGettersAndSetters() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        String testEmail = "test@test.com";
        String testPassword = "password123";

        // When
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(testPassword);

        // Then
        assertEquals(testEmail, loginRequest.getEmail());
        assertEquals(testPassword, loginRequest.getPassword());
    }

    @Test
    void testLoginRequestDefaultConstructor() {
        // When
        LoginRequest loginRequest = new LoginRequest();

        // Then
        assertNull(loginRequest.getEmail());
        assertNull(loginRequest.getPassword());
    }

    @Test
    void testEmailSetter() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        String testEmail = "user@example.com";

        // When
        loginRequest.setEmail(testEmail);

        // Then
        assertEquals(testEmail, loginRequest.getEmail());
    }

    @Test
    void testPasswordSetter() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        String testPassword = "securePassword123";

        // When
        loginRequest.setPassword(testPassword);

        // Then
        assertEquals(testPassword, loginRequest.getPassword());
    }
}
