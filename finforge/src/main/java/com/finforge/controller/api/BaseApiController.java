package com.finforge.controller.api;

import com.finforge.model.User;
import com.finforge.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all REST API controllers in FinForge backend.
 */
public abstract class BaseApiController {

    /**
     * Resolves the current user ID.
     * Looks first at the 'X-User-Id' header, then the HTTP session, and defaults to 1 for development/demo.
     */
    protected int resolveUserId(HttpServletRequest request) {
        String headerUserId = request.getHeader("X-User-Id");
        if (headerUserId != null && !headerUserId.trim().isEmpty()) {
            try {
                return Integer.parseInt(headerUserId.trim());
            } catch (NumberFormatException ignored) {}
        }

        int sessionUserId = SessionUtil.getLoggedInUserId(request);
        if (sessionUserId > 0) {
            return sessionUserId;
        }

        // Fallback default demo user ID for smooth API development
        return 1;
    }

    protected ResponseEntity<Map<String, Object>> errorResponse(HttpStatus status, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        error.put("status", status.value());
        return ResponseEntity.status(status).body(error);
    }

    protected ResponseEntity<Map<String, Object>> successResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return ResponseEntity.ok(response);
    }
}
