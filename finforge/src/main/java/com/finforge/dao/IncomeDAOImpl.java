package com.finforge.dao;

import com.finforge.exception.DAOException;
import com.finforge.model.Income;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link IncomeDAO} backed by SQL Server.
 */
public class IncomeDAOImpl implements IncomeDAO {

    private final Connection connection;

    public IncomeDAOImpl(Connection connection) {
        this.connection = connection;
    }

    // -----------------------------------------------------------------------
    // SQL constants
    // -----------------------------------------------------------------------

    private static final String SQL_INSERT =
            "INSERT INTO incomes (source, amount, income_date, user_id) VALUES (?, ?, ?, ?)";

    private static final String SQL_FIND_BY_ID =
            "SELECT income_id, source, amount, income_date, user_id, created_at, updated_at " +
            "FROM incomes WHERE income_id = ?";

    private static final String SQL_FIND_ALL_BY_USER =
            "SELECT income_id, source, amount, income_date, user_id, created_at, updated_at " +
            "FROM incomes WHERE user_id = ? ORDER BY income_date DESC, created_at DESC";

    private static final String SQL_UPDATE =
            "UPDATE incomes SET source = ?, amount = ?, income_date = ?, updated_at = GETDATE() " +
            "WHERE income_id = ? AND user_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM incomes WHERE income_id = ?";

    private static final String SQL_EXISTS =
            "SELECT COUNT(1) FROM incomes WHERE income_id = ? AND user_id = ?";

    private static final String SQL_COUNT_BY_USER =
            "SELECT COUNT(1) FROM incomes WHERE user_id = ?";

    private static final String SQL_FIND_PAGED =
            "SELECT income_id, source, amount, income_date, user_id, created_at, updated_at " +
            "FROM incomes WHERE user_id = ? " +
            "ORDER BY income_date DESC, created_at DESC " +
            "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

    // -----------------------------------------------------------------------
    // Implementations
    // -----------------------------------------------------------------------

    @Override
    public Income save(Income income) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(
                SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, income.getSource());
            ps.setBigDecimal(2, income.getAmount());
            ps.setDate(3, Date.valueOf(income.getIncomeDate()));
            ps.setInt(4, income.getUserId());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DAOException("Inserting income failed — no rows affected.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    income.setIncomeId(keys.getInt(1));
                }
            }
            return income;

        } catch (SQLException e) {
            throw new DAOException("Failed to save income: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Income> findById(int incomeId) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, incomeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to find income by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Income> findAllByUserId(int userId) throws DAOException {
        List<Income> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_FIND_ALL_BY_USER)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve incomes: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void update(Income income) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, income.getSource());
            ps.setBigDecimal(2, income.getAmount());
            ps.setDate(3, Date.valueOf(income.getIncomeDate()));
            ps.setInt(4, income.getIncomeId());
            ps.setInt(5, income.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to update income: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int incomeId) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, incomeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to delete income: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByIdAndUserId(int incomeId, int userId) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_EXISTS)) {
            ps.setInt(1, incomeId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to check income ownership: " + e.getMessage(), e);
        }
    }

    @Override
    public int countByUserId(int userId) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_COUNT_BY_USER)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to count income records: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Income> findByUserIdPaged(int userId, int offset, int limit) throws DAOException {
        List<Income> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_FIND_PAGED)) {
            ps.setInt(1, userId);
            ps.setInt(2, offset);
            ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve paged income records: " + e.getMessage(), e);
        }
        return list;
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    private Income mapRow(ResultSet rs) throws SQLException {
        Income i = new Income();
        i.setIncomeId(rs.getInt("income_id"));
        i.setSource(rs.getString("source"));
        i.setAmount(rs.getBigDecimal("amount"));

        Date incDate = rs.getDate("income_date");
        if (incDate != null) {
            i.setIncomeDate(incDate.toLocalDate());
        }
        i.setUserId(rs.getInt("user_id"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            i.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            i.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return i;
    }
}
