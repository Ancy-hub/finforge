package com.finforge.dao;

import com.finforge.exception.DAOException;
import com.finforge.model.Income;
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
 * Unit tests for {@link IncomeDAOImpl} using Mockito to mock JDBC objects.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("IncomeDAO Tests")
class IncomeDAOTest {

    @Mock private Connection        connection;
    @Mock private PreparedStatement preparedStatement;
    @Mock private ResultSet         resultSet;

    private IncomeDAO incomeDAO;

    @BeforeEach
    void setUp() {
        incomeDAO = new IncomeDAOImpl(connection);
    }

    // -----------------------------------------------------------------------
    // save()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("save() - should insert income and return with generated ID")
    void save_shouldInsertAndReturnGeneratedId() throws Exception {
        Income    income  = buildIncome();
        ResultSet genKeys = mock(ResultSet.class);

        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(genKeys);
        when(genKeys.next()).thenReturn(true);
        when(genKeys.getInt(1)).thenReturn(20);

        Income saved = incomeDAO.save(income);

        assertEquals(20, saved.getIncomeId());
        verify(preparedStatement).setString(1, "Salary");
        verify(preparedStatement).setBigDecimal(2, BigDecimal.valueOf(5000));
    }

    @Test
    @DisplayName("save() - should throw DAOException when no rows affected")
    void save_shouldThrow_whenNoRowsAffected() throws Exception {
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        assertThrows(DAOException.class, () -> incomeDAO.save(buildIncome()));
    }

    // -----------------------------------------------------------------------
    // findById()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("findById() - should return populated Optional when income exists")
    void findById_shouldReturnIncome_whenExists() throws Exception {
        mockResultSetForIncome();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        Optional<Income> result = incomeDAO.findById(1);

        assertTrue(result.isPresent());
        assertEquals("Salary", result.get().getSource());
        assertEquals(0, BigDecimal.valueOf(5000).compareTo(result.get().getAmount()));
    }

    @Test
    @DisplayName("findById() - should return empty Optional when not found")
    void findById_shouldReturnEmpty_whenNotFound() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Optional<Income> result = incomeDAO.findById(999);

        assertFalse(result.isPresent());
    }

    // -----------------------------------------------------------------------
    // findAllByUserId()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("findAllByUserId() - should return list of income records")
    void findAllByUserId_shouldReturnList() throws Exception {
        mockResultSetForIncome();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);

        List<Income> incomes = incomeDAO.findAllByUserId(1);

        assertEquals(2, incomes.size());
    }

    @Test
    @DisplayName("findAllByUserId() - should return empty list when no records")
    void findAllByUserId_shouldReturnEmptyList_whenNone() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        assertTrue(incomeDAO.findAllByUserId(1).isEmpty());
    }

    // -----------------------------------------------------------------------
    // delete()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("delete() - should execute DELETE for the given ID")
    void delete_shouldExecuteDelete() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        incomeDAO.delete(7);

        verify(preparedStatement).setInt(1, 7);
        verify(preparedStatement).executeUpdate();
    }

    // -----------------------------------------------------------------------
    // existsByIdAndUserId()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("existsByIdAndUserId() - should return false when record does not belong to user")
    void existsByIdAndUserId_shouldReturnFalse_whenNotOwned() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(0);

        assertFalse(incomeDAO.existsByIdAndUserId(1, 999));
    }

    // -----------------------------------------------------------------------
    // SQLException propagation
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("findById() - should throw DAOException on SQLException")
    void findById_shouldThrowDAOException_onSQLException() throws Exception {
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB failure"));

        assertThrows(DAOException.class, () -> incomeDAO.findById(1));
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private Income buildIncome() {
        Income i = new Income();
        i.setSource("Salary");
        i.setAmount(BigDecimal.valueOf(5000));
        i.setIncomeDate(LocalDate.of(2025, 7, 1));
        i.setUserId(1);
        return i;
    }

    private void mockResultSetForIncome() throws SQLException {
        when(resultSet.getInt("income_id")).thenReturn(1);
        when(resultSet.getString("source")).thenReturn("Salary");
        when(resultSet.getBigDecimal("amount")).thenReturn(BigDecimal.valueOf(5000));
        when(resultSet.getDate("income_date")).thenReturn(Date.valueOf(LocalDate.of(2025, 7, 1)));
        when(resultSet.getInt("user_id")).thenReturn(1);
        when(resultSet.getTimestamp("created_at")).thenReturn(null);
        when(resultSet.getTimestamp("updated_at")).thenReturn(null);
    }
}
