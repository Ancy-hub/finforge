package com.finforge.servlet;

import com.finforge.dao.CategoryDAOImpl;
import com.finforge.dao.UserDAOImpl;
import com.finforge.exception.DAOException;
import com.finforge.exception.InvalidCredentialsException;
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
 * Handles user login (GET: show form, POST: authenticate).
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(LoginServlet.class);
    private static final String VIEW = "/WEB-INF/jsp/login.jsp";

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

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try (Connection conn = DBConnection.getConnection()) {
            UserServiceImpl userService = new UserServiceImpl(
                    new UserDAOImpl(conn),
                    new CategoryDAOImpl(conn));

            User user = userService.login(username, password);
            SessionUtil.setLoggedInUser(req, user);
            logger.info("Login successful: userId={} username='{}'",
                    user.getUserId(), user.getUsername());
            resp.sendRedirect(req.getContextPath() + "/dashboard");

        } catch (ValidationException | InvalidCredentialsException e) {
            logger.warn("Login failed for username='{}': {}", username, e.getMessage());
            req.setAttribute("error", e.getMessage());
            req.setAttribute("username", username);
            req.getRequestDispatcher(VIEW).forward(req, resp);
        } catch (DAOException | SQLException e) {
            logger.error("Login DAO/SQL error for username='{}'", username, e);
            req.setAttribute("error", "A server error occurred. Please try again.");
            req.getRequestDispatcher(VIEW).forward(req, resp);
        }
    }
}
