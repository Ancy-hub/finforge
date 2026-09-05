package com.finforge.service;

import com.finforge.dao.ReportDAO;
import com.finforge.dto.CategoryReportDTO;
import com.finforge.dto.MonthlyReportDTO;
import com.finforge.dto.ReportDTO;
import com.finforge.exception.DAOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ReportServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService Tests")
class ReportServiceTest {

    @Mock private ReportDAO reportDAO;

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportServiceImpl(reportDAO);
    }

    // -----------------------------------------------------------------------
    // generateReport() — totals
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("generateReport() - should return correct totals when income and expense exist")
    void generateReport_shouldReturnCorrectTotals() throws Exception {
        // Arrange
        when(reportDAO.getTotalIncome(1)).thenReturn(new BigDecimal("8000.00"));
        when(reportDAO.getTotalExpense(1)).thenReturn(new BigDecimal("3500.50"));
        when(reportDAO.getMonthlyExpenses(1)).thenReturn(Collections.emptyList());
        when(reportDAO.getCategoryExpenses(1)).thenReturn(Collections.emptyList());

        // Act
        ReportDTO report = reportService.generateReport(1);

        // Assert
        assertEquals(new BigDecimal("8000.00"), report.getTotalIncome());
        assertEquals(new BigDecimal("3500.50"), report.getTotalExpense());
        assertEquals(new BigDecimal("4499.50"), report.getNetSavings());
    }

    @Test
    @DisplayName("generateReport() - net savings should be negative when expense exceeds income")
    void generateReport_netSavingsShouldBeNegative_whenExpenseExceedsIncome() throws Exception {
        // Arrange
        when(reportDAO.getTotalIncome(1)).thenReturn(new BigDecimal("2000.00"));
        when(reportDAO.getTotalExpense(1)).thenReturn(new BigDecimal("3000.00"));
        when(reportDAO.getMonthlyExpenses(1)).thenReturn(Collections.emptyList());
        when(reportDAO.getCategoryExpenses(1)).thenReturn(Collections.emptyList());

        // Act
        ReportDTO report = reportService.generateReport(1);

        // Assert
        assertTrue(report.getNetSavings().compareTo(BigDecimal.ZERO) < 0);
        assertEquals(new BigDecimal("-1000.00"), report.getNetSavings());
    }

    @Test
    @DisplayName("generateReport() - net savings should be zero when income equals expense")
    void generateReport_netSavingsShouldBeZero_whenIncomeEqualsExpense() throws Exception {
        // Arrange
        when(reportDAO.getTotalIncome(1)).thenReturn(new BigDecimal("5000.00"));
        when(reportDAO.getTotalExpense(1)).thenReturn(new BigDecimal("5000.00"));
        when(reportDAO.getMonthlyExpenses(1)).thenReturn(Collections.emptyList());
        when(reportDAO.getCategoryExpenses(1)).thenReturn(Collections.emptyList());

        // Act
        ReportDTO report = reportService.generateReport(1);

        // Assert
        assertEquals(0, report.getNetSavings().compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("generateReport() - should return zero totals when user has no data")
    void generateReport_shouldReturnZeroTotals_whenNoData() throws Exception {
        // Arrange
        when(reportDAO.getTotalIncome(1)).thenReturn(BigDecimal.ZERO);
        when(reportDAO.getTotalExpense(1)).thenReturn(BigDecimal.ZERO);
        when(reportDAO.getMonthlyExpenses(1)).thenReturn(Collections.emptyList());
        when(reportDAO.getCategoryExpenses(1)).thenReturn(Collections.emptyList());

        // Act
        ReportDTO report = reportService.generateReport(1);

        // Assert
        assertEquals(BigDecimal.ZERO, report.getTotalIncome());
        assertEquals(BigDecimal.ZERO, report.getTotalExpense());
        assertEquals(0, BigDecimal.ZERO.compareTo(report.getNetSavings()));
        assertTrue(report.getMonthlyExpenses().isEmpty());
        assertTrue(report.getCategoryExpenses().isEmpty());
    }

    // -----------------------------------------------------------------------
    // generateReport() — monthly breakdown
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("generateReport() - should include monthly expense breakdown")
    void generateReport_shouldIncludeMonthlyBreakdown() throws Exception {
        // Arrange
        List<MonthlyReportDTO> monthly = Arrays.asList(
                new MonthlyReportDTO("2025-05", new BigDecimal("1200.00")),
                new MonthlyReportDTO("2025-06", new BigDecimal("980.50")),
                new MonthlyReportDTO("2025-07", new BigDecimal("1540.00"))
        );

        when(reportDAO.getTotalIncome(1)).thenReturn(new BigDecimal("10000.00"));
        when(reportDAO.getTotalExpense(1)).thenReturn(new BigDecimal("3720.50"));
        when(reportDAO.getMonthlyExpenses(1)).thenReturn(monthly);
        when(reportDAO.getCategoryExpenses(1)).thenReturn(Collections.emptyList());

        // Act
        ReportDTO report = reportService.generateReport(1);

        // Assert
        assertEquals(3, report.getMonthlyExpenses().size());
        assertEquals("2025-05", report.getMonthlyExpenses().get(0).getMonth());
        assertEquals(new BigDecimal("1200.00"), report.getMonthlyExpenses().get(0).getTotalAmount());
        assertEquals("2025-07", report.getMonthlyExpenses().get(2).getMonth());
    }

    // -----------------------------------------------------------------------
    // generateReport() — category breakdown
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("generateReport() - should include category-wise expense breakdown")
    void generateReport_shouldIncludeCategoryBreakdown() throws Exception {
        // Arrange
        List<CategoryReportDTO> categories = Arrays.asList(
                new CategoryReportDTO("Food",          new BigDecimal("800.00")),
                new CategoryReportDTO("Travel",        new BigDecimal("650.00")),
                new CategoryReportDTO("Entertainment", new BigDecimal("200.00"))
        );

        when(reportDAO.getTotalIncome(1)).thenReturn(new BigDecimal("5000.00"));
        when(reportDAO.getTotalExpense(1)).thenReturn(new BigDecimal("1650.00"));
        when(reportDAO.getMonthlyExpenses(1)).thenReturn(Collections.emptyList());
        when(reportDAO.getCategoryExpenses(1)).thenReturn(categories);

        // Act
        ReportDTO report = reportService.generateReport(1);

        // Assert
        assertEquals(3, report.getCategoryExpenses().size());
        assertEquals("Food",   report.getCategoryExpenses().get(0).getCategoryName());
        assertEquals(new BigDecimal("800.00"), report.getCategoryExpenses().get(0).getTotalAmount());
    }

    // -----------------------------------------------------------------------
    // generateReport() — DAO calls verification
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("generateReport() - should call all four DAO methods exactly once")
    void generateReport_shouldCallAllDaoMethodsOnce() throws Exception {
        // Arrange
        when(reportDAO.getTotalIncome(1)).thenReturn(BigDecimal.ZERO);
        when(reportDAO.getTotalExpense(1)).thenReturn(BigDecimal.ZERO);
        when(reportDAO.getMonthlyExpenses(1)).thenReturn(Collections.emptyList());
        when(reportDAO.getCategoryExpenses(1)).thenReturn(Collections.emptyList());

        // Act
        reportService.generateReport(1);

        // Assert
        verify(reportDAO, times(1)).getTotalIncome(1);
        verify(reportDAO, times(1)).getTotalExpense(1);
        verify(reportDAO, times(1)).getMonthlyExpenses(1);
        verify(reportDAO, times(1)).getCategoryExpenses(1);
        verifyNoMoreInteractions(reportDAO);
    }

    // -----------------------------------------------------------------------
    // generateReport() — DAOException propagation
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("generateReport() - should propagate DAOException from getTotalIncome")
    void generateReport_shouldPropagate_whenTotalIncomeThrows() throws Exception {
        when(reportDAO.getTotalIncome(1)).thenThrow(new DAOException("DB failure"));

        assertThrows(DAOException.class, () -> reportService.generateReport(1));
    }

    @Test
    @DisplayName("generateReport() - should propagate DAOException from getCategoryExpenses")
    void generateReport_shouldPropagate_whenCategoryExpensesThrows() throws Exception {
        when(reportDAO.getTotalIncome(1)).thenReturn(BigDecimal.ZERO);
        when(reportDAO.getTotalExpense(1)).thenReturn(BigDecimal.ZERO);
        when(reportDAO.getMonthlyExpenses(1)).thenReturn(Collections.emptyList());
        when(reportDAO.getCategoryExpenses(1)).thenThrow(new DAOException("DB failure"));

        assertThrows(DAOException.class, () -> reportService.generateReport(1));
    }

    // -----------------------------------------------------------------------
    // generateReport() — different user IDs are scoped correctly
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("generateReport() - should use the correct userId for all DAO calls")
    void generateReport_shouldUseCorrectUserId() throws Exception {
        // Arrange
        int userId = 42;
        when(reportDAO.getTotalIncome(userId)).thenReturn(new BigDecimal("3000.00"));
        when(reportDAO.getTotalExpense(userId)).thenReturn(new BigDecimal("1000.00"));
        when(reportDAO.getMonthlyExpenses(userId)).thenReturn(Collections.emptyList());
        when(reportDAO.getCategoryExpenses(userId)).thenReturn(Collections.emptyList());

        // Act
        reportService.generateReport(userId);

        // Assert — all DAO calls must use userId=42, never another ID
        verify(reportDAO).getTotalIncome(42);
        verify(reportDAO).getTotalExpense(42);
        verify(reportDAO).getMonthlyExpenses(42);
        verify(reportDAO).getCategoryExpenses(42);
    }
}
