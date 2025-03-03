package com.openclassrooms.starterjwt.security.jwt;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AuthTokenFilterTest {

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
       
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldAuthenticate() throws Exception {
        // Given
        String token = "valid.jwt.token";
        String bearerToken = "Bearer " + token;
        String username = "testuser";

        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(userDetailsService).loadUserByUsername(username);
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldContinueChain() throws Exception {
        // Given
        String token = "invalid.jwt.token";
        String bearerToken = "Bearer " + token;

        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtils.validateJwtToken(token)).thenReturn(false);

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void doFilterInternal_WithNoToken_ShouldContinueChain() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtils, never()).validateJwtToken(anyString());
    }

    @Test
    void doFilterInternal_WithNonBearerToken_ShouldContinueChain() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Basic sometoken");

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtils, never()).validateJwtToken(anyString());
    }

    @Test
    void doFilterInternal_WithException_ShouldContinueChain() throws Exception {
        // Given
        String token = "valid.jwt.token";
        String bearerToken = "Bearer " + token;

        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtils.validateJwtToken(token)).thenThrow(new RuntimeException("Token validation failed"));

        // When
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
    }
}
