package com.finforge.dao;

import com.finforge.exception.DAOException;
import com.finforge.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserDAOImpl} using Mockito to mock JDBC objects.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserDAO Tests")
class UserDAOTest {

    @Mock private Connection        connection;
    @Mock private PreparedStatement preparedStatement;
    @Mock private ResultSet         resultSet;

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAOImpl(connection);
    }

    // -----------------------------------------------------------------------
    // save()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("save() - should persist user and return generated ID")
    void save_shouldPersistUserAndReturnGeneratedId() throws Exception {
        // Arrange
        User user = buildUser();
        ResultSet generatedKeys = mock(ResultSet.class);

        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(42);

        // Act
        User saved = userDAO.save(user);

        // Assert
        assertEquals(42, saved.getUserId());
        verify(preparedStatement).setString(1, user.getUsername());
        verify(preparedStatement).setString(2, user.getEmail());
        verify(preparedStatement).setString(3, user.getPasswordHash());
    }

    @Test
    @DisplayName("save() - should throw DAOException when no rows affected")
    void save_shouldThrowDAOException_whenNoRowsAffected() throws Exception {
        // Arrange
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        // Act & Assert
        assertThrows(DAOException.class, () -> userDAO.save(buildUser()));
    }

    // -----------------------------------------------------------------------
    // findById()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("findById() - should return populated Optional when user exists")
    void findById_shouldReturnUser_whenExists() throws Exception {
        // Arrange
        mockResultSetForUser();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        // Act
        Optional<User> result = userDAO.findById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getUserId());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    @DisplayName("findById() - should return empty Optional when user not found")
    void findById_shouldReturnEmpty_whenNotFound() throws Exception {
        // Arrange
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act
        Optional<User> result = userDAO.findById(999);

        // Assert
        assertFalse(result.isPresent());
    }

    // -----------------------------------------------------------------------
    // findByUsername()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("findByUsername() - should return user when username matches")
    void findByUsername_shouldReturnUser_whenFound() throws Exception {
        // Arrange
        mockResultSetForUser();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        // Act
        Optional<User> result = userDAO.findByUsername("testuser");

        // Assert
        assertTrue(result.isPresent());
        verify(preparedStatement).setString(1, "testuser");
    }

    // -----------------------------------------------------------------------
    // existsByUsername()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("existsByUsername() - should return true when username exists")
    void existsByUsername_shouldReturnTrue_whenExists() throws Exception {
        // Arrange
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        // Act
        boolean exists = userDAO.existsByUsername("testuser");

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("existsByUsername() - should return false when username does not exist")
    void existsByUsername_shouldReturnFalse_whenNotExists() throws Exception {
        // Arrange
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(0);

        // Act
        boolean exists = userDAO.existsByUsername("nonexistent");

        // Assert
        assertFalse(exists);
    }

    // -----------------------------------------------------------------------
    // updatePassword()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updatePassword() - should call executeUpdate with correct params")
    void updatePassword_shouldExecuteUpdate() throws Exception {
        // Arrange
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Act
        userDAO.updatePassword(1, "newHash");

        // Assert
        verify(preparedStatement).setString(1, "newHash");
        verify(preparedStatement).setInt(2, 1);
        verify(preparedStatement).executeUpdate();
    }

    // -----------------------------------------------------------------------
    // findById() - SQLException propagation
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("findById() - should throw DAOException on SQLException")
    void findById_shouldThrowDAOException_onSQLException() throws Exception {
        // Arrange
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        // Act & Assert
        assertThrows(DAOException.class, () -> userDAO.findById(1));
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private User buildUser() {
        User u = new User();
        u.setUsername("testuser");
        u.setEmail("test@example.com");
        u.setPasswordHash("hashvalue");
        u.setFirstName("Test");
        u.setLastName("User");
        u.setActive(true);
        return u;
    }

    private void mockResultSetForUser() throws SQLException {
        when(resultSet.getInt("user_id")).thenReturn(1);
        when(resultSet.getString("username")).thenReturn("testuser");
        when(resultSet.getString("email")).thenReturn("test@example.com");
        when(resultSet.getString("password_hash")).thenReturn("hashvalue");
        when(resultSet.getString("first_name")).thenReturn("Test");
        when(resultSet.getString("last_name")).thenReturn("User");
        when(resultSet.getString("phone")).thenReturn(null);
        when(resultSet.getTimestamp("created_at")).thenReturn(null);
        when(resultSet.getTimestamp("updated_at")).thenReturn(null);
        when(resultSet.getBoolean("is_active")).thenReturn(true);
    }
}
