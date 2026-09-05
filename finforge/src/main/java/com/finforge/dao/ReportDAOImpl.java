package com.finforge.dao;

import com.finforge.dto.CategoryReportDTO;
import com.finforge.dto.MonthlyReportDTO;
import com.finforge.exception.DAOException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of {@link ReportDAO} backed by SQL Server.
 */
public class ReportDAOImpl implements ReportDAO {

    private final Connection connection;

    public ReportDAOImpl(Connection connection) {
        this.connection = connection;
    }

    // -----------------------------------------------------------------------
    // SQL constants
    // -----------------------------------------------------------------------

    private static final String SQL_TOTAL_INCOME =
            "SELECT ISNULL(SUM(amount), 0) FROM incomes WHERE user_id = ?";

    private static final String SQL_TOTAL_EXPENSE =
            "SELECT ISNULL(SUM(amount), 0) FROM expenses WHERE user_id = ?";

    private static final String SQL_MONTHLY_EXPENSES =
            "SELECT FORMAT(expense_date, 'yyyy-MM') AS month, " +
            "       SUM(amount) AS total_amount " +
            "FROM expenses " +
            "WHERE user_id = ? " +
            "GROUP BY FORMAT(expense_date, 'yyyy-MM') " +
            "ORDER BY FORMAT(expense_date, 'yyyy-MM') ASC";

    private static final String SQL_CATEGORY_EXPENSES =
            "SELECT c.name AS category_name, ISNULL(SUM(e.amount), 0) AS total_amount " +
            "FROM categories c " +
            "LEFT JOIN expenses e ON c.category_id = e.category_id AND e.user_id = c.user_id " +
            "WHERE c.user_id = ? " +
            "GROUP BY c.name " +
            "ORDER BY total_amount DESC";

    // -----------------------------------------------------------------------
    // Implementations
    // -----------------------------------------------------------------------

    @Override
    public BigDecimal getTotalIncome(int userId) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_TOTAL_INCOME)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal val = rs.getBigDecimal(1);
                    return (val != null) ? val : BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to calculate total income: " + e.getMessage(), e);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalExpense(int userId) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_TOTAL_EXPENSE)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal val = rs.getBigDecimal(1);
                    return (val != null) ? val : BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to calculate total expense: " + e.getMessage(), e);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public List<MonthlyReportDTO> getMonthlyExpenses(int userId) throws DAOException {
        List<MonthlyReportDTO> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_MONTHLY_EXPENSES)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String     month  = rs.getString("month");
                    BigDecimal amount = rs.getBigDecimal("total_amount");
                    list.add(new MonthlyReportDTO(month, amount));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve monthly expenses: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<CategoryReportDTO> getCategoryExpenses(int userId) throws DAOException {
        List<CategoryReportDTO> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_CATEGORY_EXPENSES)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String     name   = rs.getString("category_name");
                    BigDecimal amount = rs.getBigDecimal("total_amount");
                    list.add(new CategoryReportDTO(name, amount));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve category expenses: " + e.getMessage(), e);
        }
        return list;
    }
}
