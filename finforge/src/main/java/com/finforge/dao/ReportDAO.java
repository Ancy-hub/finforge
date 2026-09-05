package com.finforge.dao;

import com.finforge.dto.CategoryReportDTO;
import com.finforge.dto.MonthlyReportDTO;
import com.finforge.exception.DAOException;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Access Object interface for aggregated reporting queries.
 */
public interface ReportDAO {

    /** Returns the total income amount for a user (0 if no records). */
    BigDecimal getTotalIncome(int userId) throws DAOException;

    /** Returns the total expense amount for a user (0 if no records). */
    BigDecimal getTotalExpense(int userId) throws DAOException;

    /**
     * Returns month-by-month expense totals for a user.
     * Each row contains a month label in "YYYY-MM" format and the summed amount.
     * Results are ordered by month ascending.
     */
    List<MonthlyReportDTO> getMonthlyExpenses(int userId) throws DAOException;

    /**
     * Returns expense totals grouped by category for a user.
     * Results are ordered by total amount descending.
     */
    List<CategoryReportDTO> getCategoryExpenses(int userId) throws DAOException;
}
