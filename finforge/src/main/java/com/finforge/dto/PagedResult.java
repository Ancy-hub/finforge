package com.finforge.dto;

import java.util.List;

/**
 * Generic container for a single page of query results, carrying both the
 * items for the current page and the pagination metadata needed to render
 * navigation controls in the UI.
 *
 * @param <T> the item type
 */
public class PagedResult<T> {

    private List<T> items;
    private int     currentPage;
    private int     pageSize;
    private int     totalItems;
    private int     totalPages;

    public PagedResult() {}

    /**
     * Convenience constructor that computes {@code totalPages} automatically.
     *
     * @param items       the records for the current page
     * @param currentPage 1-based page number
     * @param pageSize    maximum records per page
     * @param totalItems  total record count across all pages
     */
    public PagedResult(List<T> items, int currentPage, int pageSize, int totalItems) {
        this.items       = items;
        this.currentPage = currentPage;
        this.pageSize    = pageSize;
        this.totalItems  = totalItems;
        this.totalPages  = (totalItems == 0) ? 1
                : (int) Math.ceil((double) totalItems / pageSize);
    }

    // ---- Getters ----

    public List<T> getItems()      { return items; }
    public int getCurrentPage()    { return currentPage; }
    public int getPageSize()       { return pageSize; }
    public int getTotalItems()     { return totalItems; }
    public int getTotalPages()     { return totalPages; }

    /** {@code true} when a previous page exists (currentPage > 1). */
    public boolean isHasPreviousPage() { return currentPage > 1; }

    /** {@code true} when a next page exists. */
    public boolean isHasNextPage()     { return currentPage < totalPages; }

    // ---- Setters ----

    public void setItems(List<T> items)         { this.items = items; }
    public void setCurrentPage(int v)           { this.currentPage = v; }
    public void setPageSize(int v)              { this.pageSize = v; }
    public void setTotalItems(int v)            { this.totalItems = v; }
    public void setTotalPages(int v)            { this.totalPages = v; }
}
