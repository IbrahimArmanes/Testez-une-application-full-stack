package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void delete() {
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        userService.delete(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void findById() {
        Long userId = 1L;
        User expectedUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User result = userService.findById(userId);

        assertNotNull(result);
        verify(userRepository).findById(userId);
    }

    @Test
    void findById_ReturnsNull_WhenUserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        User result = userService.findById(userId);

        assertNull(result);
        verify(userRepository).findById(userId);
    }
}
