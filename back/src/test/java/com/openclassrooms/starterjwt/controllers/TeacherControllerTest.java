package com.openclassrooms.starterjwt.controllers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;

@ExtendWith(MockitoExtension.class)
public class TeacherControllerTest {

    @Mock
    private TeacherMapper teacherMapper;
    @Mock
    private TeacherService teacherService;

    private TeacherController teacherController;
    private Teacher teacher;
    private TeacherDto teacherDto;

    @BeforeEach
    void setUp() {
        teacherController = new TeacherController(teacherService, teacherMapper);
        
        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        
        teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setFirstName("John");
        teacherDto.setLastName("Doe");
        teacherDto.setCreatedAt(LocalDateTime.now());
        teacherDto.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void findById_Success() {
        when(teacherService.findById(1L)).thenReturn(teacher);
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

        ResponseEntity<?> response = teacherController.findById("1");

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(teacherDto, response.getBody());
    }

    @Test
    void findById_NotFound() {
        when(teacherService.findById(1L)).thenReturn(null);

        ResponseEntity<?> response = teacherController.findById("1");

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void findById_BadRequest() {
        ResponseEntity<?> response = teacherController.findById("invalid");

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void findAll_Success() {
        List<Teacher> teachers = Arrays.asList(teacher);
        List<TeacherDto> teacherDtos = Arrays.asList(teacherDto);

        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

        ResponseEntity<?> response = teacherController.findAll();

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(teacherDtos, response.getBody());
        verify(teacherService).findAll();
        verify(teacherMapper).toDto(teachers);
    }
}
