package com.finforge.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain model representing a single expense entry.
 */
@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private int expenseId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "category_id", nullable = false)
    private int categoryId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** Denormalized field: populated via JOIN or service for display purposes. */
    @Transient
    private String categoryName;

    public Expense() {}

    public Expense(int expenseId, String title, String description,
                    BigDecimal amount, int categoryId, int userId,
                    LocalDate expenseDate, LocalDateTime createdAt,
                    LocalDateTime updatedAt) {
        this.expenseId   = expenseId;
        this.title       = title;
        this.description = description;
        this.amount      = amount;
        this.categoryId  = categoryId;
        this.userId      = userId;
        this.expenseDate = expenseDate;
        this.createdAt   = createdAt;
        this.updatedAt   = updatedAt;
    }

    // ---- Getters and Setters ----

    public int getExpenseId()                      { return expenseId; }
    public void setExpenseId(int expenseId)        { this.expenseId = expenseId; }

    public String getTitle()                       { return title; }
    public void setTitle(String title)             { this.title = title; }

    public String getDescription()                 { return description; }
    public void setDescription(String d)           { this.description = d; }

    public BigDecimal getAmount()                  { return amount; }
    public void setAmount(BigDecimal amount)       { this.amount = amount; }

    public int getCategoryId()                     { return categoryId; }
    public void setCategoryId(int categoryId)      { this.categoryId = categoryId; }

    public int getUserId()                         { return userId; }
    public void setUserId(int userId)              { this.userId = userId; }

    public LocalDate getExpenseDate()              { return expenseDate; }
    public void setExpenseDate(LocalDate d)        { this.expenseDate = d; }

    public LocalDateTime getCreatedAt()            { return createdAt; }
    public void setCreatedAt(LocalDateTime t)      { this.createdAt = t; }

    public LocalDateTime getUpdatedAt()            { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t)      { this.updatedAt = t; }

    public String getCategoryName()                { return categoryName; }
    public void setCategoryName(String n)          { this.categoryName = n; }

    @Override
    public String toString() {
        return "Expense{expenseId=" + expenseId + ", title='" + title
                + "', amount=" + amount + ", date=" + expenseDate + "}";
    }
}
