package com.finforge.servlet;

import com.finforge.dao.UserDAOImpl;
import com.finforge.exception.DAOException;
import com.finforge.exception.InvalidCredentialsException;
import com.finforge.exception.UserNotFoundException;
import com.finforge.exception.ValidationException;
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
 * Handles the change-password form.
 * GET  /change-password   → show form
 * POST /change-password   → process password change
 */
@WebServlet("/change-password")
public class ChangePasswordServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(ChangePasswordServlet.class);
    private static final String VIEW = "/WEB-INF/jsp/change-password.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int    userId          = SessionUtil.getLoggedInUserId(req);
        String currentPassword = req.getParameter("currentPassword");
        String newPassword     = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        try (Connection conn = DBConnection.getConnection()) {
            UserServiceImpl userService = new UserServiceImpl(new UserDAOImpl(conn));
            userService.changePassword(userId, currentPassword, newPassword, confirmPassword);
            logger.info("Password changed successfully: userId={}", userId);
            req.setAttribute("success", "Password changed successfully.");
        } catch (ValidationException | InvalidCredentialsException e) {
            logger.warn("Password change validation failed for userId={}: {}", userId, e.getMessage());
            req.setAttribute("error", e.getMessage());
        } catch (UserNotFoundException | DAOException | SQLException e) {
            logger.error("Password change DAO/SQL error for userId={}", userId, e);
            req.setAttribute("error", "A server error occurred. Please try again.");
        }
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }
}
