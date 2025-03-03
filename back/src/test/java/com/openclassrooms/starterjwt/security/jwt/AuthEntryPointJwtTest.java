package com.openclassrooms.starterjwt.security.jwt;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class AuthEntryPointJwtTest {

    @InjectMocks
    private AuthEntryPointJwt authEntryPointJwt;

    @Mock
    private AuthenticationException authException;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCommence() throws IOException, ServletException {
        // Given
        String errorMessage = "Invalid credentials";
        String servletPath = "/api/test";
        request.setServletPath(servletPath);
        when(authException.getMessage()).thenReturn(errorMessage);

        // When
        authEntryPointJwt.commence(request, response, authException);

        // Then
        assertEquals("application/json", response.getContentType());
        assertEquals(401, response.getStatus());

        String responseContent = response.getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(responseContent, Map.class);

        assertEquals(401, responseMap.get("status"));
        assertEquals("Unauthorized", responseMap.get("error"));
        assertEquals(errorMessage, responseMap.get("message"));
        assertEquals(servletPath, responseMap.get("path"));
    }

    @Test
    void testCommenceWithNullMessage() throws IOException, ServletException {
        // Given
        when(authException.getMessage()).thenReturn(null);
        request.setServletPath("/api/test");

        // When
        authEntryPointJwt.commence(request, response, authException);

        // Then
        assertEquals("application/json", response.getContentType());
        assertEquals(401, response.getStatus());

        String responseContent = response.getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(responseContent, Map.class);

        assertEquals(401, responseMap.get("status"));
        assertEquals("Unauthorized", responseMap.get("error"));
        assertNull(responseMap.get("message"));
    }
}
