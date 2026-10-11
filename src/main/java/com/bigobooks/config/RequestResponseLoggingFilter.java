package com.bigobooks.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    // public static final String TRACE_HEADER = "X-Request-ID";
    public static final String TRACE_KEY = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // String traceId = request.getHeader(TRACE_HEADER);
        String traceId = UUID.randomUUID().toString();
        // if (traceId == null || traceId.isBlank()) {
        // traceId = UUID.randomUUID().toString();
        // }
        MDC.put(TRACE_KEY, traceId);
        // response.setHeader(TRACE_HEADER, traceId);
        try {
            chain.doFilter(request, response);
        } finally {
            String query = request.getQueryString();
            log.info("{} {}{} -> {}", request.getMethod(), request.getRequestURI(),
                    query == null ? "" : "?" + query, response.getStatus());
            MDC.remove(TRACE_KEY);
        }
    }
}
