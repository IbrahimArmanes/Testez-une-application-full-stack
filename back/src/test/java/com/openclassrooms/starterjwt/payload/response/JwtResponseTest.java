package com.openclassrooms.starterjwt.payload.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class JwtResponseTest {

    @Test
    void testJwtResponseConstructorAndGetters() {
        // Given
        String token = "test.jwt.token";
        Long id = 1L;
        String username = "testUser";
        String firstName = "John";
        String lastName = "Doe";
        Boolean admin = true;

        // When
        JwtResponse jwtResponse = new JwtResponse(token, id, username, firstName, lastName, admin);

        // Then
        assertEquals(token, jwtResponse.getToken());
        assertEquals("Bearer", jwtResponse.getType());
        assertEquals(id, jwtResponse.getId());
        assertEquals(username, jwtResponse.getUsername());
        assertEquals(firstName, jwtResponse.getFirstName());
        assertEquals(lastName, jwtResponse.getLastName());
        assertEquals(admin, jwtResponse.getAdmin());
    }

    @Test
    void testJwtResponseSetters() {
        // Given
        JwtResponse jwtResponse = new JwtResponse("initial.token", 1L, "initial", "initialFirst", "initialLast", false);

        // When
        String newToken = "new.jwt.token";
        String newType = "NewType";
        Long newId = 2L;
        String newUsername = "newUser";
        String newFirstName = "Jane";
        String newLastName = "Smith";
        Boolean newAdmin = true;

        jwtResponse.setToken(newToken);
        jwtResponse.setType(newType);
        jwtResponse.setId(newId);
        jwtResponse.setUsername(newUsername);
        jwtResponse.setFirstName(newFirstName);
        jwtResponse.setLastName(newLastName);
        jwtResponse.setAdmin(newAdmin);

        // Then
        assertEquals(newToken, jwtResponse.getToken());
        assertEquals(newType, jwtResponse.getType());
        assertEquals(newId, jwtResponse.getId());
        assertEquals(newUsername, jwtResponse.getUsername());
        assertEquals(newFirstName, jwtResponse.getFirstName());
        assertEquals(newLastName, jwtResponse.getLastName());
        assertEquals(newAdmin, jwtResponse.getAdmin());
    }
}
