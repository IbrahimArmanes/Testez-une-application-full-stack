package com.openclassrooms.starterjwt.mapper;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;

@ExtendWith(MockitoExtension.class)
public class SessionMapperTest {

    @Spy
    @InjectMocks
    private SessionMapperImpl sessionMapper;

    @Mock
    private TeacherService teacherService;

    @Mock
    private UserService userService;

    private Session session;
    private SessionDto sessionDto;
    private Teacher teacher;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // Initialize test data
        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setLastName("Smith");
        teacher.setFirstName("John");
        teacher.setCreatedAt(LocalDateTime.now());

        user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@test.com");
        user1.setFirstName("User");
        user1.setLastName("One");

        user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@test.com");
        user2.setFirstName("User");
        user2.setLastName("Two");

        session = new Session();
        session.setId(1L);
        session.setName("Yoga Session");
        session.setDescription("Test Description");
        session.setTeacher(teacher);
        session.setUsers(Arrays.asList(user1, user2));
        session.setCreatedAt(LocalDateTime.now());

        sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("Yoga Session");
        sessionDto.setDescription("Test Description");
        sessionDto.setTeacher_id(1L);
        sessionDto.setUsers(Arrays.asList(1L, 2L));
        sessionDto.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void toEntity_ShouldMapDtoToEntity() {
        // Given
        when(teacherService.findById(1L)).thenReturn(teacher);
        when(userService.findById(1L)).thenReturn(user1);
        when(userService.findById(2L)).thenReturn(user2);

        // When
        Session result = sessionMapper.toEntity(sessionDto);

        // Then
        assertNotNull(result);
        assertEquals(sessionDto.getId(), result.getId());
        assertEquals(sessionDto.getName(), result.getName());
        assertEquals(sessionDto.getDescription(), result.getDescription());
        assertEquals(teacher, result.getTeacher());
        assertEquals(2, result.getUsers().size());
        assertTrue(result.getUsers().contains(user1));
        assertTrue(result.getUsers().contains(user2));
    }

    @Test
    void toDto_ShouldMapEntityToDto() {
        // When
        SessionDto result = sessionMapper.toDto(session);

        // Then
        assertNotNull(result);
        assertEquals(session.getId(), result.getId());
        assertEquals(session.getName(), result.getName());
        assertEquals(session.getDescription(), result.getDescription());
        assertEquals(session.getTeacher().getId(), result.getTeacher_id());
        assertEquals(2, result.getUsers().size());
        assertTrue(result.getUsers().contains(1L));
        assertTrue(result.getUsers().contains(2L));
    }

    @Test
    void toEntity_WithNullTeacherAndUsers_ShouldMapCorrectly() {
        // Given
        sessionDto.setTeacher_id(null);
        sessionDto.setUsers(null);

        // When
        Session result = sessionMapper.toEntity(sessionDto);

        // Then
        assertNotNull(result);
        assertNull(result.getTeacher());
        assertTrue(result.getUsers().isEmpty());
    }

    @Test
    void toDto_WithNullTeacherAndUsers_ShouldMapCorrectly() {
        // Given
        session.setTeacher(null);
        session.setUsers(null);

        // When
        SessionDto result = sessionMapper.toDto(session);

        // Then
        assertNotNull(result);
        assertNull(result.getTeacher_id());
        assertTrue(result.getUsers().isEmpty());
    }
}
