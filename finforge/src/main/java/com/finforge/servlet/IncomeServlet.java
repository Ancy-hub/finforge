package com.finforge.servlet;

import com.finforge.dao.IncomeDAOImpl;
import com.finforge.dto.IncomeDTO;
import com.finforge.dto.PagedResult;
import com.finforge.exception.DAOException;
import com.finforge.exception.ValidationException;
import com.finforge.model.Income;
import com.finforge.service.IncomeServiceImpl;
import com.finforge.util.DBConnection;
import com.finforge.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Handles all CRUD operations for income entries.
 *
 * <pre>
 * GET  /incomes                  → list all incomes
 * GET  /incomes?action=add       → show add form
 * POST /incomes?action=add       → save new income
 * GET  /incomes?action=edit&id=n → show edit form
 * POST /incomes?action=edit      → save updated income
 * POST /incomes?action=delete    → delete income
 * </pre>
 */
@WebServlet("/incomes")
public class IncomeServlet extends HttpServlet {

    private static final String LIST_VIEW = "/WEB-INF/jsp/income/list-incomes.jsp";
    private static final String ADD_VIEW  = "/WEB-INF/jsp/income/add-income.jsp";
    private static final String EDIT_VIEW = "/WEB-INF/jsp/income/edit-income.jsp";
    private static final int    PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        int    userId = SessionUtil.getLoggedInUserId(req);

        if ("add".equals(action)) {
            req.getRequestDispatcher(ADD_VIEW).forward(req, resp);

        } else if ("edit".equals(action)) {
            String idStr = req.getParameter("id");
            try (Connection conn = DBConnection.getConnection()) {
                IncomeServiceImpl incService = new IncomeServiceImpl(new IncomeDAOImpl(conn));
                int    incId  = Integer.parseInt(idStr);
                Income income = incService.getIncomeById(incId, userId);
                req.setAttribute("income", income);
            } catch (NumberFormatException | ValidationException | DAOException | SQLException e) {
                resp.sendRedirect(req.getContextPath() + "/incomes");
                return;
            }
            req.getRequestDispatcher(EDIT_VIEW).forward(req, resp);

        } else {
            // ---- List with pagination ----
            String pageParam = req.getParameter("page");
            int page = 1;
            if (pageParam != null && pageParam.matches("\\d+")) {
                page = Math.max(1, Integer.parseInt(pageParam));
            }
            try (Connection conn = DBConnection.getConnection()) {
                IncomeServiceImpl incService = new IncomeServiceImpl(new IncomeDAOImpl(conn));
                PagedResult<Income> paged = incService.getAllIncomesPaged(userId, page, PAGE_SIZE);
                req.setAttribute("incomes", paged.getItems());
                req.setAttribute("paged", paged);
            } catch (DAOException | SQLException e) {
                req.setAttribute("error", "Failed to load income records.");
            }
            req.getRequestDispatcher(LIST_VIEW).forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        int    userId = SessionUtil.getLoggedInUserId(req);

        if ("delete".equals(action)) {
            handleDelete(req, resp, userId);
        } else if ("edit".equals(action)) {
            handleEdit(req, resp, userId);
        } else {
            handleAdd(req, resp, userId);
        }
    }

    // -----------------------------------------------------------------------

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {

        IncomeDTO dto = buildDto(req);
        try (Connection conn = DBConnection.getConnection()) {
            new IncomeServiceImpl(new IncomeDAOImpl(conn)).addIncome(userId, dto);
            resp.sendRedirect(req.getContextPath() + "/incomes?success=added");
        } catch (ValidationException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("dto", dto);
            req.getRequestDispatcher(ADD_VIEW).forward(req, resp);
        } catch (DAOException | SQLException e) {
            req.setAttribute("error", "Failed to add income. Please try again.");
            req.getRequestDispatcher(ADD_VIEW).forward(req, resp);
        }
    }

    private void handleEdit(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {

        IncomeDTO dto = buildDto(req);
        dto.setIncomeId(req.getParameter("incomeId"));
        try (Connection conn = DBConnection.getConnection()) {
            new IncomeServiceImpl(new IncomeDAOImpl(conn)).updateIncome(userId, dto);
            resp.sendRedirect(req.getContextPath() + "/incomes?success=updated");
        } catch (ValidationException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("dto", dto);
            req.getRequestDispatcher(EDIT_VIEW).forward(req, resp);
        } catch (DAOException | SQLException e) {
            req.setAttribute("error", "Failed to update income. Please try again.");
            req.getRequestDispatcher(EDIT_VIEW).forward(req, resp);
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws IOException {
        try {
            int incomeId = Integer.parseInt(req.getParameter("id"));
            try (Connection conn = DBConnection.getConnection()) {
                new IncomeServiceImpl(new IncomeDAOImpl(conn)).deleteIncome(incomeId, userId);
            }
        } catch (NumberFormatException | ValidationException | DAOException | SQLException e) {
            // Redirect regardless
        }
        resp.sendRedirect(req.getContextPath() + "/incomes");
    }

    private IncomeDTO buildDto(HttpServletRequest req) {
        IncomeDTO dto = new IncomeDTO();
        dto.setSource(req.getParameter("source"));
        dto.setAmount(req.getParameter("amount"));
        dto.setIncomeDate(req.getParameter("incomeDate"));
        return dto;
    }
}
