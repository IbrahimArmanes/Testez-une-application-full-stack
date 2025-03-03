package com.openclassrooms.starterjwt.controllers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private User testUser;
    private User anotherUser;
    private String testUserToken;
    private String anotherUserToken;

    @BeforeEach
    public void setup() {
        // Clean the repository
        userRepository.deleteAll();

        // Create test users
        testUser = User.builder()
                .email("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password(passwordEncoder.encode("password"))
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        anotherUser = User.builder()
                .email("another@test.com")
                .firstName("Another")
                .lastName("User")
                .password(passwordEncoder.encode("password"))
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Save users to repository
        testUser = userRepository.save(testUser);
        anotherUser = userRepository.save(anotherUser);

        // Create authentication and token for test user
        UserDetailsImpl testUserDetails = UserDetailsImpl.builder()
                .id(testUser.getId())
                .username(testUser.getEmail())
                .firstName(testUser.getFirstName())
                .lastName(testUser.getLastName())
                .password(testUser.getPassword())
                .admin(testUser.isAdmin())
                .build();

        Authentication testAuth = new UsernamePasswordAuthenticationToken(
                testUserDetails, null, testUserDetails.getAuthorities());
        testUserToken = jwtUtils.generateJwtToken(testAuth);

        // Create authentication and token for another user
        UserDetailsImpl anotherUserDetails = UserDetailsImpl.builder()
                .id(anotherUser.getId())
                .username(anotherUser.getEmail())
                .firstName(anotherUser.getFirstName())
                .lastName(anotherUser.getLastName())
                .password(anotherUser.getPassword())
                .admin(anotherUser.isAdmin())
                .build();

        Authentication anotherAuth = new UsernamePasswordAuthenticationToken(
                anotherUserDetails, null, anotherUserDetails.getAuthorities());
        anotherUserToken = jwtUtils.generateJwtToken(anotherAuth);
    }

    @Test
    public void testFindUserById_Success() throws Exception {
        // Test finding the test user
        mockMvc.perform(get("/api/user/" + testUser.getId())
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testUser.getLastName()));
    }

    @Test
    public void testFindUserById_NotFound() throws Exception {
        // Test finding a non-existent user
        mockMvc.perform(get("/api/user/999")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindUserById_BadRequest() throws Exception {
        // Test with invalid ID format
        mockMvc.perform(get("/api/user/invalid-id")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteUser_Success() throws Exception {
        // Save the ID before deletion
        Long userId = testUser.getId();
        
        // Test deleting the authenticated user
        mockMvc.perform(delete("/api/user/" + userId)
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk());

        // Verify user is deleted directly in the repository
        Optional<User> deletedUser = userRepository.findById(userId);
        assertFalse(deletedUser.isPresent(), "User should be deleted from the database");
    }

    @Test
    public void testDeleteUser_Unauthorized() throws Exception {
        // Test deleting another user (not the authenticated user)
        mockMvc.perform(delete("/api/user/" + anotherUser.getId())
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isUnauthorized());

        // Verify user is not deleted using another user's token
        mockMvc.perform(get("/api/user/" + anotherUser.getId())
                .header("Authorization", "Bearer " + anotherUserToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteUser_NotFound() throws Exception {
        // Test deleting a non-existent user
        mockMvc.perform(delete("/api/user/999")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteUser_BadRequest() throws Exception {
        // Test with invalid ID format
        mockMvc.perform(delete("/api/user/invalid-id")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isBadRequest());
    }
}
