package com.finforge.servlet;

import com.finforge.dao.UserDAOImpl;
import com.finforge.dto.UserDTO;
import com.finforge.exception.DAOException;
import com.finforge.exception.UserNotFoundException;
import com.finforge.exception.ValidationException;
import com.finforge.model.User;
import com.finforge.service.UserServiceImpl;
import com.finforge.util.DBConnection;
import com.finforge.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Handles viewing and updating the user profile.
 * GET  /profile        → show current profile
 * POST /profile        → save updated profile
 */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(ProfileServlet.class);
    private static final String VIEW = "/WEB-INF/jsp/profile.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int userId = SessionUtil.getLoggedInUserId(req);

        try (Connection conn = DBConnection.getConnection()) {
            UserServiceImpl userService = new UserServiceImpl(new UserDAOImpl(conn));
            User user = userService.getProfile(userId);
            req.setAttribute("user", user);
        } catch (UserNotFoundException | DAOException | SQLException e) {
            req.setAttribute("error", e.getMessage());
        }
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int userId = SessionUtil.getLoggedInUserId(req);

        UserDTO dto = new UserDTO();
        dto.setFirstName(req.getParameter("firstName"));
        dto.setLastName(req.getParameter("lastName"));
        dto.setEmail(req.getParameter("email"));
        dto.setPhone(req.getParameter("phone"));

        try (Connection conn = DBConnection.getConnection()) {
            UserServiceImpl userService = new UserServiceImpl(new UserDAOImpl(conn));
            userService.updateProfile(userId, dto);

            // Refresh session user
            User updatedUser = userService.getProfile(userId);
            SessionUtil.setLoggedInUser(req, updatedUser);
            logger.info("Profile updated: userId={}", userId);

            req.setAttribute("success", "Profile updated successfully.");
            req.setAttribute("user", updatedUser);
        } catch (ValidationException | UserNotFoundException e) {
            logger.warn("Profile update validation failed for userId={}: {}", userId, e.getMessage());
            req.setAttribute("error", e.getMessage());
        } catch (DAOException | SQLException e) {
            logger.error("Profile update DAO/SQL error for userId={}", userId, e);
            req.setAttribute("error", "A server error occurred. Please try again.");
        }
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }
}
