package com.finforge.service;

import com.finforge.dao.IncomeDAO;
import com.finforge.dto.IncomeDTO;
import com.finforge.dto.PagedResult;
import com.finforge.exception.DAOException;
import com.finforge.exception.ValidationException;
import com.finforge.model.Income;
import com.finforge.util.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Business-logic implementation for income management.
 */
public class IncomeServiceImpl implements IncomeService {

    private static final Logger logger = LogManager.getLogger(IncomeServiceImpl.class);

    private final IncomeDAO incomeDAO;

    public IncomeServiceImpl(IncomeDAO incomeDAO) {
        this.incomeDAO = incomeDAO;
    }

    // -----------------------------------------------------------------------

    @Override
    public Income addIncome(int userId, IncomeDTO dto)
            throws ValidationException, DAOException {

        Income income = validateAndBuild(userId, dto);
        Income saved  = incomeDAO.save(income);
        logger.info("Income added: id={} source='{}' userId={}",
                saved.getIncomeId(), saved.getSource(), userId);
        return saved;
    }

    @Override
    public void updateIncome(int userId, IncomeDTO dto)
            throws ValidationException, DAOException {

        int incomeId = ValidationUtil.validateId(dto.getIncomeId(), "Income ID");

        if (!incomeDAO.existsByIdAndUserId(incomeId, userId)) {
            throw new ValidationException("Income record not found or access denied.");
        }

        Income income = validateAndBuild(userId, dto);
        income.setIncomeId(incomeId);
        incomeDAO.update(income);
    }

    @Override
    public void deleteIncome(int incomeId, int userId)
            throws ValidationException, DAOException {

        if (!incomeDAO.existsByIdAndUserId(incomeId, userId)) {
            throw new ValidationException("Income record not found or access denied.");
        }
        incomeDAO.delete(incomeId);
        logger.info("Income deleted: id={} userId={}", incomeId, userId);
    }

    @Override
    public Income getIncomeById(int incomeId, int userId)
            throws ValidationException, DAOException {

        return incomeDAO.findById(incomeId)
                .filter(i -> i.getUserId() == userId)
                .orElseThrow(() -> new ValidationException("Income record not found or access denied."));
    }

    @Override
    public List<Income> getAllIncomes(int userId) throws DAOException {
        return incomeDAO.findAllByUserId(userId);
    }

    @Override
    public PagedResult<Income> getAllIncomesPaged(int userId, int page, int pageSize)
            throws DAOException {

        int total  = incomeDAO.countByUserId(userId);
        int offset = (page - 1) * pageSize;
        List<Income> items = incomeDAO.findByUserIdPaged(userId, offset, pageSize);
        return new PagedResult<>(items, page, pageSize, total);
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private Income validateAndBuild(int userId, IncomeDTO dto) throws ValidationException {
        ValidationUtil.validateNotEmpty(dto.getSource(), "Source");
        BigDecimal amount = ValidationUtil.validateAmount(dto.getAmount(), "Amount");
        LocalDate  date   = ValidationUtil.validateDate(dto.getIncomeDate(), "Income date");

        Income income = new Income();
        income.setSource(dto.getSource().trim());
        income.setAmount(amount);
        income.setIncomeDate(date);
        income.setUserId(userId);
        return income;
    }
}
