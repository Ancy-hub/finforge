package com.finforge.dao;

import com.finforge.exception.DAOException;
import com.finforge.model.Expense;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link ExpenseDAO} backed by SQL Server.
 */
public class ExpenseDAOImpl implements ExpenseDAO {

    private final Connection connection;

    public ExpenseDAOImpl(Connection connection) {
        this.connection = connection;
    }

    // -----------------------------------------------------------------------
    // SQL constants
    // -----------------------------------------------------------------------

    private static final String SQL_INSERT =
            "INSERT INTO expenses (title, description, amount, category_id, user_id, expense_date) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_ID =
            "SELECT e.expense_id, e.title, e.description, e.amount, e.category_id, e.user_id, " +
            "       e.expense_date, e.created_at, e.updated_at, c.name AS category_name " +
            "FROM expenses e " +
            "LEFT JOIN categories c ON e.category_id = c.category_id " +
            "WHERE e.expense_id = ?";

    private static final String SQL_FIND_ALL_BY_USER =
            "SELECT e.expense_id, e.title, e.description, e.amount, e.category_id, e.user_id, " +
            "       e.expense_date, e.created_at, e.updated_at, c.name AS category_name " +
            "FROM expenses e " +
            "LEFT JOIN categories c ON e.category_id = c.category_id " +
            "WHERE e.user_id = ? " +
            "ORDER BY e.expense_date DESC, e.created_at DESC";

    private static final String SQL_UPDATE =
            "UPDATE expenses SET title = ?, description = ?, amount = ?, " +
            "category_id = ?, expense_date = ?, updated_at = GETDATE() " +
            "WHERE expense_id = ? AND user_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM expenses WHERE expense_id = ?";

    private static final String SQL_EXISTS =
            "SELECT COUNT(1) FROM expenses WHERE expense_id = ? AND user_id = ?";

    private static final String SQL_COUNT_BY_USER =
            "SELECT COUNT(1) FROM expenses WHERE user_id = ?";

    private static final String SQL_FIND_PAGED =
            "SELECT e.expense_id, e.title, e.description, e.amount, e.category_id, e.user_id, " +
            "       e.expense_date, e.created_at, e.updated_at, c.name AS category_name " +
            "FROM expenses e " +
            "LEFT JOIN categories c ON e.category_id = c.category_id " +
            "WHERE e.user_id = ? " +
            "ORDER BY e.expense_date DESC, e.created_at DESC " +
            "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

    // -----------------------------------------------------------------------
    // Implementations
    // -----------------------------------------------------------------------

    @Override
    public Expense save(Expense expense) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(
                SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, expense.getTitle());
            ps.setString(2, expense.getDescription());
            ps.setBigDecimal(3, expense.getAmount());
            ps.setInt(4, expense.getCategoryId());
            ps.setInt(5, expense.getUserId());
            ps.setDate(6, Date.valueOf(expense.getExpenseDate()));

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DAOException("Inserting expense failed — no rows affected.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    expense.setExpenseId(keys.getInt(1));
                }
            }
            return expense;

        } catch (SQLException e) {
            throw new DAOException("Failed to save expense: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Expense> findById(int expenseId) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, expenseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to find expense by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Expense> findAllByUserId(int userId) throws DAOException {
        List<Expense> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_FIND_ALL_BY_USER)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve expenses: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void update(Expense expense) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, expense.getTitle());
            ps.setString(2, expense.getDescription());
            ps.setBigDecimal(3, expense.getAmount());
            ps.setInt(4, expense.getCategoryId());
            ps.setDate(5, Date.valueOf(expense.getExpenseDate()));
            ps.setInt(6, expense.getExpenseId());
            ps.setInt(7, expense.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to update expense: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int expenseId) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, expenseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to delete expense: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByIdAndUserId(int expenseId, int userId) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_EXISTS)) {
            ps.setInt(1, expenseId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to check expense ownership: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Expense> findByFilters(int userId, LocalDate fromDate, LocalDate toDate,
                                       Integer categoryId) throws DAOException {
        List<Expense> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT e.expense_id, e.title, e.description, e.amount, e.category_id, e.user_id, " +
                "       e.expense_date, e.created_at, e.updated_at, c.name AS category_name " +
                "FROM expenses e " +
                "LEFT JOIN categories c ON e.category_id = c.category_id " +
                "WHERE e.user_id = ?");
        if (fromDate   != null) sql.append(" AND e.expense_date >= ?");
        if (toDate     != null) sql.append(" AND e.expense_date <= ?");
        if (categoryId != null) sql.append(" AND e.category_id = ?");
        sql.append(" ORDER BY e.expense_date DESC, e.created_at DESC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, userId);
            if (fromDate   != null) ps.setDate(idx++, Date.valueOf(fromDate));
            if (toDate     != null) ps.setDate(idx++, Date.valueOf(toDate));
            if (categoryId != null) ps.setInt(idx++, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to filter expenses: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public int countByUserId(int userId) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_COUNT_BY_USER)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to count expenses: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Expense> findByUserIdPaged(int userId, int offset, int limit) throws DAOException {
        List<Expense> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_FIND_PAGED)) {
            ps.setInt(1, userId);
            ps.setInt(2, offset);
            ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve paged expenses: " + e.getMessage(), e);
        }
        return list;
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    private Expense mapRow(ResultSet rs) throws SQLException {
        Expense e = new Expense();
        e.setExpenseId(rs.getInt("expense_id"));
        e.setTitle(rs.getString("title"));
        e.setDescription(rs.getString("description"));
        e.setAmount(rs.getBigDecimal("amount"));
        e.setCategoryId(rs.getInt("category_id"));
        e.setUserId(rs.getInt("user_id"));

        Date expDate = rs.getDate("expense_date");
        if (expDate != null) {
            e.setExpenseDate(expDate.toLocalDate());
        }
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            e.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            e.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        e.setCategoryName(rs.getString("category_name"));
        return e;
    }
}
