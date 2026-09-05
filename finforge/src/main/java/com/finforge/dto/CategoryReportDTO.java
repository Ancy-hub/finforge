package com.finforge.dto;

import java.math.BigDecimal;

/**
 * Row in the Category-wise Expense Summary report.
 */
public class CategoryReportDTO {

    private String     categoryName;
    private BigDecimal totalAmount;

    public CategoryReportDTO() {}

    public CategoryReportDTO(String categoryName, BigDecimal totalAmount) {
        this.categoryName = categoryName;
        this.totalAmount  = totalAmount;
    }

    // ---- Getters and Setters ----

    public String getCategoryName()                { return categoryName; }
    public void setCategoryName(String name)       { this.categoryName = name; }

    public BigDecimal getTotalAmount()             { return totalAmount; }
    public void setTotalAmount(BigDecimal amount)  { this.totalAmount = amount; }
}
