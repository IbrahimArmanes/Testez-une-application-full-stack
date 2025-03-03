package com.openclassrooms.starterjwt.controllers;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;

@ExtendWith(MockitoExtension.class)
public class SessionControllerTest {

    @Mock
    private SessionMapper sessionMapper;
    @Mock
    private SessionService sessionService;

    private SessionController sessionController;
    private Session session;
    private SessionDto sessionDto;

    @BeforeEach
    void setUp() {
        sessionController = new SessionController(sessionService, sessionMapper);
        
        session = new Session();
        session.setId(1L);
        session.setName("Yoga Session");
        session.setDate(new Date());
        
        sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("Yoga Session");
        sessionDto.setDate(new Date());
    }

    @Test
    void findById_Success() {
        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.findById("1");

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(sessionDto, response.getBody());
    }

    @Test
    void findById_NotFound() {
        when(sessionService.getById(1L)).thenReturn(null);

        ResponseEntity<?> response = sessionController.findById("1");

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void findById_BadRequest() {
        ResponseEntity<?> response = sessionController.findById("invalid");

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void findAll_Success() {
        List<Session> sessions = Arrays.asList(session);
        List<SessionDto> sessionDtos = Arrays.asList(sessionDto);

        when(sessionService.findAll()).thenReturn(sessions);
        when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos);

        ResponseEntity<?> response = sessionController.findAll();

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(sessionDtos, response.getBody());
    }

    @Test
    void create_Success() {
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.create(session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.create(sessionDto);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(sessionDto, response.getBody());
    }

    @Test
    void update_Success() {
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.update(eq(1L), any(Session.class))).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.update("1", sessionDto);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(sessionDto, response.getBody());
    }

    @Test
    void delete_Success() {
        when(sessionService.getById(1L)).thenReturn(session);

        ResponseEntity<?> response = sessionController.save("1");

        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(sessionService).delete(1L);
    }

    @Test
    void delete_NotFound() {
        when(sessionService.getById(1L)).thenReturn(null);

        ResponseEntity<?> response = sessionController.save("1");

        assertTrue(response.getStatusCode().is4xxClientError());
        verify(sessionService, never()).delete(anyLong());
    }

    @Test
    void participate_Success() {
        ResponseEntity<?> response = sessionController.participate("1", "1");

        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(sessionService).participate(1L, 1L);
    }

    @Test
    void noLongerParticipate_Success() {
        ResponseEntity<?> response = sessionController.noLongerParticipate("1", "1");

        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(sessionService).noLongerParticipate(1L, 1L);
    }
}
