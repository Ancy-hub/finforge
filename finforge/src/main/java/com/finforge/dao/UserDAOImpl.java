package com.finforge.dao;

import com.finforge.exception.DAOException;
import com.finforge.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * JDBC implementation of {@link UserDAO} backed by SQL Server.
 */
public class UserDAOImpl implements UserDAO {

    private final Connection connection;

    public UserDAOImpl(Connection connection) {
        this.connection = connection;
    }

    // -----------------------------------------------------------------------
    // SQL constants
    // -----------------------------------------------------------------------

    private static final String SQL_INSERT =
            "INSERT INTO users (username, email, password_hash, first_name, last_name, phone) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_ID =
            "SELECT user_id, username, email, password_hash, first_name, last_name, " +
            "       phone, created_at, updated_at, is_active " +
            "FROM users WHERE user_id = ?";

    private static final String SQL_FIND_BY_USERNAME =
            "SELECT user_id, username, email, password_hash, first_name, last_name, " +
            "       phone, created_at, updated_at, is_active " +
            "FROM users WHERE username = ?";

    private static final String SQL_FIND_BY_EMAIL =
            "SELECT user_id, username, email, password_hash, first_name, last_name, " +
            "       phone, created_at, updated_at, is_active " +
            "FROM users WHERE email = ?";

    private static final String SQL_UPDATE =
            "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone = ?, " +
            "updated_at = GETDATE() WHERE user_id = ?";

    private static final String SQL_UPDATE_PASSWORD =
            "UPDATE users SET password_hash = ?, updated_at = GETDATE() WHERE user_id = ?";

    private static final String SQL_EXISTS_USERNAME =
            "SELECT COUNT(1) FROM users WHERE username = ?";

    private static final String SQL_EXISTS_EMAIL =
            "SELECT COUNT(1) FROM users WHERE email = ?";

    // -----------------------------------------------------------------------
    // Implementations
    // -----------------------------------------------------------------------

    @Override
    public User save(User user) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(
                SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getFirstName());
            ps.setString(5, user.getLastName());
            ps.setString(6, user.getPhone());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DAOException("Inserting user failed — no rows affected.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setUserId(keys.getInt(1));
                } else {
                    throw new DAOException("Inserting user failed — no generated key returned.");
                }
            }
            return user;

        } catch (SQLException e) {
            throw new DAOException("Failed to save user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findById(int userId) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to find user by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_FIND_BY_USERNAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to find user by username: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_FIND_BY_EMAIL)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to find user by email: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public void update(User user) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            ps.setInt(5, user.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to update user: " + e.getMessage(), e);
        }
    }

    @Override
    public void updatePassword(int userId, String newPasswordHash) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE_PASSWORD)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to update password: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByUsername(String username) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_EXISTS_USERNAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to check username existence: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByEmail(String email) throws DAOException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_EXISTS_EMAIL)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to check email existence: " + e.getMessage(), e);
        }
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setPhone(rs.getString("phone"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        user.setActive(rs.getBoolean("is_active"));
        return user;
    }
}
