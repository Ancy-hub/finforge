package com.finforge.service;

import com.finforge.dto.ExpenseDTO;
import com.finforge.dto.ExpenseFilterDTO;
import com.finforge.dto.PagedResult;
import com.finforge.exception.DAOException;
import com.finforge.exception.ValidationException;
import com.finforge.model.Expense;

import java.util.List;

/**
 * Service interface for expense management operations.
 */
public interface ExpenseService {

    /** Validates and persists a new expense for the given user. */
    Expense addExpense(int userId, ExpenseDTO dto) throws ValidationException, DAOException;

    /** Validates and updates an existing expense owned by the given user. */
    void updateExpense(int userId, ExpenseDTO dto) throws ValidationException, DAOException;

    /** Deletes an expense by ID after verifying ownership. */
    void deleteExpense(int expenseId, int userId) throws ValidationException, DAOException;

    /** Returns a single expense by ID (must belong to the given user). */
    Expense getExpenseById(int expenseId, int userId) throws ValidationException, DAOException;

    /** Returns all expenses for the given user. */
    List<Expense> getAllExpenses(int userId) throws DAOException;

    /**
     * Returns expenses matching optional date-range and/or category filters.
     * Blank filter fields are ignored.
     */
    List<Expense> searchExpenses(int userId, ExpenseFilterDTO filter)
            throws ValidationException, DAOException;

    /** Returns a page of expenses for the given user, ordered by date descending. */
    PagedResult<Expense> getAllExpensesPaged(int userId, int page, int pageSize) throws DAOException;
}
