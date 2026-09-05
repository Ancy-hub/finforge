package com.finforge.service;

import com.finforge.dto.IncomeDTO;
import com.finforge.dto.PagedResult;
import com.finforge.exception.DAOException;
import com.finforge.exception.ValidationException;
import com.finforge.model.Income;

import java.util.List;

/**
 * Service interface for income management operations.
 */
public interface IncomeService {

    /** Validates and persists a new income entry for the given user. */
    Income addIncome(int userId, IncomeDTO dto) throws ValidationException, DAOException;

    /** Validates and updates an existing income entry owned by the given user. */
    void updateIncome(int userId, IncomeDTO dto) throws ValidationException, DAOException;

    /** Deletes an income entry by ID after verifying ownership. */
    void deleteIncome(int incomeId, int userId) throws ValidationException, DAOException;

    /** Returns a single income entry by ID (must belong to the given user). */
    Income getIncomeById(int incomeId, int userId) throws ValidationException, DAOException;

    /** Returns all income entries for the given user. */
    List<Income> getAllIncomes(int userId) throws DAOException;

    /** Returns a page of income entries for the given user, ordered by date descending. */
    PagedResult<Income> getAllIncomesPaged(int userId, int page, int pageSize) throws DAOException;
}
