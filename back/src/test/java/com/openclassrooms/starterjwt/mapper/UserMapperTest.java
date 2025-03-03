package com.openclassrooms.starterjwt.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;

@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void shouldMapUserToUserDto() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setLastName("Doe");
        user.setFirstName("John");
        user.setPassword("password123");
        user.setAdmin(false);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);

        // When
        UserDto userDto = userMapper.toDto(user);

        // Then
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getPassword(), userDto.getPassword());
        assertEquals(user.isAdmin(), userDto.isAdmin());
        assertEquals(user.getCreatedAt(), userDto.getCreatedAt());
    }

    @Test
    void shouldMapUserDtoToUser() {
        // Given
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@test.com");
        userDto.setLastName("Doe");
        userDto.setFirstName("John");
        userDto.setPassword("password123");
        userDto.setAdmin(false);
        LocalDateTime now = LocalDateTime.now();
        userDto.setCreatedAt(now);

        // When
        User user = userMapper.toEntity(userDto);

        // Then
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getLastName(), user.getLastName());
        assertEquals(userDto.getFirstName(), user.getFirstName());
        assertEquals(userDto.getPassword(), user.getPassword());
        assertEquals(userDto.isAdmin(), user.isAdmin());
        assertEquals(userDto.getCreatedAt(), user.getCreatedAt());
    }

    @Test
    void shouldMapUserListToUserDtoList() {
        // Given
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFirstName("John"); // Added required firstName
        user.setLastName("Doe"); // Added required lastName
        user.setPassword("password123"); // Added required password
        user.setAdmin(false); // Added required admin status
        users.add(user);
    
        // When
        List<UserDto> userDtos = userMapper.toDto(users);
    
        // Then
        assertEquals(users.size(), userDtos.size());
        assertEquals(users.get(0).getId(), userDtos.get(0).getId());
        assertEquals(users.get(0).getEmail(), userDtos.get(0).getEmail());
        assertEquals(users.get(0).getFirstName(), userDtos.get(0).getFirstName());
        assertEquals(users.get(0).getLastName(), userDtos.get(0).getLastName());
    }

    @Test
    void shouldHandleNullUser() {
        // When
        UserDto userDto = userMapper.toDto((User) null);

        // Then
        assertNull(userDto);
    }
      @Test
      void shouldHandleNullUserDto() {
          // When
          User user = userMapper.toEntity((UserDto) null);

          // Then
          assertNull(user);
      }
    }

