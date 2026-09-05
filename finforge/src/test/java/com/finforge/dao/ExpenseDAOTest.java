package com.finforge.dao;

import com.finforge.exception.DAOException;
import com.finforge.model.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ExpenseDAOImpl} using Mockito to mock JDBC objects.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ExpenseDAO Tests")
class ExpenseDAOTest {

    @Mock private Connection        connection;
    @Mock private PreparedStatement preparedStatement;
    @Mock private ResultSet         resultSet;

    private ExpenseDAO expenseDAO;

    @BeforeEach
    void setUp() {
        expenseDAO = new ExpenseDAOImpl(connection);
    }

    // -----------------------------------------------------------------------
    // save()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("save() - should insert expense and return with generated ID")
    void save_shouldInsertAndReturnGeneratedId() throws Exception {
        // Arrange
        Expense expense   = buildExpense();
        ResultSet genKeys = mock(ResultSet.class);

        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(genKeys);
        when(genKeys.next()).thenReturn(true);
        when(genKeys.getInt(1)).thenReturn(10);

        // Act
        Expense saved = expenseDAO.save(expense);

        // Assert
        assertEquals(10, saved.getExpenseId());
        verify(preparedStatement).setString(1, "Grocery");
        verify(preparedStatement).setBigDecimal(3, BigDecimal.valueOf(150.00));
    }

    @Test
    @DisplayName("save() - should throw DAOException when no rows affected")
    void save_shouldThrow_whenNoRowsAffected() throws Exception {
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        assertThrows(DAOException.class, () -> expenseDAO.save(buildExpense()));
    }

    // -----------------------------------------------------------------------
    // findById()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("findById() - should return populated Optional when expense exists")
    void findById_shouldReturnExpense_whenExists() throws Exception {
        mockResultSetForExpense();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        Optional<Expense> result = expenseDAO.findById(1);

        assertTrue(result.isPresent());
        assertEquals("Grocery", result.get().getTitle());
        assertEquals(0, BigDecimal.valueOf(150).compareTo(result.get().getAmount()));
    }

    @Test
    @DisplayName("findById() - should return empty Optional when not found")
    void findById_shouldReturnEmpty_whenNotFound() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Optional<Expense> result = expenseDAO.findById(999);

        assertFalse(result.isPresent());
    }

    // -----------------------------------------------------------------------
    // findAllByUserId()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("findAllByUserId() - should return list of expenses for user")
    void findAllByUserId_shouldReturnList() throws Exception {
        mockResultSetForExpense();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        // Two rows, then end
        when(resultSet.next()).thenReturn(true, true, false);

        List<Expense> expenses = expenseDAO.findAllByUserId(1);

        assertEquals(2, expenses.size());
    }

    @Test
    @DisplayName("findAllByUserId() - should return empty list when no expenses")
    void findAllByUserId_shouldReturnEmptyList_whenNoExpenses() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        List<Expense> expenses = expenseDAO.findAllByUserId(1);

        assertTrue(expenses.isEmpty());
    }

    // -----------------------------------------------------------------------
    // delete()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("delete() - should execute DELETE statement")
    void delete_shouldExecuteDelete() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        expenseDAO.delete(5);

        verify(preparedStatement).setInt(1, 5);
        verify(preparedStatement).executeUpdate();
    }

    // -----------------------------------------------------------------------
    // existsByIdAndUserId()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("existsByIdAndUserId() - should return true when record belongs to user")
    void existsByIdAndUserId_shouldReturnTrue_whenBelongsToUser() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        assertTrue(expenseDAO.existsByIdAndUserId(1, 1));
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private Expense buildExpense() {
        Expense e = new Expense();
        e.setTitle("Grocery");
        e.setDescription("Weekly grocery");
        e.setAmount(BigDecimal.valueOf(150.00));
        e.setCategoryId(1);
        e.setUserId(1);
        e.setExpenseDate(LocalDate.of(2025, 6, 15));
        return e;
    }

    private void mockResultSetForExpense() throws SQLException {
        when(resultSet.getInt("expense_id")).thenReturn(1);
        when(resultSet.getString("title")).thenReturn("Grocery");
        when(resultSet.getString("description")).thenReturn("Weekly grocery");
        when(resultSet.getBigDecimal("amount")).thenReturn(BigDecimal.valueOf(150));
        when(resultSet.getInt("category_id")).thenReturn(1);
        when(resultSet.getInt("user_id")).thenReturn(1);
        when(resultSet.getDate("expense_date")).thenReturn(Date.valueOf(LocalDate.of(2025, 6, 15)));
        when(resultSet.getTimestamp("created_at")).thenReturn(null);
        when(resultSet.getTimestamp("updated_at")).thenReturn(null);
        when(resultSet.getString("category_name")).thenReturn("Food");
    }
}
