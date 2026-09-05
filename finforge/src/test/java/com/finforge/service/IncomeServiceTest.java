package com.finforge.service;

import com.finforge.dao.IncomeDAO;
import com.finforge.dto.IncomeDTO;
import com.finforge.exception.ValidationException;
import com.finforge.model.Income;
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
 * Unit tests for {@link IncomeServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("IncomeService Tests")
class IncomeServiceTest {

    @Mock private IncomeDAO incomeDAO;

    private IncomeService incomeService;

    @BeforeEach
    void setUp() {
        incomeService = new IncomeServiceImpl(incomeDAO);
    }

    // -----------------------------------------------------------------------
    // addIncome()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("addIncome() - should save income and return it when DTO is valid")
    void addIncome_shouldSave_whenValid() throws Exception {
        IncomeDTO dto   = validIncomeDTO();
        Income    saved = buildIncome(7);

        when(incomeDAO.save(any(Income.class))).thenReturn(saved);

        Income result = incomeService.addIncome(1, dto);

        assertNotNull(result);
        assertEquals(7, result.getIncomeId());
        verify(incomeDAO).save(any(Income.class));
    }

    @Test
    @DisplayName("addIncome() - should throw ValidationException when source is blank")
    void addIncome_shouldThrow_whenSourceBlank() {
        IncomeDTO dto = validIncomeDTO();
        dto.setSource("  ");

        assertThrows(ValidationException.class, () -> incomeService.addIncome(1, dto));
        verifyNoInteractions(incomeDAO);
    }

    @Test
    @DisplayName("addIncome() - should throw ValidationException when amount is zero")
    void addIncome_shouldThrow_whenAmountZero() {
        IncomeDTO dto = validIncomeDTO();
        dto.setAmount("0.00");

        assertThrows(ValidationException.class, () -> incomeService.addIncome(1, dto));
    }

    @Test
    @DisplayName("addIncome() - should throw ValidationException when amount is negative")
    void addIncome_shouldThrow_whenAmountNegative() {
        IncomeDTO dto = validIncomeDTO();
        dto.setAmount("-100");

        assertThrows(ValidationException.class, () -> incomeService.addIncome(1, dto));
    }

    @Test
    @DisplayName("addIncome() - should throw ValidationException when date is blank")
    void addIncome_shouldThrow_whenDateBlank() {
        IncomeDTO dto = validIncomeDTO();
        dto.setIncomeDate("");

        assertThrows(ValidationException.class, () -> incomeService.addIncome(1, dto));
    }

    @Test
    @DisplayName("addIncome() - should throw ValidationException when date format invalid")
    void addIncome_shouldThrow_whenDateInvalid() {
        IncomeDTO dto = validIncomeDTO();
        dto.setIncomeDate("15-07-2025"); // wrong format

        assertThrows(ValidationException.class, () -> incomeService.addIncome(1, dto));
    }

    // -----------------------------------------------------------------------
    // updateIncome()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateIncome() - should update when income belongs to user")
    void updateIncome_shouldUpdate_whenOwned() throws Exception {
        IncomeDTO dto = validIncomeDTO();
        dto.setIncomeId("7");

        when(incomeDAO.existsByIdAndUserId(7, 1)).thenReturn(true);

        incomeService.updateIncome(1, dto);

        verify(incomeDAO).update(any(Income.class));
    }

    @Test
    @DisplayName("updateIncome() - should throw ValidationException when income not owned")
    void updateIncome_shouldThrow_whenNotOwned() throws Exception {
        IncomeDTO dto = validIncomeDTO();
        dto.setIncomeId("7");

        when(incomeDAO.existsByIdAndUserId(7, 1)).thenReturn(false);

        assertThrows(ValidationException.class, () -> incomeService.updateIncome(1, dto));
        verify(incomeDAO, never()).update(any());
    }

    // -----------------------------------------------------------------------
    // deleteIncome()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteIncome() - should delete when income belongs to user")
    void deleteIncome_shouldDelete_whenOwned() throws Exception {
        when(incomeDAO.existsByIdAndUserId(7, 1)).thenReturn(true);

        incomeService.deleteIncome(7, 1);

        verify(incomeDAO).delete(7);
    }

    @Test
    @DisplayName("deleteIncome() - should throw ValidationException when not owned by user")
    void deleteIncome_shouldThrow_whenNotOwned() throws Exception {
        when(incomeDAO.existsByIdAndUserId(7, 1)).thenReturn(false);

        assertThrows(ValidationException.class, () -> incomeService.deleteIncome(7, 1));
        verify(incomeDAO, never()).delete(anyInt());
    }

    // -----------------------------------------------------------------------
    // getAllIncomes()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getAllIncomes() - should return all income records for user")
    void getAllIncomes_shouldReturnList() throws Exception {
        when(incomeDAO.findAllByUserId(1))
                .thenReturn(Arrays.asList(buildIncome(1), buildIncome(2)));

        assertEquals(2, incomeService.getAllIncomes(1).size());
    }

    @Test
    @DisplayName("getAllIncomes() - should return empty list when no records exist")
    void getAllIncomes_shouldReturnEmpty_whenNone() throws Exception {
        when(incomeDAO.findAllByUserId(1)).thenReturn(Collections.emptyList());

        assertTrue(incomeService.getAllIncomes(1).isEmpty());
    }

    // -----------------------------------------------------------------------
    // getIncomeById()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getIncomeById() - should throw ValidationException when income not owned")
    void getIncomeById_shouldThrow_whenNotOwned() throws Exception {
        Income income = buildIncome(5);
        income.setUserId(99); // Different user

        when(incomeDAO.findById(5)).thenReturn(Optional.of(income));

        assertThrows(ValidationException.class, () -> incomeService.getIncomeById(5, 1));
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private IncomeDTO validIncomeDTO() {
        IncomeDTO dto = new IncomeDTO();
        dto.setSource("Salary");
        dto.setAmount("5000.00");
        dto.setIncomeDate("2025-07-01");
        return dto;
    }

    private Income buildIncome(int id) {
        Income i = new Income();
        i.setIncomeId(id);
        i.setSource("Salary");
        i.setAmount(BigDecimal.valueOf(5000));
        i.setIncomeDate(LocalDate.of(2025, 7, 1));
        i.setUserId(1);
        return i;
    }
}
