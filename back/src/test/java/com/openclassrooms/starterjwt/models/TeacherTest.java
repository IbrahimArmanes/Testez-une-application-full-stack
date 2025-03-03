package com.openclassrooms.starterjwt.models;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class TeacherTest {

    @Test
    void testTeacherBuilder() {
        LocalDateTime now = LocalDateTime.now();
        Teacher teacher = Teacher.builder()
                .id(1L)
                .lastName("Smith")
                .firstName("John")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, teacher.getId());
        assertEquals("Smith", teacher.getLastName());
        assertEquals("John", teacher.getFirstName());
        assertEquals(now, teacher.getCreatedAt());
        assertEquals(now, teacher.getUpdatedAt());
    }

    @Test
    void testTeacherSettersAndGetters() {
        Teacher teacher = new Teacher();
        LocalDateTime now = LocalDateTime.now();

        teacher.setId(1L);
        teacher.setLastName("Smith");
        teacher.setFirstName("John");
        teacher.setCreatedAt(now);
        teacher.setUpdatedAt(now);

        assertEquals(1L, teacher.getId());
        assertEquals("Smith", teacher.getLastName());
        assertEquals("John", teacher.getFirstName());
        assertEquals(now, teacher.getCreatedAt());
        assertEquals(now, teacher.getUpdatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        Teacher teacher1 = Teacher.builder().id(1L).lastName("Smith").firstName("John").build();
        Teacher teacher2 = Teacher.builder().id(1L).lastName("Doe").firstName("Jane").build();
        Teacher teacher3 = Teacher.builder().id(2L).lastName("Smith").firstName("John").build();

        assertEquals(teacher1, teacher2); // Should be equal because same ID
        assertNotEquals(teacher1, teacher3); // Should not be equal because different ID
        assertEquals(teacher1.hashCode(), teacher2.hashCode());
        assertNotEquals(teacher1.hashCode(), teacher3.hashCode());
    }

    @Test
    void testToString() {
        Teacher teacher = Teacher.builder()
                .id(1L)
                .lastName("Smith")
                .firstName("John")
                .build();

        String toString = teacher.toString();
        
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("lastName=Smith"));
        assertTrue(toString.contains("firstName=John"));
    }

    @Test
    void testChainedSetters() {
        Teacher teacher = new Teacher()
                .setId(1L)
                .setLastName("Smith")
                .setFirstName("John");

        assertEquals(1L, teacher.getId());
        assertEquals("Smith", teacher.getLastName());
        assertEquals("John", teacher.getFirstName());
    }
}
