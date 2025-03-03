package com.openclassrooms.starterjwt.models;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void testUserCreation() {
        User user = new User();
        assertNotNull(user);
    }

    @Test
    void testUserBuilder() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, user.getId());
        assertEquals("test@test.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("password123", user.getPassword());
        assertFalse(user.isAdmin());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    void testUserSettersAndGetters() {
        User user = new User();
        LocalDateTime now = LocalDateTime.now();

        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password123");
        user.setAdmin(true);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        assertEquals(1L, user.getId());
        assertEquals("test@test.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("password123", user.getPassword());
        assertTrue(user.isAdmin());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = User.builder()
                .id(1L)
                .email("test@test.com")
                .lastName("Doe")
                .firstName("John")
                .password("password")
                .admin(false)
                .build();

        User user2 = User.builder()
                .id(1L)
                .email("test@test.com")
                .lastName("Doe")
                .firstName("John")
                .password("password")
                .admin(false)
                .build();

        User user3 = User.builder()
                .id(2L)
                .email("test2@test.com")
                .lastName("Doe")
                .firstName("John")
                .password("password")
                .admin(false)
                .build();

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }


    @Test
    void testRequiredArgsConstructor() {
        User user = new User("test@test.com", "Doe", "John", "password123", false);
        
        assertEquals("test@test.com", user.getEmail());
        assertEquals("Doe", user.getLastName());
        assertEquals("John", user.getFirstName());
        assertEquals("password123", user.getPassword());
        assertFalse(user.isAdmin());
    }

    @Test
    void testToString() {
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        String userString = user.toString();
        
        assertTrue(userString.contains("id=1"));
        assertTrue(userString.contains("email=test@test.com"));
        assertTrue(userString.contains("firstName=John"));
        assertTrue(userString.contains("lastName=Doe"));
        assertTrue(userString.contains("password=password123"));
    }

}
