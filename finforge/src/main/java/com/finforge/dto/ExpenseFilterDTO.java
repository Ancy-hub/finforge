package com.finforge.dto;

/**
 * Carries optional filter criteria for expense search queries submitted from the UI.
 * <p>
 * All fields are raw {@code String} values taken directly from HTTP request parameters
 * so that the servlet layer can forward them back to the JSP unchanged when validation
 * fails.  The service layer is responsible for parsing and validating them.
 * </p>
 */
public class ExpenseFilterDTO {

    /** ISO date string (YYYY-MM-DD), lower bound — inclusive.  Null/blank = no lower bound. */
    private String fromDate;

    /** ISO date string (YYYY-MM-DD), upper bound — inclusive.  Null/blank = no upper bound. */
    private String toDate;

    /** Integer string for the category primary key.  Null/blank = all categories. */
    private String categoryId;

    public ExpenseFilterDTO() {}

    // ---- Getters / setters ----

    public String getFromDate()              { return fromDate; }
    public void   setFromDate(String v)      { this.fromDate = v; }

    public String getToDate()                { return toDate; }
    public void   setToDate(String v)        { this.toDate = v; }

    public String getCategoryId()            { return categoryId; }
    public void   setCategoryId(String v)    { this.categoryId = v; }

    // ---- Helpers ----

    /** Returns {@code true} when at least one filter field is non-blank. */
    public boolean hasAnyFilter() {
        return isNotBlank(fromDate) || isNotBlank(toDate) || isNotBlank(categoryId);
    }

    private static boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
