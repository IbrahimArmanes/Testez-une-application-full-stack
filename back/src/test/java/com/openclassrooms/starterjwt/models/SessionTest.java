package com.openclassrooms.starterjwt.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void testSessionBuilder() {
        // Given
        Teacher teacher = new Teacher();
        Date sessionDate = new Date();
        LocalDateTime now = LocalDateTime.now();
        
        // When
        Session session = Session.builder()
                .id(1L)
                .name("Yoga Session")
                .date(sessionDate)
                .description("Beginner friendly yoga session")
                .teacher(teacher)
                .users(new ArrayList<>())
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Then
        assertEquals(1L, session.getId());
        assertEquals("Yoga Session", session.getName());
        assertEquals(sessionDate, session.getDate());
        assertEquals("Beginner friendly yoga session", session.getDescription());
        assertEquals(teacher, session.getTeacher());
        assertNotNull(session.getUsers());
        assertEquals(now, session.getCreatedAt());
        assertEquals(now, session.getUpdatedAt());
    }

    @Test
    void testSessionNoArgsConstructor() {
        // When
        Session session = new Session();

        // Then
        assertNotNull(session);
        assertNull(session.getId());
        assertNull(session.getName());
        assertNull(session.getDate());
        assertNull(session.getDescription());
        assertNull(session.getTeacher());
        assertNull(session.getUsers());
    }

    @Test
    void testSessionSettersAndGetters() {
        // Given
        Session session = new Session();
        Teacher teacher = new Teacher();
        Date sessionDate = new Date();
        LocalDateTime now = LocalDateTime.now();

        // When
        session.setId(1L);
        session.setName("Yoga Session");
        session.setDate(sessionDate);
        session.setDescription("Beginner friendly yoga session");
        session.setTeacher(teacher);
        session.setUsers(new ArrayList<>());
        session.setCreatedAt(now);
        session.setUpdatedAt(now);

        // Then
        assertEquals(1L, session.getId());
        assertEquals("Yoga Session", session.getName());
        assertEquals(sessionDate, session.getDate());
        assertEquals("Beginner friendly yoga session", session.getDescription());
        assertEquals(teacher, session.getTeacher());
        assertNotNull(session.getUsers());
        assertEquals(now, session.getCreatedAt());
        assertEquals(now, session.getUpdatedAt());
    }

    @Test
    void testEquals() {
        // Given
        Session session1 = Session.builder().id(1L).build();
        Session session2 = Session.builder().id(1L).build();
        Session session3 = Session.builder().id(2L).build();

        // Then
        assertEquals(session1, session2);
        assertNotEquals(session1, session3);
    }

    @Test
    void testHashCode() {
        // Given
        Session session1 = Session.builder().id(1L).build();
        Session session2 = Session.builder().id(1L).build();

        // Then
        assertEquals(session1.hashCode(), session2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        Session session = Session.builder()
                .id(1L)
                .name("Yoga Session")
                .build();

        // When
        String toString = session.toString();

        // Then
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=Yoga Session"));
    }
}
