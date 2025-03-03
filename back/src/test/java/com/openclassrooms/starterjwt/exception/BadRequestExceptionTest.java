package com.openclassrooms.starterjwt.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

class BadRequestExceptionTest {

    @Test
    void shouldHaveCorrectHttpStatus() {
        // Given
        ResponseStatus responseStatus = BadRequestException.class.getAnnotation(ResponseStatus.class);

        // Then
        assertNotNull(responseStatus);
        assertEquals(HttpStatus.BAD_REQUEST, responseStatus.value());
    }

    @Test
    void shouldExtendRuntimeException() {
        // Given
        BadRequestException exception = new BadRequestException();

        // Then
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void shouldBeAbleToThrowException() {
        // Then
        assertThrows(BadRequestException.class, () -> {
            throw new BadRequestException();
        });
    }
}
