package com.bigobooks.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

import com.bigobooks.shared.web.CommonExceptionHandler;

class ControllerAdviceTest {

    @Test
    void mapsAccessDeniedToForbidden() {
        var response = new CommonExceptionHandler().handleAccessDenied(
                new AccessDeniedException("Access Denied"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
