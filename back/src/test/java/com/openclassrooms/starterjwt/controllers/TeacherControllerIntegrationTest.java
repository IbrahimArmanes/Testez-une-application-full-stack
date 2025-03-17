package com.openclassrooms.starterjwt.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

import io.jsonwebtoken.lang.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TeacherControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TeacherMapper teacherMapper;

    private String jwtToken;
    private Teacher testTeacher1;
    private Teacher testTeacher2;

    @BeforeEach
    public void setup() throws Exception {
        // Clean repositories
        teacherRepository.deleteAll();
        userRepository.deleteAll();

        // Create a test user for authentication
        User testUser = User.builder()
                .email("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password(passwordEncoder.encode("password"))
                .admin(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(testUser);

        // Login to get JWT token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("password");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JwtResponse jwtResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), JwtResponse.class);
        jwtToken = jwtResponse.getToken();

        // Create test teachers
        testTeacher1 = Teacher.builder()
                .firstName("John")
                .lastName("Doe")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testTeacher2 = Teacher.builder()
                .firstName("Jane")
                .lastName("Smith")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        teacherRepository.save(testTeacher1);
        teacherRepository.save(testTeacher2);
    }

    @Test
    public void testFindAllTeachers() throws Exception {
        mockMvc.perform(get("/api/teacher")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].lastName", is("Doe")))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[1].lastName", is("Smith")))
                .andExpect(jsonPath("$[1].firstName", is("Jane")));
    }

    @Test
    public void testFindTeacherById_Success() throws Exception {
        mockMvc.perform(get("/api/teacher/" + testTeacher1.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testTeacher1.getId().intValue())))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    public void testFindTeacherById_NotFound() throws Exception {
        // Test with a non-existent ID
        mockMvc.perform(get("/api/teacher/999")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindTeacherById_BadRequest() throws Exception {
        // Test with invalid ID format
        mockMvc.perform(get("/api/teacher/invalid")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testUnauthorizedAccess() throws Exception {
        // Test without auth token
        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isUnauthorized());
        
        mockMvc.perform(get("/api/teacher/" + testTeacher1.getId()))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    public void testMapperConversion() {
        // Test that mapper correctly converts between entity and DTO
        TeacherDto dto = teacherMapper.toDto(testTeacher1);
        
        assertNotNull(dto);
        assertEquals(testTeacher1.getId(), dto.getId());
        assertEquals(testTeacher1.getFirstName(), dto.getFirstName());
        assertEquals(testTeacher1.getLastName(), dto.getLastName());
        List<Teacher> teacherList = new ArrayList<>();
        teacherList.add(testTeacher1);
        teacherList.add(testTeacher2);
        List<TeacherDto> dtos = teacherMapper.toDto(teacherList);
        assertEquals(2, dtos.size());
    }
}
