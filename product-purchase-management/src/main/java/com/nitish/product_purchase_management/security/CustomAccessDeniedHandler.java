package com.nitish.product_purchase_management.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class CustomAccessDeniedHandler
        implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(
                HttpServletResponse.SC_FORBIDDEN);

        response.setContentType(
                "application/json");

        String responseBody = """
                {
                    "success": false,
                    "code": "ACCESS_DENIED",
                    "message": "You do not have permission to access this resource.",
                    "path": "%s",
                    "timestamp": "%s"
                }
                """.formatted(
                request.getRequestURI(),
                Instant.now());

        response.getWriter().write(responseBody);
    }
}