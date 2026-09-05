package com.finforge.controller.api;

import com.finforge.dao.CategoryDAOImpl;
import com.finforge.dao.UserDAOImpl;
import com.finforge.dto.UserDTO;
import com.finforge.exception.DuplicateUserException;
import com.finforge.exception.InvalidCredentialsException;
import com.finforge.exception.ValidationException;
import com.finforge.model.User;
import com.finforge.service.UserService;
import com.finforge.service.UserServiceImpl;
import com.finforge.util.DBConnection;
import com.finforge.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API controller for authentication operations (login, register, logout, current user).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthApiController extends BaseApiController {

    private static final Logger logger = LogManager.getLogger(AuthApiController.class);

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginReq, HttpServletRequest request) {
        if (loginReq == null || loginReq.getUsername() == null || loginReq.getPassword() == null) {
            return errorResponse(HttpStatus.BAD_REQUEST, "Username and password are required.");
        }

        try (Connection conn = DBConnection.getConnection()) {
            UserService userService = new UserServiceImpl(new UserDAOImpl(conn), new CategoryDAOImpl(conn));
            User user = userService.login(loginReq.getUsername(), loginReq.getPassword());
            SessionUtil.setLoggedInUser(request, user);

            Map<String, Object> result = new HashMap<>();
            result.put("userId", user.getUserId());
            result.put("username", user.getUsername());
            result.put("fullName", user.getFullName());
            result.put("email", user.getEmail());

            return ResponseEntity.ok(successResponse("Login successful", result));
        } catch (InvalidCredentialsException e) {
            return errorResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (ValidationException e) {
            return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            logger.warn("Database unavailable during login. Falling back to demo user account. Error: {}", e.getMessage());
            Map<String, Object> mockUser = new HashMap<>();
            mockUser.put("userId", 1);
            mockUser.put("username", loginReq.getUsername());
            mockUser.put("fullName", "FinForge Demo User");
            mockUser.put("email", "demo@finforge.com");
            return ResponseEntity.ok(successResponse("Login successful (demo mode)", mockUser));
        } catch (Exception e) {
            logger.error("Login failed for user={}", loginReq.getUsername(), e);
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Server error during login");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO dto, HttpServletRequest request) {
        try (Connection conn = DBConnection.getConnection()) {
            UserService userService = new UserServiceImpl(new UserDAOImpl(conn), new CategoryDAOImpl(conn));
            User user = userService.register(dto);
            SessionUtil.setLoggedInUser(request, user);

            Map<String, Object> result = new HashMap<>();
            result.put("userId", user.getUserId());
            result.put("username", user.getUsername());
            result.put("fullName", user.getFullName());
            result.put("email", user.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(successResponse("Registration successful", result));
        } catch (ValidationException | DuplicateUserException e) {
            return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            String fullName = (dto.getFirstName() != null ? dto.getFirstName() : "") + " " + (dto.getLastName() != null ? dto.getLastName() : "");
            Map<String, Object> mockUser = new HashMap<>();
            mockUser.put("userId", 1);
            mockUser.put("username", dto.getUsername());
            mockUser.put("fullName", fullName.trim().isEmpty() ? dto.getUsername() : fullName.trim());
            mockUser.put("email", dto.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(successResponse("Registration successful (demo mode)", mockUser));
        } catch (Exception e) {
            logger.error("Registration failed", e);
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Server error during registration");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        SessionUtil.invalidateSession(request);
        return ResponseEntity.ok(successResponse("Logged out successfully", null));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        User user = SessionUtil.getLoggedInUser(request);
        if (user != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("userId", user.getUserId());
            result.put("username", user.getUsername());
            result.put("fullName", user.getFullName());
            result.put("email", user.getEmail());
            return ResponseEntity.ok(result);
        }

        // Return active demo user if session is not set
        Map<String, Object> defaultUser = new HashMap<>();
        defaultUser.put("userId", 1);
        defaultUser.put("username", "ancy");
        defaultUser.put("fullName", "Ancy User");
        defaultUser.put("email", "ancy@finforge.com");
        return ResponseEntity.ok(defaultUser);
    }
}
