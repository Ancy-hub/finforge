package com.finforge.servlet;

import com.finforge.dao.ReportDAOImpl;
import com.finforge.dto.ReportDTO;
import com.finforge.exception.DAOException;
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
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Generates and displays financial reports for the logged-in user.
 */
@WebServlet("/reports")
public class ReportServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(ReportServlet.class);
    private static final String VIEW = "/WEB-INF/jsp/report/report.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int userId = SessionUtil.getLoggedInUserId(req);

        try (Connection conn = DBConnection.getConnection()) {
            ReportServiceImpl reportService = new ReportServiceImpl(new ReportDAOImpl(conn));
            ReportDTO report = reportService.generateReport(userId);
            logger.debug("Report generated: userId={}", userId);
            req.setAttribute("report", report);
        } catch (DAOException | SQLException e) {
            logger.error("Report generation failed for userId={}", userId, e);
            req.setAttribute("error", "Failed to generate report. Please try again.");
        }
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }
}
