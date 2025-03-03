package com.openclassrooms.starterjwt.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class NotFoundExceptionTest {

    @Test
    void shouldHaveCorrectHttpStatus() {
        // Given
        ResponseStatus responseStatus = NotFoundException.class.getAnnotation(ResponseStatus.class);

        // Then
        assertNotNull(responseStatus);
        assertEquals(HttpStatus.NOT_FOUND, responseStatus.value());
    }

    @Test
    void shouldCreateExceptionWithoutParameters() {
        // When
        NotFoundException exception = new NotFoundException();

        // Then
        assertNotNull(exception);
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void shouldThrowNotFoundException() {
        // Then
        assertThrows(NotFoundException.class, () -> {
            throw new NotFoundException();
        });
    }
}
