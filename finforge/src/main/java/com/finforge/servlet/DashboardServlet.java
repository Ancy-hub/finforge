package com.finforge.servlet;

import com.finforge.dao.ReportDAOImpl;
import com.finforge.dto.ReportDTO;
import com.finforge.exception.DAOException;
import com.finforge.model.User;
import com.finforge.service.ReportServiceImpl;
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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Renders the application dashboard showing financial summary for the logged-in user.
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(DashboardServlet.class);
    private static final String VIEW = "/WEB-INF/jsp/dashboard.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User loggedInUser = SessionUtil.getLoggedInUser(req);
        int  userId       = loggedInUser.getUserId();

        try (Connection conn = DBConnection.getConnection()) {
            ReportServiceImpl reportService = new ReportServiceImpl(new ReportDAOImpl(conn));
            ReportDTO         report        = reportService.generateReport(userId);

            req.setAttribute("report",      report);
            req.setAttribute("currentUser", loggedInUser);
        } catch (DAOException | SQLException e) {
            logger.error("Dashboard data load failed for userId={}", userId, e);
            req.setAttribute("error", "Failed to load dashboard data.");
            // Provide zero-value fallback so JSP can still render
            ReportDTO empty = new ReportDTO();
            empty.setTotalIncome(BigDecimal.ZERO);
            empty.setTotalExpense(BigDecimal.ZERO);
            empty.setNetSavings(BigDecimal.ZERO);
            req.setAttribute("report", empty);
        }
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }
}
