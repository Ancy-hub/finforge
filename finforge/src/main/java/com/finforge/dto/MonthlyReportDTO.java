package com.finforge.dto;

import java.math.BigDecimal;

/**
 * Row in the Monthly Expense Summary report.
 * month is formatted as "YYYY-MM" (e.g. "2025-03").
 */
public class MonthlyReportDTO {

    private String     month;
    private BigDecimal totalAmount;

    public MonthlyReportDTO() {}

    public MonthlyReportDTO(String month, BigDecimal totalAmount) {
        this.month       = month;
        this.totalAmount = totalAmount;
    }

    // ---- Getters and Setters ----

    public String getMonth()                       { return month; }
    public void setMonth(String month)             { this.month = month; }

    public BigDecimal getTotalAmount()             { return totalAmount; }
    public void setTotalAmount(BigDecimal amount)  { this.totalAmount = amount; }
}
