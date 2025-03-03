package com.openclassrooms.starterjwt.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;
import com.openclassrooms.starterjwt.services.TeacherService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class SessionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionMapper sessionMapper;
    
    @Autowired
    private TeacherRepository teacherRepository;

    private Session testSession;
    private Teacher testTeacher;
    private User testUser;

    @BeforeEach
    public void setup() {
        // Clear existing data
        sessionRepository.deleteAll();
    
        // Create a teacher
        testTeacher = new Teacher();
        testTeacher.setFirstName("Test");
        testTeacher.setLastName("Teacher");
        
        // Fix the teacher creation logic
        List<Teacher> teachers = teacherService.findAll();
        if (!teachers.isEmpty()) {
            // Use an existing teacher if available
            testTeacher = teachers.get(0);
        } else {
            // Otherwise, we need a different approach since we don't have direct repository access
            // We may need to add a TeacherRepository to the test
            testTeacher = createTeacher(testTeacher);
        }
    
        // Create a user
        testUser = User.builder()
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .admin(false)
                .build();
        testUser = userRepository.save(testUser);
    
        // Create a session
        testSession = Session.builder()
                .name("Test Session")
                .date(new Date())
                .description("Test Description")
                .teacher(testTeacher)
                .users(new ArrayList<>())
                .build();
        testSession = sessionRepository.save(testSession);
    }

    // Then in your createTeacher method:
    private Teacher createTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @Test
    @WithMockUser
    public void testGetAllSessions() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/session")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(testSession.getName()))
                .andExpect(jsonPath("$[0].description").value(testSession.getDescription()));
    }

    @Test
    @WithMockUser
    public void testGetSessionById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/session/{id}", testSession.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testSession.getName()))
                .andExpect(jsonPath("$.description").value(testSession.getDescription()));
    }

    @Test
    @WithMockUser
    public void testGetSessionByInvalidId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/session/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testGetSessionByNonNumericId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/session/abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void testCreateSession() throws Exception {
        SessionDto newSessionDto = new SessionDto();
        newSessionDto.setName("New Test Session");
        newSessionDto.setDate(new Date());
        newSessionDto.setDescription("New Test Description");
        newSessionDto.setTeacher_id(testTeacher.getId());
        newSessionDto.setUsers(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newSessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newSessionDto.getName()))
                .andExpect(jsonPath("$.description").value(newSessionDto.getDescription()));
    }

    @Test
    @WithMockUser
    public void testUpdateSession() throws Exception {
        SessionDto updatedSessionDto = sessionMapper.toDto(testSession);
        updatedSessionDto.setName("Updated Session Name");
        updatedSessionDto.setDescription("Updated Description");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/session/{id}", testSession.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedSessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedSessionDto.getName()))
                .andExpect(jsonPath("$.description").value(updatedSessionDto.getDescription()));
    }

    @Test
    @WithMockUser
    public void testUpdateSessionWithInvalidId() throws Exception {
        SessionDto updatedSessionDto = sessionMapper.toDto(testSession);
        updatedSessionDto.setName("Updated Session Name");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/session/abc", testSession.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedSessionDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void testDeleteSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/{id}", testSession.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/session/{id}", testSession.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testDeleteSessionWithInvalidId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void testParticipateInSession() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/session/{id}/participate/{userId}", 
                testSession.getId(), testUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify the user is now in the session's users list
        Session updatedSession = sessionService.getById(testSession.getId());
        assert(updatedSession.getUsers().stream()
                .anyMatch(user -> user.getId().equals(testUser.getId())));
    }

    @Test
    @WithMockUser
    public void testParticipateWithInvalidIds() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/session/abc/participate/{userId}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/session/{id}/participate/abc", testSession.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void testNoLongerParticipate() throws Exception {
        // First make the user participate
        sessionService.participate(testSession.getId(), testUser.getId());
        
        // Then remove the participation
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/{id}/participate/{userId}", 
                testSession.getId(), testUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify the user is no longer in the session's users list
        Session updatedSession = sessionService.getById(testSession.getId());
        assert(updatedSession.getUsers().stream()
                .noneMatch(user -> user.getId().equals(testUser.getId())));
    }

    @Test
    @WithMockUser
    public void testNoLongerParticipateWithInvalidIds() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/abc/participate/{userId}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/{id}/participate/abc", testSession.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
