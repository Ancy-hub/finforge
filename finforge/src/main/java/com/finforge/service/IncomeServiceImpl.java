package com.finforge.service;

import com.finforge.dao.IncomeDAO;
import com.finforge.dto.IncomeDTO;
import com.finforge.dto.PagedResult;
import com.finforge.exception.DAOException;
import com.finforge.exception.ValidationException;
import com.finforge.model.Income;
import com.finforge.repository.IncomeRepository;
import com.finforge.util.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Business-logic implementation for income management using Spring Data JPA.
 */
@Service
@Transactional
public class IncomeServiceImpl implements IncomeService {

    private static final Logger logger = LogManager.getLogger(IncomeServiceImpl.class);

    private final IncomeRepository incomeRepository;
    private final IncomeDAO incomeDAO;

    @Autowired
    public IncomeServiceImpl(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
        this.incomeDAO = null;
    }

    public IncomeServiceImpl(IncomeDAO incomeDAO) {
        this.incomeRepository = null;
        this.incomeDAO = incomeDAO;
    }

    @Override
    public Income addIncome(int userId, IncomeDTO dto)
            throws ValidationException, DAOException {

        Income income = validateAndBuild(userId, dto);
        Income saved = (incomeRepository != null)
                ? incomeRepository.save(income)
                : incomeDAO.save(income);

        logger.info("Income added: id={} source='{}' userId={}",
                saved.getIncomeId(), saved.getSource(), userId);
        return saved;
    }

    @Override
    public void updateIncome(int userId, IncomeDTO dto)
            throws ValidationException, DAOException {

        int incomeId = ValidationUtil.validateId(dto.getIncomeId(), "Income ID");

        if (incomeRepository != null) {
            if (incomeRepository.findByIncomeIdAndUserId(incomeId, userId).isEmpty()) {
                throw new ValidationException("Income record not found or access denied.");
            }
        } else {
            if (!incomeDAO.existsByIdAndUserId(incomeId, userId)) {
                throw new ValidationException("Income record not found or access denied.");
            }
        }

        Income income = validateAndBuild(userId, dto);
        income.setIncomeId(incomeId);

        if (incomeRepository != null) {
            incomeRepository.save(income);
        } else {
            incomeDAO.update(income);
        }
    }

    @Override
    public void deleteIncome(int incomeId, int userId)
            throws ValidationException, DAOException {

        if (incomeRepository != null) {
            if (incomeRepository.findByIncomeIdAndUserId(incomeId, userId).isEmpty()) {
                throw new ValidationException("Income record not found or access denied.");
            }
            incomeRepository.deleteByIncomeIdAndUserId(incomeId, userId);
        } else {
            if (!incomeDAO.existsByIdAndUserId(incomeId, userId)) {
                throw new ValidationException("Income record not found or access denied.");
            }
            incomeDAO.delete(incomeId);
        }
        logger.info("Income deleted: id={} userId={}", incomeId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Income getIncomeById(int incomeId, int userId)
            throws ValidationException, DAOException {

        if (incomeRepository != null) {
            return incomeRepository.findByIncomeIdAndUserId(incomeId, userId)
                    .orElseThrow(() -> new ValidationException("Income record not found or access denied."));
        }

        return incomeDAO.findById(incomeId)
                .filter(i -> i.getUserId() == userId)
                .orElseThrow(() -> new ValidationException("Income record not found or access denied."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Income> getAllIncomes(int userId) throws DAOException {
        if (incomeRepository != null) {
            return incomeRepository.findByUserIdOrderByIncomeDateDesc(userId);
        }
        return incomeDAO.findAllByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<Income> getAllIncomesPaged(int userId, int page, int pageSize)
            throws DAOException {

        if (incomeRepository != null) {
            Page<Income> paged = incomeRepository.findByUserIdOrderByIncomeDateDesc(userId, PageRequest.of(Math.max(0, page - 1), pageSize));
            return new PagedResult<>(paged.getContent(), page, pageSize, (int) paged.getTotalElements());
        }

        int total  = incomeDAO.countByUserId(userId);
        int offset = (page - 1) * pageSize;
        List<Income> items = incomeDAO.findByUserIdPaged(userId, offset, pageSize);
        return new PagedResult<>(items, page, pageSize, total);
    }

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
