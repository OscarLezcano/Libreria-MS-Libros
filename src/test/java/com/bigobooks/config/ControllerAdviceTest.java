package com.bigobooks.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

class ControllerAdviceTest {

    @Test
    void mapsAccessDeniedToForbidden() {
        var response = new ControllerAdvice().accessDeniedExceptionHandler(
                new AccessDeniedException("Access Denied"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
