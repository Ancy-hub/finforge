package com.finforge.servlet;

import com.finforge.dao.CategoryDAOImpl;
import com.finforge.dao.ExpenseDAOImpl;
import com.finforge.dto.ExpenseDTO;
import com.finforge.dto.ExpenseFilterDTO;
import com.finforge.dto.PagedResult;
import com.finforge.exception.DAOException;
import com.finforge.exception.ValidationException;
import com.finforge.model.Category;
import com.finforge.model.Expense;
import com.finforge.service.CategoryServiceImpl;
import com.finforge.service.ExpenseServiceImpl;
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
import java.util.List;

/**
 * Handles all CRUD operations for expenses.
 *
 * <pre>
 * GET  /expenses                  → list all expenses
 * GET  /expenses?action=add       → show add form
 * POST /expenses?action=add       → save new expense
 * GET  /expenses?action=edit&id=n → show edit form
 * POST /expenses?action=edit      → save updated expense
 * POST /expenses?action=delete    → delete expense
 * </pre>
 */
@WebServlet("/expenses")
public class ExpenseServlet extends HttpServlet {

    private static final String LIST_VIEW      = "/WEB-INF/jsp/expense/list-expenses.jsp";
    private static final String ADD_VIEW       = "/WEB-INF/jsp/expense/add-expense.jsp";
    private static final String EDIT_VIEW      = "/WEB-INF/jsp/expense/edit-expense.jsp";
    private static final int    PAGE_SIZE      = 10;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        int    userId = SessionUtil.getLoggedInUserId(req);

        if ("add".equals(action)) {
            loadCategories(req, userId);
            req.getRequestDispatcher(ADD_VIEW).forward(req, resp);

        } else if ("edit".equals(action)) {
            String idStr = req.getParameter("id");
            try (Connection conn = DBConnection.getConnection()) {
                ExpenseServiceImpl  expService = new ExpenseServiceImpl(new ExpenseDAOImpl(conn));
                CategoryServiceImpl catService = new CategoryServiceImpl(new CategoryDAOImpl(conn));

                int     expId   = Integer.parseInt(idStr);
                Expense expense = expService.getExpenseById(expId, userId);
                List<Category> cats = catService.getAllCategories(userId);

                req.setAttribute("expense",    expense);
                req.setAttribute("categories", cats);
            } catch (NumberFormatException | ValidationException | DAOException | SQLException e) {
                req.setAttribute("error", "Expense not found.");
                resp.sendRedirect(req.getContextPath() + "/expenses");
                return;
            }
            req.getRequestDispatcher(EDIT_VIEW).forward(req, resp);

        } else {
            // ---- List with optional filter + pagination ----
            ExpenseFilterDTO filter = new ExpenseFilterDTO();
            filter.setFromDate(req.getParameter("fromDate"));
            filter.setToDate(req.getParameter("toDate"));
            filter.setCategoryId(req.getParameter("categoryId"));

            String pageParam = req.getParameter("page");
            int page = 1;
            if (pageParam != null && pageParam.matches("\\d+")) {
                page = Math.max(1, Integer.parseInt(pageParam));
            }

            try (Connection conn = DBConnection.getConnection()) {
                ExpenseServiceImpl  expService = new ExpenseServiceImpl(new ExpenseDAOImpl(conn));
                CategoryServiceImpl catService = new CategoryServiceImpl(new CategoryDAOImpl(conn));

                if (filter.hasAnyFilter()) {
                    List<Expense> expenses = expService.searchExpenses(userId, filter);
                    req.setAttribute("expenses", expenses);
                    req.setAttribute("filtered", true);
                } else {
                    PagedResult<Expense> paged = expService.getAllExpensesPaged(userId, page, PAGE_SIZE);
                    req.setAttribute("expenses", paged.getItems());
                    req.setAttribute("paged", paged);
                }
                req.setAttribute("categories", catService.getAllCategories(userId));
                req.setAttribute("filter", filter);
            } catch (ValidationException | DAOException | SQLException e) {
                req.setAttribute("error", "Failed to load expenses.");
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
    // Private action handlers
    // -----------------------------------------------------------------------

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {

        ExpenseDTO dto = buildDtoFromRequest(req);
        try (Connection conn = DBConnection.getConnection()) {
            new ExpenseServiceImpl(new ExpenseDAOImpl(conn)).addExpense(userId, dto);
            resp.sendRedirect(req.getContextPath() + "/expenses?success=added");
        } catch (ValidationException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("dto", dto);
            loadCategories(req, userId);
            req.getRequestDispatcher(ADD_VIEW).forward(req, resp);
        } catch (DAOException | SQLException e) {
            req.setAttribute("error", "Failed to add expense. Please try again.");
            loadCategories(req, userId);
            req.getRequestDispatcher(ADD_VIEW).forward(req, resp);
        }
    }

    private void handleEdit(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {

        ExpenseDTO dto = buildDtoFromRequest(req);
        dto.setExpenseId(req.getParameter("expenseId"));
        try (Connection conn = DBConnection.getConnection()) {
            new ExpenseServiceImpl(new ExpenseDAOImpl(conn)).updateExpense(userId, dto);
            resp.sendRedirect(req.getContextPath() + "/expenses?success=updated");
        } catch (ValidationException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("dto", dto);
            loadCategories(req, userId);
            req.getRequestDispatcher(EDIT_VIEW).forward(req, resp);
        } catch (DAOException | SQLException e) {
            req.setAttribute("error", "Failed to update expense. Please try again.");
            req.getRequestDispatcher(EDIT_VIEW).forward(req, resp);
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws IOException {
        try {
            int expenseId = Integer.parseInt(req.getParameter("id"));
            try (Connection conn = DBConnection.getConnection()) {
                new ExpenseServiceImpl(new ExpenseDAOImpl(conn)).deleteExpense(expenseId, userId);
            }
        } catch (NumberFormatException | ValidationException | DAOException | SQLException e) {
            // Log and redirect with error param
        }
        resp.sendRedirect(req.getContextPath() + "/expenses");
    }

    // -----------------------------------------------------------------------

    private ExpenseDTO buildDtoFromRequest(HttpServletRequest req) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setTitle(req.getParameter("title"));
        dto.setDescription(req.getParameter("description"));
        dto.setAmount(req.getParameter("amount"));
        dto.setCategoryId(req.getParameter("categoryId"));
        dto.setExpenseDate(req.getParameter("expenseDate"));
        return dto;
    }

    private void loadCategories(HttpServletRequest req, int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            CategoryServiceImpl catService = new CategoryServiceImpl(new CategoryDAOImpl(conn));
            req.setAttribute("categories", catService.getAllCategories(userId));
        } catch (DAOException | SQLException e) {
            req.setAttribute("categories", List.of());
        }
    }
}
