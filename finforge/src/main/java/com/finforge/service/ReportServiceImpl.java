package com.finforge.service;

import com.finforge.dao.ReportDAO;
import com.finforge.dto.CategoryReportDTO;
import com.finforge.dto.MonthlyReportDTO;
import com.finforge.dto.ReportDTO;
import com.finforge.exception.DAOException;
import com.finforge.model.Category;
import com.finforge.model.Expense;
import com.finforge.repository.CategoryRepository;
import com.finforge.repository.ExpenseRepository;
import com.finforge.repository.IncomeRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Business-logic implementation for report generation using Spring Data JPA.
 */
@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private static final Logger logger = LogManager.getLogger(ReportServiceImpl.class);
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final CategoryRepository categoryRepository;
    private final ReportDAO reportDAO;

    @Autowired
    public ReportServiceImpl(ExpenseRepository expenseRepository,
                             IncomeRepository incomeRepository,
                             CategoryRepository categoryRepository) {
        this.expenseRepository  = expenseRepository;
        this.incomeRepository   = incomeRepository;
        this.categoryRepository = categoryRepository;
        this.reportDAO          = null;
    }

    public ReportServiceImpl(ReportDAO reportDAO) {
        this.expenseRepository  = null;
        this.incomeRepository   = null;
        this.categoryRepository = null;
        this.reportDAO          = reportDAO;
    }

    @Override
    public ReportDTO generateReport(int userId) throws DAOException {
        logger.debug("Generating report for userId={}", userId);

        if (reportDAO != null) {
            BigDecimal totalIncome  = reportDAO.getTotalIncome(userId);
            BigDecimal totalExpense = reportDAO.getTotalExpense(userId);
            BigDecimal netSavings   = totalIncome.subtract(totalExpense);

            ReportDTO report = new ReportDTO();
            report.setTotalIncome(totalIncome);
            report.setTotalExpense(totalExpense);
            report.setNetSavings(netSavings);
            report.setMonthlyExpenses(reportDAO.getMonthlyExpenses(userId));
            report.setCategoryExpenses(reportDAO.getCategoryExpenses(userId));
            return report;
        }

        BigDecimal totalIncome = incomeRepository.sumAmountByUserId(userId);
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;

        BigDecimal totalExpense = expenseRepository.sumAmountByUserId(userId);
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        BigDecimal netSavings = totalIncome.subtract(totalExpense);

        List<Expense> expenses = expenseRepository.findByUserIdOrderByExpenseDateDesc(userId);
        List<Category> categories = categoryRepository.findByUserIdOrderByNameAsc(userId);
        Map<Integer, String> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getCategoryId, Category::getName, (a, b) -> a));

        // Aggregate monthly expenses
        Map<String, BigDecimal> monthlyTotals = new TreeMap<>();
        for (Expense e : expenses) {
            if (e.getExpenseDate() != null) {
                String month = e.getExpenseDate().format(MONTH_FORMATTER);
                monthlyTotals.merge(month, e.getAmount() != null ? e.getAmount() : BigDecimal.ZERO, BigDecimal::add);
            }
        }
        List<MonthlyReportDTO> monthlyReportList = monthlyTotals.entrySet().stream()
                .map(entry -> new MonthlyReportDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        // Aggregate category expenses
        Map<String, BigDecimal> categoryTotals = new LinkedHashMap<>();
        for (Category c : categories) {
            categoryTotals.put(c.getName(), BigDecimal.ZERO);
        }
        for (Expense e : expenses) {
            String catName = categoryMap.getOrDefault(e.getCategoryId(), "Uncategorized");
            categoryTotals.merge(catName, e.getAmount() != null ? e.getAmount() : BigDecimal.ZERO, BigDecimal::add);
        }
        List<CategoryReportDTO> categoryReportList = categoryTotals.entrySet().stream()
                .map(entry -> new CategoryReportDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()))
                .collect(Collectors.toList());

        ReportDTO report = new ReportDTO();
        report.setTotalIncome(totalIncome);
        report.setTotalExpense(totalExpense);
        report.setNetSavings(netSavings);
        report.setMonthlyExpenses(monthlyReportList);
        report.setCategoryExpenses(categoryReportList);
        return report;
    }
}
