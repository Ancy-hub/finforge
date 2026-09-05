package com.finforge.dto;

/**
 * Data Transfer Object for expense add / edit forms.
 * All fields are Strings as received from HTTP request parameters.
 */
public class ExpenseDTO {

    private String expenseId;   // present only on edit
    private String title;
    private String description;
    private String amount;
    private String categoryId;
    private String expenseDate; // expected format: YYYY-MM-DD

    public ExpenseDTO() {}

    // ---- Getters and Setters ----

    public String getExpenseId()                    { return expenseId; }
    public void setExpenseId(String expenseId)      { this.expenseId = expenseId; }

    public String getTitle()                        { return title; }
    public void setTitle(String title)              { this.title = title; }

    public String getDescription()                  { return description; }
    public void setDescription(String d)            { this.description = d; }

    public String getAmount()                       { return amount; }
    public void setAmount(String amount)            { this.amount = amount; }

    public String getCategoryId()                   { return categoryId; }
    public void setCategoryId(String categoryId)    { this.categoryId = categoryId; }

    public String getExpenseDate()                  { return expenseDate; }
    public void setExpenseDate(String d)            { this.expenseDate = d; }
}
