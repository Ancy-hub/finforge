package com.finforge.dto;

/**
 * Data Transfer Object for income add / edit forms.
 * All fields are Strings as received from HTTP request parameters.
 */
public class IncomeDTO {

    private String incomeId;    // present only on edit
    private String source;
    private String amount;
    private String incomeDate;  // expected format: YYYY-MM-DD

    public IncomeDTO() {}

    // ---- Getters and Setters ----

    public String getIncomeId()                 { return incomeId; }
    public void setIncomeId(String incomeId)    { this.incomeId = incomeId; }

    public String getSource()                   { return source; }
    public void setSource(String source)        { this.source = source; }

    public String getAmount()                   { return amount; }
    public void setAmount(String amount)        { this.amount = amount; }

    public String getIncomeDate()               { return incomeDate; }
    public void setIncomeDate(String d)         { this.incomeDate = d; }
}
