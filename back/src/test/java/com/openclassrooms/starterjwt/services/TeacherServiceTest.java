package com.openclassrooms.starterjwt.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;

class TeacherServiceTest {

    @InjectMocks
    private TeacherService teacherService;

    @Mock
    private TeacherRepository teacherRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        Teacher teacher1 = new Teacher();
        Teacher teacher2 = new Teacher();
        List<Teacher> expectedTeachers = Arrays.asList(teacher1, teacher2);
        when(teacherRepository.findAll()).thenReturn(expectedTeachers);

        List<Teacher> result = teacherService.findAll();

        assertEquals(2, result.size());
        verify(teacherRepository).findAll();
    }

    @Test
    void findById() {
        Long teacherId = 1L;
        Teacher expectedTeacher = new Teacher();
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(expectedTeacher));

        Teacher result = teacherService.findById(teacherId);

        assertNotNull(result);
        verify(teacherRepository).findById(teacherId);
    }

    @Test
    void findById_ReturnsNull_WhenTeacherNotFound() {
        Long teacherId = 1L;
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

        Teacher result = teacherService.findById(teacherId);

        assertNull(result);
        verify(teacherRepository).findById(teacherId);
    }
}
