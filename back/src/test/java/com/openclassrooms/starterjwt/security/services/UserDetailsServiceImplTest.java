package com.openclassrooms.starterjwt.security.services;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void loadUserByUsername_WhenUserExists_ReturnsUserDetails() {
        // Arrange
        String email = "test@test.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        assertEquals(user.getId(), userDetailsImpl.getId());
        assertEquals(user.getFirstName(), userDetailsImpl.getFirstName());
        assertEquals(user.getLastName(), userDetailsImpl.getLastName());
        
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void loadUserByUsername_WhenUserNotFound_ThrowsException() {
        // Arrange
        String email = "nonexistent@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        String expectedMessage = "User Not Found with email: " + email;
        String actualMessage = exception.getMessage();
        
        assertTrue(actualMessage.contains(expectedMessage));
        verify(userRepository, times(1)).findByEmail(email);
    }
}
