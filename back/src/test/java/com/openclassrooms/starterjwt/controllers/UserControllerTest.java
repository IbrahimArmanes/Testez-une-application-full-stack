package com.openclassrooms.starterjwt.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private UserService userService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private UserDetails userDetails;

    private UserController userController;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService, userMapper);
        
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@test.com");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void findById_Success() {
        when(userService.findById(1L)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        ResponseEntity<?> response = userController.findById("1");

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(userDto, response.getBody());
    }

    @Test
    void findById_NotFound() {
        when(userService.findById(1L)).thenReturn(null);

        ResponseEntity<?> response = userController.findById("1");

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void findById_BadRequest() {
        ResponseEntity<?> response = userController.findById("invalid");

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void delete_Success() {
        when(userService.findById(1L)).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@test.com");

        ResponseEntity<?> response = userController.save("1");

        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(userService).delete(1L);
    }

    @Test
    void delete_Unauthorized() {
        when(userService.findById(1L)).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("different@test.com");

        ResponseEntity<?> response = userController.save("1");

        assertTrue(response.getStatusCode().is4xxClientError());
        verify(userService, never()).delete(anyLong());
    }

    @Test
    void delete_NotFound() {
        when(userService.findById(1L)).thenReturn(null);

        ResponseEntity<?> response = userController.save("1");

        assertTrue(response.getStatusCode().is4xxClientError());
        verify(userService, never()).delete(anyLong());
    }

    @Test
    void delete_BadRequest() {
        ResponseEntity<?> response = userController.save("invalid");

        assertTrue(response.getStatusCode().is4xxClientError());
        verify(userService, never()).delete(anyLong());
    }
}
