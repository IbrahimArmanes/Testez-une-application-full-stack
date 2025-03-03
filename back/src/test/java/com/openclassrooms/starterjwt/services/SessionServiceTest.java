package com.openclassrooms.starterjwt.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

class SessionServiceTest {

    @InjectMocks
    private SessionService sessionService;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSession() {
        Session session = new Session();
        when(sessionRepository.save(any(Session.class))).thenReturn(session);
        
        Session result = sessionService.create(session);
        
        assertNotNull(result);
        verify(sessionRepository).save(session);
    }

    @Test
    void deleteSession() {
        Long id = 1L;
        doNothing().when(sessionRepository).deleteById(id);
        
        sessionService.delete(id);
        
        verify(sessionRepository).deleteById(id);
    }

    @Test
    void findAllSessions() {
        List<Session> sessions = Arrays.asList(new Session(), new Session());
        when(sessionRepository.findAll()).thenReturn(sessions);
        
        List<Session> result = sessionService.findAll();
        
        assertEquals(2, result.size());
        verify(sessionRepository).findAll();
    }

    @Test
    void getSessionById() {
        Long id = 1L;
        Session session = new Session();
        when(sessionRepository.findById(id)).thenReturn(Optional.of(session));
        
        Session result = sessionService.getById(id);
        
        assertNotNull(result);
        verify(sessionRepository).findById(id);
    }

    @Test
    void updateSession() {
        Long id = 1L;
        Session session = new Session();
        when(sessionRepository.save(any(Session.class))).thenReturn(session);
        
        Session result = sessionService.update(id, session);
        
        assertNotNull(result);
        verify(sessionRepository).save(session);
    }

    @Test
    void participate_Success() {
        Long sessionId = 1L;
        Long userId = 1L;
        Session session = new Session();
        session.setUsers(new ArrayList<>());
        User user = new User();
        user.setId(userId);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        sessionService.participate(sessionId, userId);

        verify(sessionRepository).save(session);
    }

    @Test
    void participate_ThrowsNotFoundException() {
        Long sessionId = 1L;
        Long userId = 1L;

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(sessionId, userId));
    }

    @Test
    void participate_ThrowsBadRequestException() {
        Long sessionId = 1L;
        Long userId = 1L;
        Session session = new Session();
        User user = new User();
        user.setId(userId);
        session.setUsers(new ArrayList<>(Arrays.asList(user)));

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> sessionService.participate(sessionId, userId));
    }

    @Test
    void noLongerParticipate_Success() {
        Long sessionId = 1L;
        Long userId = 1L;
        Session session = new Session();
        User user = new User();
        user.setId(userId);
        session.setUsers(new ArrayList<>(Arrays.asList(user)));

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        sessionService.noLongerParticipate(sessionId, userId);

        verify(sessionRepository).save(session);
    }

    @Test
    void noLongerParticipate_ThrowsNotFoundException() {
        Long sessionId = 1L;
        Long userId = 1L;

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(sessionId, userId));
    }

    @Test
    void noLongerParticipate_ThrowsBadRequestException() {
        Long sessionId = 1L;
        Long userId = 1L;
        Session session = new Session();
        session.setUsers(new ArrayList<>());

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(sessionId, userId));
    }
}
