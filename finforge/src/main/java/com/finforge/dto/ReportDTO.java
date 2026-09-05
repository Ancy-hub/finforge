package com.finforge.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Aggregated report data for the Report module.
 */
public class ReportDTO {

    private BigDecimal            totalIncome;
    private BigDecimal            totalExpense;
    private BigDecimal            netSavings;
    private List<MonthlyReportDTO>   monthlyExpenses;
    private List<CategoryReportDTO>  categoryExpenses;

    public ReportDTO() {}

    // ---- Getters and Setters ----

    public BigDecimal getTotalIncome()                           { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome)           { this.totalIncome = totalIncome; }

    public BigDecimal getTotalExpense()                          { return totalExpense; }
    public void setTotalExpense(BigDecimal totalExpense)         { this.totalExpense = totalExpense; }

    public BigDecimal getNetSavings()                            { return netSavings; }
    public void setNetSavings(BigDecimal netSavings)             { this.netSavings = netSavings; }

    public List<MonthlyReportDTO> getMonthlyExpenses()           { return monthlyExpenses; }
    public void setMonthlyExpenses(List<MonthlyReportDTO> list)  { this.monthlyExpenses = list; }

    public List<CategoryReportDTO> getCategoryExpenses()         { return categoryExpenses; }
    public void setCategoryExpenses(List<CategoryReportDTO> l)   { this.categoryExpenses = l; }
}
