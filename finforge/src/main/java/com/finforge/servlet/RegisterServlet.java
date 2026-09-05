package com.finforge.servlet;

import com.finforge.dao.CategoryDAOImpl;
import com.finforge.dao.UserDAOImpl;
import com.finforge.dto.UserDTO;
import com.finforge.exception.DAOException;
import com.finforge.exception.DuplicateUserException;
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
 * Handles user registration (GET: show form, POST: process registration).
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(RegisterServlet.class);
    private static final String VIEW = "/WEB-INF/jsp/register.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (SessionUtil.isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UserDTO dto = new UserDTO();
        dto.setUsername(req.getParameter("username"));
        dto.setEmail(req.getParameter("email"));
        dto.setPassword(req.getParameter("password"));
        dto.setConfirmPassword(req.getParameter("confirmPassword"));
        dto.setFirstName(req.getParameter("firstName"));
        dto.setLastName(req.getParameter("lastName"));
        dto.setPhone(req.getParameter("phone"));

        try (Connection conn = DBConnection.getConnection()) {
            UserServiceImpl userService = new UserServiceImpl(
                    new UserDAOImpl(conn),
                    new CategoryDAOImpl(conn));

            User user = userService.register(dto);
            SessionUtil.setLoggedInUser(req, user);
            logger.info("Registration successful: userId={} username='{}'",
                    user.getUserId(), user.getUsername());
            resp.sendRedirect(req.getContextPath() + "/dashboard");

        } catch (ValidationException | DuplicateUserException e) {
            logger.warn("Registration failed for username='{}': {}", dto.getUsername(), e.getMessage());
            req.setAttribute("error", e.getMessage());
            req.setAttribute("dto", dto);
            req.getRequestDispatcher(VIEW).forward(req, resp);
        } catch (DAOException | SQLException e) {
            logger.error("Registration DAO/SQL error for username='{}'", dto.getUsername(), e);
            req.setAttribute("error", "A server error occurred. Please try again.");
            req.getRequestDispatcher(VIEW).forward(req, resp);
        }
    }
}
