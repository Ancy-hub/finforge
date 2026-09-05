package com.finforge.dao;

import com.finforge.exception.DAOException;
import com.finforge.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CategoryDAOImpl} using Mockito to mock JDBC objects.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryDAO Tests")
class CategoryDAOTest {

    @Mock private Connection        connection;
    @Mock private PreparedStatement preparedStatement;
    @Mock private ResultSet         resultSet;

    private CategoryDAO categoryDAO;

    @BeforeEach
    void setUp() {
        categoryDAO = new CategoryDAOImpl(connection);
    }

    // -----------------------------------------------------------------------
    // save()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("save() - should insert category and return with generated ID")
    void save_shouldInsertAndReturnGeneratedId() throws Exception {
        Category  cat     = buildCategory();
        ResultSet genKeys = mock(ResultSet.class);

        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(genKeys);
        when(genKeys.next()).thenReturn(true);
        when(genKeys.getInt(1)).thenReturn(5);

        Category saved = categoryDAO.save(cat);

        assertEquals(5, saved.getCategoryId());
        verify(preparedStatement).setString(1, "Food");
        verify(preparedStatement).setInt(3, 1);
    }

    @Test
    @DisplayName("save() - should throw DAOException when no rows affected")
    void save_shouldThrow_whenNoRowsAffected() throws Exception {
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        assertThrows(DAOException.class, () -> categoryDAO.save(buildCategory()));
    }

    // -----------------------------------------------------------------------
    // findById()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("findById() - should return populated Optional when category exists")
    void findById_shouldReturnCategory_whenExists() throws Exception {
        mockResultSetForCategory();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        Optional<Category> result = categoryDAO.findById(1);

        assertTrue(result.isPresent());
        assertEquals("Food", result.get().getName());
    }

    @Test
    @DisplayName("findById() - should return empty Optional when not found")
    void findById_shouldReturnEmpty_whenNotFound() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        assertFalse(categoryDAO.findById(999).isPresent());
    }

    // -----------------------------------------------------------------------
    // findAllByUserId()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("findAllByUserId() - should return all categories for user")
    void findAllByUserId_shouldReturnAllCategories() throws Exception {
        mockResultSetForCategory();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, true, false);

        List<Category> cats = categoryDAO.findAllByUserId(1);

        assertEquals(3, cats.size());
        verify(preparedStatement).setInt(1, 1);
    }

    // -----------------------------------------------------------------------
    // update()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("update() - should execute UPDATE with correct params")
    void update_shouldExecuteUpdateWithCorrectParams() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        Category cat = buildCategory();
        cat.setCategoryId(3);
        categoryDAO.update(cat);

        verify(preparedStatement).setString(1, "Food");
        verify(preparedStatement).setInt(3, 3);
        verify(preparedStatement).setInt(4, 1);
        verify(preparedStatement).executeUpdate();
    }

    // -----------------------------------------------------------------------
    // delete()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("delete() - should execute DELETE for given ID")
    void delete_shouldExecuteDelete() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        categoryDAO.delete(3);

        verify(preparedStatement).setInt(1, 3);
        verify(preparedStatement).executeUpdate();
    }

    // -----------------------------------------------------------------------
    // existsByNameAndUserId()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("existsByNameAndUserId() - should return true when name already taken")
    void existsByNameAndUserId_shouldReturnTrue_whenNameExists() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        assertTrue(categoryDAO.existsByNameAndUserId("Food", 1));
    }

    @Test
    @DisplayName("existsByNameAndUserId() - should return false when name not taken")
    void existsByNameAndUserId_shouldReturnFalse_whenNameFree() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(0);

        assertFalse(categoryDAO.existsByNameAndUserId("NewCategory", 1));
    }

    // -----------------------------------------------------------------------
    // SQLException propagation
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("findAllByUserId() - should throw DAOException on SQLException")
    void findAllByUserId_shouldThrowDAOException_onSQLException() throws Exception {
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> categoryDAO.findAllByUserId(1));
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private Category buildCategory() {
        Category c = new Category();
        c.setName("Food");
        c.setDescription("Food expenses");
        c.setUserId(1);
        return c;
    }

    private void mockResultSetForCategory() throws SQLException {
        when(resultSet.getInt("category_id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("Food");
        when(resultSet.getString("description")).thenReturn("Food expenses");
        when(resultSet.getInt("user_id")).thenReturn(1);
        when(resultSet.getTimestamp("created_at")).thenReturn(null);
    }
}
