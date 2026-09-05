package com.finforge.dao;

import com.finforge.exception.DAOException;
import com.finforge.model.Expense;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for {@link Expense} persistence operations.
 */
public interface ExpenseDAO {

    /** Persists a new expense and returns it with the generated ID. */
    Expense save(Expense expense) throws DAOException;

    /** Finds an expense by its primary key (includes category name via JOIN). */
    Optional<Expense> findById(int expenseId) throws DAOException;

    /** Returns all expenses for a given user, ordered by date descending. */
    List<Expense> findAllByUserId(int userId) throws DAOException;

    /** Updates an existing expense record. */
    void update(Expense expense) throws DAOException;

    /** Deletes an expense by primary key. */
    void delete(int expenseId) throws DAOException;

    /** Returns {@code true} if the expense belongs to the specified user. */
    boolean existsByIdAndUserId(int expenseId, int userId) throws DAOException;

    /**
     * Returns expenses matching the supplied optional filters.
     * Any null parameter is ignored (i.e. treated as "no restriction").
     */
    List<Expense> findByFilters(int userId, LocalDate fromDate, LocalDate toDate,
                                Integer categoryId) throws DAOException;

    /** Returns the total number of expenses for a given user (used for pagination). */
    int countByUserId(int userId) throws DAOException;

    /**
     * Returns a page of expenses for a given user, ordered by date descending.
     *
     * @param offset 0-based row offset (i.e. (page - 1) * pageSize)
     * @param limit  maximum rows to return
     */
    List<Expense> findByUserIdPaged(int userId, int offset, int limit) throws DAOException;
}
