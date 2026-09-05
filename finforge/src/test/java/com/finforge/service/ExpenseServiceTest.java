package com.finforge.service;

import com.finforge.dao.ExpenseDAO;
import com.finforge.dto.ExpenseDTO;
import com.finforge.exception.DAOException;
import com.finforge.exception.ValidationException;
import com.finforge.model.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ExpenseServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ExpenseService Tests")
class ExpenseServiceTest {

    @Mock private ExpenseDAO expenseDAO;

    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        expenseService = new ExpenseServiceImpl(expenseDAO);
    }

    // -----------------------------------------------------------------------
    // addExpense()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("addExpense() - should save expense and return it when DTO is valid")
    void addExpense_shouldSave_whenDtoValid() throws Exception {
        // Arrange
        ExpenseDTO dto   = validExpenseDTO();
        Expense    saved = buildExpense(10);

        when(expenseDAO.save(any(Expense.class))).thenReturn(saved);

        // Act
        Expense result = expenseService.addExpense(1, dto);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getExpenseId());
        verify(expenseDAO).save(any(Expense.class));
    }

    @Test
    @DisplayName("addExpense() - should throw ValidationException when title is blank")
    void addExpense_shouldThrow_whenTitleBlank() {
        ExpenseDTO dto = validExpenseDTO();
        dto.setTitle("");

        assertThrows(ValidationException.class, () -> expenseService.addExpense(1, dto));
        verifyNoInteractions(expenseDAO);
    }

    @Test
    @DisplayName("addExpense() - should throw ValidationException when amount is negative")
    void addExpense_shouldThrow_whenAmountNegative() {
        ExpenseDTO dto = validExpenseDTO();
        dto.setAmount("-50");

        assertThrows(ValidationException.class, () -> expenseService.addExpense(1, dto));
    }

    @Test
    @DisplayName("addExpense() - should throw ValidationException when amount is zero")
    void addExpense_shouldThrow_whenAmountZero() {
        ExpenseDTO dto = validExpenseDTO();
        dto.setAmount("0");

        assertThrows(ValidationException.class, () -> expenseService.addExpense(1, dto));
    }

    @Test
    @DisplayName("addExpense() - should throw ValidationException when date is invalid")
    void addExpense_shouldThrow_whenDateInvalid() {
        ExpenseDTO dto = validExpenseDTO();
        dto.setExpenseDate("not-a-date");

        assertThrows(ValidationException.class, () -> expenseService.addExpense(1, dto));
    }

    // -----------------------------------------------------------------------
    // updateExpense()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateExpense() - should update expense when it belongs to user")
    void updateExpense_shouldUpdate_whenOwned() throws Exception {
        ExpenseDTO dto = validExpenseDTO();
        dto.setExpenseId("10");

        when(expenseDAO.existsByIdAndUserId(10, 1)).thenReturn(true);

        expenseService.updateExpense(1, dto);

        verify(expenseDAO).update(any(Expense.class));
    }

    @Test
    @DisplayName("updateExpense() - should throw ValidationException when expense not owned by user")
    void updateExpense_shouldThrow_whenNotOwned() throws Exception {
        ExpenseDTO dto = validExpenseDTO();
        dto.setExpenseId("10");

        when(expenseDAO.existsByIdAndUserId(10, 1)).thenReturn(false);

        assertThrows(ValidationException.class, () -> expenseService.updateExpense(1, dto));
        verify(expenseDAO, never()).update(any());
    }

    // -----------------------------------------------------------------------
    // deleteExpense()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteExpense() - should delete expense when it belongs to user")
    void deleteExpense_shouldDelete_whenOwned() throws Exception {
        when(expenseDAO.existsByIdAndUserId(5, 1)).thenReturn(true);

        expenseService.deleteExpense(5, 1);

        verify(expenseDAO).delete(5);
    }

    @Test
    @DisplayName("deleteExpense() - should throw ValidationException when not owned by user")
    void deleteExpense_shouldThrow_whenNotOwned() throws Exception {
        when(expenseDAO.existsByIdAndUserId(5, 1)).thenReturn(false);

        assertThrows(ValidationException.class, () -> expenseService.deleteExpense(5, 1));
        verify(expenseDAO, never()).delete(anyInt());
    }

    // -----------------------------------------------------------------------
    // getExpenseById()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getExpenseById() - should return expense when it belongs to user")
    void getExpenseById_shouldReturn_whenOwned() throws Exception {
        Expense expense = buildExpense(5);
        expense.setUserId(1);

        when(expenseDAO.findById(5)).thenReturn(Optional.of(expense));

        Expense result = expenseService.getExpenseById(5, 1);

        assertNotNull(result);
        assertEquals(5, result.getExpenseId());
    }

    @Test
    @DisplayName("getExpenseById() - should throw ValidationException when not owned by user")
    void getExpenseById_shouldThrow_whenNotOwned() throws Exception {
        Expense expense = buildExpense(5);
        expense.setUserId(99); // Different user

        when(expenseDAO.findById(5)).thenReturn(Optional.of(expense));

        assertThrows(ValidationException.class, () -> expenseService.getExpenseById(5, 1));
    }

    // -----------------------------------------------------------------------
    // getAllExpenses()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getAllExpenses() - should return all expenses for the user")
    void getAllExpenses_shouldReturnList() throws Exception {
        List<Expense> list = Arrays.asList(buildExpense(1), buildExpense(2));
        when(expenseDAO.findAllByUserId(1)).thenReturn(list);

        List<Expense> result = expenseService.getAllExpenses(1);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("getAllExpenses() - should return empty list when user has no expenses")
    void getAllExpenses_shouldReturnEmptyList_whenNone() throws Exception {
        when(expenseDAO.findAllByUserId(1)).thenReturn(Collections.emptyList());

        assertTrue(expenseService.getAllExpenses(1).isEmpty());
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private ExpenseDTO validExpenseDTO() {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setTitle("Grocery");
        dto.setDescription("Weekly shopping");
        dto.setAmount("150.00");
        dto.setCategoryId("1");
        dto.setExpenseDate("2025-06-15");
        return dto;
    }

    private Expense buildExpense(int id) {
        Expense e = new Expense();
        e.setExpenseId(id);
        e.setTitle("Grocery");
        e.setAmount(BigDecimal.valueOf(150));
        e.setCategoryId(1);
        e.setUserId(1);
        e.setExpenseDate(LocalDate.of(2025, 6, 15));
        return e;
    }
}
