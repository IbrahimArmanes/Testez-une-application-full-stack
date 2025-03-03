package com.openclassrooms.starterjwt.payload.request;

import static org.junit.jupiter.api.Assertions.*;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.var;

public class SignupRequestTest {

    private Validator validator;
    private SignupRequest signupRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        signupRequest = new SignupRequest();
    }

    @Test
    void testValidSignupRequest() {
        signupRequest.setEmail("test@test.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("password123");

        var violations = validator.validate(signupRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testEmailValidation() {
        signupRequest.setEmail("invalid-email");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("password123");

        var violations = validator.validate(signupRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void testFirstNameSizeValidation() {
        signupRequest.setEmail("test@test.com");
        signupRequest.setFirstName("Jo"); // Too short
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("password123");

        var violations = validator.validate(signupRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void testLastNameSizeValidation() {
        signupRequest.setEmail("test@test.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("D"); // Too short
        signupRequest.setPassword("password123");

        var violations = validator.validate(signupRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void testPasswordSizeValidation() {
        signupRequest.setEmail("test@test.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("pass"); // Too short

        var violations = validator.validate(signupRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void testLombokGettersAndSetters() {
        String email = "test@test.com";
        String firstName = "John";
        String lastName = "Doe";
        String password = "password123";

        signupRequest.setEmail(email);
        signupRequest.setFirstName(firstName);
        signupRequest.setLastName(lastName);
        signupRequest.setPassword(password);

        assertEquals(email, signupRequest.getEmail());
        assertEquals(firstName, signupRequest.getFirstName());
        assertEquals(lastName, signupRequest.getLastName());
        assertEquals(password, signupRequest.getPassword());
    }

    @Test
    void testLombokEqualsAndHashCode() {
        SignupRequest request1 = new SignupRequest();
        SignupRequest request2 = new SignupRequest();

        request1.setEmail("test@test.com");
        request1.setFirstName("John");
        request1.setLastName("Doe");
        request1.setPassword("password123");

        request2.setEmail("test@test.com");
        request2.setFirstName("John");
        request2.setLastName("Doe");
        request2.setPassword("password123");

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }
}
