package com.openclassrooms.starterjwt.security.services;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

public class UserDetailsImplTest {

    @Test
    void testUserDetailsBuilder() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("testUser")
                .firstName("John")
                .lastName("Doe")
                .admin(false)
                .password("password123")
                .build();

        assertEquals(1L, userDetails.getId());
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("John", userDetails.getFirstName());
        assertEquals("Doe", userDetails.getLastName());
        assertEquals(false, userDetails.getAdmin());
        assertEquals("password123", userDetails.getPassword());
    }

    @Test
    void testGetAuthorities() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("testUser")
                .build();

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertTrue(authorities.isEmpty());
    }

    @Test
    void testAccountMethods() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("testUser")
                .build();

        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void testEquals() {
        UserDetailsImpl user1 = UserDetailsImpl.builder()
                .id(1L)
                .username("user1")
                .build();

        UserDetailsImpl user2 = UserDetailsImpl.builder()
                .id(1L)
                .username("user2")
                .build();

        UserDetailsImpl user3 = UserDetailsImpl.builder()
                .id(2L)
                .username("user1")
                .build();

        assertTrue(user1.equals(user1)); // Same object
        assertTrue(user1.equals(user2)); // Same id
        assertFalse(user1.equals(user3)); // Different id
        assertFalse(user1.equals(null)); // Null comparison
        assertFalse(user1.equals(new Object())); // Different class
    }

    @Test
    void testAllArgsConstructor() {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L, 
                "testUser", 
                "John", 
                "Doe", 
                false, 
                "password123"
        );

        assertEquals(1L, userDetails.getId());
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("John", userDetails.getFirstName());
        assertEquals("Doe", userDetails.getLastName());
        assertEquals(false, userDetails.getAdmin());
        assertEquals("password123", userDetails.getPassword());
    }
}
