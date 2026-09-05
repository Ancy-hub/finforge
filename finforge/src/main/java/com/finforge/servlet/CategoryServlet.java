package com.finforge.servlet;

import com.finforge.dao.CategoryDAOImpl;
import com.finforge.dto.CategoryDTO;
import com.finforge.exception.DAOException;
import com.finforge.exception.ValidationException;
import com.finforge.model.Category;
import com.finforge.service.CategoryServiceImpl;
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
import java.util.List;

/**
 * Handles all CRUD operations for expense categories.
 *
 * <pre>
 * GET  /categories                  → list all categories
 * GET  /categories?action=add       → show add form
 * POST /categories?action=add       → save new category
 * GET  /categories?action=edit&id=n → show edit form
 * POST /categories?action=edit      → save updated category
 * POST /categories?action=delete    → delete category
 * </pre>
 */
@WebServlet("/categories")
public class CategoryServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(CategoryServlet.class);
    private static final String LIST_VIEW = "/WEB-INF/jsp/category/list-categories.jsp";
    private static final String ADD_VIEW  = "/WEB-INF/jsp/category/add-category.jsp";
    private static final String EDIT_VIEW = "/WEB-INF/jsp/category/edit-category.jsp";

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
                CategoryServiceImpl catService = new CategoryServiceImpl(new CategoryDAOImpl(conn));
                int      catId    = Integer.parseInt(idStr);
                Category category = catService.getCategoryById(catId, userId);
                req.setAttribute("category", category);
            } catch (NumberFormatException | ValidationException | DAOException | SQLException e) {
                logger.warn("Category load-for-edit failed: userId={} idStr='{}': {}",
                        userId, idStr, e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/categories");
                return;
            }
            req.getRequestDispatcher(EDIT_VIEW).forward(req, resp);

        } else {
            try (Connection conn = DBConnection.getConnection()) {
                CategoryServiceImpl catService = new CategoryServiceImpl(new CategoryDAOImpl(conn));
                List<Category> categories = catService.getAllCategories(userId);
                req.setAttribute("categories", categories);
            } catch (DAOException | SQLException e) {
                logger.error("Category list load failed for userId={}", userId, e);
                req.setAttribute("error", "Failed to load categories.");
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

        CategoryDTO dto = buildDto(req);
        try (Connection conn = DBConnection.getConnection()) {
            new CategoryServiceImpl(new CategoryDAOImpl(conn)).addCategory(userId, dto);
            resp.sendRedirect(req.getContextPath() + "/categories?success=added");
        } catch (ValidationException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("dto", dto);
            req.getRequestDispatcher(ADD_VIEW).forward(req, resp);
        } catch (DAOException | SQLException e) {
            req.setAttribute("error", "Failed to add category. Please try again.");
            req.getRequestDispatcher(ADD_VIEW).forward(req, resp);
        }
    }

    private void handleEdit(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {

        CategoryDTO dto = buildDto(req);
        dto.setCategoryId(req.getParameter("categoryId"));
        try (Connection conn = DBConnection.getConnection()) {
            new CategoryServiceImpl(new CategoryDAOImpl(conn)).updateCategory(userId, dto);
            resp.sendRedirect(req.getContextPath() + "/categories?success=updated");
        } catch (ValidationException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("dto", dto);
            req.getRequestDispatcher(EDIT_VIEW).forward(req, resp);
        } catch (DAOException | SQLException e) {
            req.setAttribute("error", "Failed to update category. Please try again.");
            req.getRequestDispatcher(EDIT_VIEW).forward(req, resp);
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws IOException {
        try {
            int catId = Integer.parseInt(req.getParameter("id"));
            try (Connection conn = DBConnection.getConnection()) {
                new CategoryServiceImpl(new CategoryDAOImpl(conn)).deleteCategory(catId, userId);
            }
        } catch (NumberFormatException | ValidationException | DAOException | SQLException e) {
            logger.warn("Category delete failed: userId={} id='{}': {}",
                    userId, req.getParameter("id"), e.getMessage());
            // Redirect regardless
        }
        resp.sendRedirect(req.getContextPath() + "/categories");
    }

    private CategoryDTO buildDto(HttpServletRequest req) {
        CategoryDTO dto = new CategoryDTO();
        dto.setName(req.getParameter("name"));
        dto.setDescription(req.getParameter("description"));
        return dto;
    }
}
