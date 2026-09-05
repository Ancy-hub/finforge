package com.finforge.dao;

import com.finforge.exception.DAOException;
import com.finforge.model.Income;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for {@link Income} persistence operations.
 */
public interface IncomeDAO {

    /** Persists a new income entry and returns it with the generated ID. */
    Income save(Income income) throws DAOException;

    /** Finds an income entry by its primary key. */
    Optional<Income> findById(int incomeId) throws DAOException;

    /** Returns all income entries for a given user, ordered by date descending. */
    List<Income> findAllByUserId(int userId) throws DAOException;

    /** Updates an existing income record. */
    void update(Income income) throws DAOException;

    /** Deletes an income entry by primary key. */
    void delete(int incomeId) throws DAOException;

    /** Returns {@code true} if the income record belongs to the specified user. */
    boolean existsByIdAndUserId(int incomeId, int userId) throws DAOException;

    /** Returns the total number of income records for a given user (used for pagination). */
    int countByUserId(int userId) throws DAOException;

    /**
     * Returns a page of income entries for a given user, ordered by date descending.
     *
     * @param offset 0-based row offset (i.e. (page - 1) * pageSize)
     * @param limit  maximum rows to return
     */
    List<Income> findByUserIdPaged(int userId, int offset, int limit) throws DAOException;
}
