package com.finforge.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain model representing a single income entry.
 */
public class Income {

    private int           incomeId;
    private String        source;
    private BigDecimal    amount;
    private LocalDate     incomeDate;
    private int           userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Income() {}

    public Income(int incomeId, String source, BigDecimal amount,
                  LocalDate incomeDate, int userId,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.incomeId   = incomeId;
        this.source     = source;
        this.amount     = amount;
        this.incomeDate = incomeDate;
        this.userId     = userId;
        this.createdAt  = createdAt;
        this.updatedAt  = updatedAt;
    }

    // ---- Getters and Setters ----

    public int getIncomeId()                     { return incomeId; }
    public void setIncomeId(int incomeId)        { this.incomeId = incomeId; }

    public String getSource()                    { return source; }
    public void setSource(String source)         { this.source = source; }

    public BigDecimal getAmount()                { return amount; }
    public void setAmount(BigDecimal amount)     { this.amount = amount; }

    public LocalDate getIncomeDate()             { return incomeDate; }
    public void setIncomeDate(LocalDate d)       { this.incomeDate = d; }

    public int getUserId()                       { return userId; }
    public void setUserId(int userId)            { this.userId = userId; }

    public LocalDateTime getCreatedAt()          { return createdAt; }
    public void setCreatedAt(LocalDateTime t)    { this.createdAt = t; }

    public LocalDateTime getUpdatedAt()          { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t)    { this.updatedAt = t; }

    @Override
    public String toString() {
        return "Income{incomeId=" + incomeId + ", source='" + source
                + "', amount=" + amount + ", date=" + incomeDate + "}";
    }
}
