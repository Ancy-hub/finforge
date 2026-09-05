package com.finforge.dao;

import com.finforge.exception.DAOException;
import com.finforge.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link CategoryDAO} backed by SQL Server.
 */
public class CategoryDAOImpl implements CategoryDAO {

    private final Connection connection;

    public CategoryDAOImpl(Connection connection) {
        this.connection = connection;
    }

    // -----------------------------------------------------------------------
    // SQL constants
    // -----------------------------------------------------------------------

    private static final String SQL_INSERT =
            "INSERT INTO categories (name, description, user_id) VALUES (?, ?, ?)";

    private static final String SQL_FIND_BY_ID =
            "SELECT category_id, name, description, user_id, created_at " +
            "FROM categories WHERE category_id = ?";

    private static final String SQL_FIND_ALL_BY_USER =
            "SELECT category_id, name, description, user_id, created_at " +
            "FROM categories WHERE user_id = ? ORDER BY name ASC";

    private static final String SQL_UPDATE =
            "UPDATE categories SET name = ?, description = ? " +
            "WHERE category_id = ? AND user_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM categories WHERE category_id = ?";

    private static final String SQL_EXISTS_BY_ID_USER =
            "SELECT COUNT(1) FROM categories WHERE category_id = ? AND user_id = ?";

    private static final String SQL_EXISTS_BY_NAME_USER =
            "SELECT COUNT(1) FROM categories WHERE name = ? AND user_id = ?";

    // -----------------------------------------------------------------------
    // Implementations
    // -----------------------------------------------------------------------

    @Override
    public Category save(Category category) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(
                SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, category.getUserId());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DAOException("Inserting category failed — no rows affected.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    category.setCategoryId(keys.getInt(1));
                }
            }
            return category;

        } catch (SQLException e) {
            throw new DAOException("Failed to save category: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Category> findById(int categoryId) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to find category by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Category> findAllByUserId(int userId) throws DAOException {
        List<Category> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_FIND_ALL_BY_USER)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to retrieve categories: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void update(Category category) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, category.getCategoryId());
            ps.setInt(4, category.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to update category: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int categoryId) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, categoryId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to delete category: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByIdAndUserId(int categoryId, int userId) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_EXISTS_BY_ID_USER)) {
            ps.setInt(1, categoryId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to check category ownership: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByNameAndUserId(String name, int userId) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_EXISTS_BY_NAME_USER)) {
            ps.setString(1, name);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to check category name uniqueness: " + e.getMessage(), e);
        }
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    private Category mapRow(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setCategoryId(rs.getInt("category_id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        c.setUserId(rs.getInt("user_id"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            c.setCreatedAt(createdAt.toLocalDateTime());
        }
        return c;
    }
}
