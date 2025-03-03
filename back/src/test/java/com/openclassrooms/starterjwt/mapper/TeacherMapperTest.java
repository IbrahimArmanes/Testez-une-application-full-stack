package com.openclassrooms.starterjwt.mapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;

@SpringBootTest
public class TeacherMapperTest {

    @Autowired
    private TeacherMapper teacherMapper;

    @Test
    void shouldMapTeacherToTeacherDto() {
        // Given
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setLastName("Doe");
        teacher.setFirstName("John");
        LocalDateTime now = LocalDateTime.now();
        teacher.setCreatedAt(now);

        // When
        TeacherDto teacherDto = teacherMapper.toDto(teacher);

        // Then
        assertNotNull(teacherDto);
        assertEquals(teacher.getId(), teacherDto.getId());
        assertEquals(teacher.getLastName(), teacherDto.getLastName());
        assertEquals(teacher.getFirstName(), teacherDto.getFirstName());
        assertEquals(teacher.getCreatedAt(), teacherDto.getCreatedAt());
    }

    @Test
    void shouldMapTeacherDtoToTeacher() {
        // Given
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setLastName("Doe");
        teacherDto.setFirstName("John");
        LocalDateTime now = LocalDateTime.now();
        teacherDto.setCreatedAt(now);

        // When
        Teacher teacher = teacherMapper.toEntity(teacherDto);

        // Then
        assertNotNull(teacher);
        assertEquals(teacherDto.getId(), teacher.getId());
        assertEquals(teacherDto.getLastName(), teacher.getLastName());
        assertEquals(teacherDto.getFirstName(), teacher.getFirstName());
        assertEquals(teacherDto.getCreatedAt(), teacher.getCreatedAt());
    }

    @Test
    void shouldHandleNullTeacher() {
        // When
        TeacherDto teacherDto = teacherMapper.toDto((Teacher) null);

        // Then
        assertNull(teacherDto);
    }

    @Test
    void shouldHandleNullTeacherDto() {
        // When
        Teacher teacher = teacherMapper.toEntity((TeacherDto) null);

        // Then
        assertNull(teacher);
    }
}
