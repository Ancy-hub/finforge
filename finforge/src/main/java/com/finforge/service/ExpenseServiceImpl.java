package com.finforge.service;

import com.finforge.dao.ExpenseDAO;
import com.finforge.dto.ExpenseDTO;
import com.finforge.dto.ExpenseFilterDTO;
import com.finforge.dto.PagedResult;
import com.finforge.exception.DAOException;
import com.finforge.exception.ValidationException;
import com.finforge.model.Expense;
import com.finforge.repository.CategoryRepository;
import com.finforge.repository.ExpenseRepository;
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
 * Business-logic implementation for expense management using Spring Data JPA.
 */
@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private static final Logger logger = LogManager.getLogger(ExpenseServiceImpl.class);

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseDAO expenseDAO;

    @Autowired
    public ExpenseServiceImpl(ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.expenseDAO = null;
    }

    public ExpenseServiceImpl(ExpenseDAO expenseDAO) {
        this.expenseRepository = null;
        this.categoryRepository = null;
        this.expenseDAO = expenseDAO;
    }

    @Override
    public Expense addExpense(int userId, ExpenseDTO dto)
            throws ValidationException, DAOException {

        Expense expense = validateAndBuild(userId, dto);
        Expense saved = (expenseRepository != null)
                ? expenseRepository.save(expense)
                : expenseDAO.save(expense);

        logger.info("Expense added: id={} title='{}' userId={}",
                saved.getExpenseId(), saved.getTitle(), userId);
        return saved;
    }

    @Override
    public void updateExpense(int userId, ExpenseDTO dto)
            throws ValidationException, DAOException {

        int expenseId = ValidationUtil.validateId(dto.getExpenseId(), "Expense ID");

        if (expenseRepository != null) {
            if (expenseRepository.findByExpenseIdAndUserId(expenseId, userId).isEmpty()) {
                throw new ValidationException("Expense not found or access denied.");
            }
        } else {
            if (!expenseDAO.existsByIdAndUserId(expenseId, userId)) {
                throw new ValidationException("Expense not found or access denied.");
            }
        }

        Expense expense = validateAndBuild(userId, dto);
        expense.setExpenseId(expenseId);

        if (expenseRepository != null) {
            expenseRepository.save(expense);
        } else {
            expenseDAO.update(expense);
        }
    }

    @Override
    public void deleteExpense(int expenseId, int userId)
            throws ValidationException, DAOException {

        if (expenseRepository != null) {
            if (expenseRepository.findByExpenseIdAndUserId(expenseId, userId).isEmpty()) {
                throw new ValidationException("Expense not found or access denied.");
            }
            expenseRepository.deleteByExpenseIdAndUserId(expenseId, userId);
        } else {
            if (!expenseDAO.existsByIdAndUserId(expenseId, userId)) {
                throw new ValidationException("Expense not found or access denied.");
            }
            expenseDAO.delete(expenseId);
        }
        logger.info("Expense deleted: id={} userId={}", expenseId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Expense getExpenseById(int expenseId, int userId)
            throws ValidationException, DAOException {

        if (expenseRepository != null) {
            Expense expense = expenseRepository.findByExpenseIdAndUserId(expenseId, userId)
                    .orElseThrow(() -> new ValidationException("Expense not found or access denied."));
            enrichCategoryName(expense);
            return expense;
        }

        return expenseDAO.findById(expenseId)
                .filter(e -> e.getUserId() == userId)
                .orElseThrow(() -> new ValidationException("Expense not found or access denied."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expense> getAllExpenses(int userId) throws DAOException {
        if (expenseRepository != null) {
            List<Expense> list = expenseRepository.findByUserIdOrderByExpenseDateDesc(userId);
            list.forEach(this::enrichCategoryName);
            return list;
        }
        return expenseDAO.findAllByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expense> searchExpenses(int userId, ExpenseFilterDTO filter)
            throws ValidationException, DAOException {

        LocalDate fromDate   = null;
        LocalDate toDate     = null;
        Integer   categoryId = null;

        String rawFrom = filter.getFromDate();
        String rawTo   = filter.getToDate();
        String rawCat  = filter.getCategoryId();

        if (rawFrom != null && !rawFrom.trim().isEmpty()) {
            fromDate = ValidationUtil.validateDate(rawFrom.trim(), "From date");
        }
        if (rawTo != null && !rawTo.trim().isEmpty()) {
            toDate = ValidationUtil.validateDate(rawTo.trim(), "To date");
        }
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new ValidationException("\"From date\" cannot be after \"To date\".");
        }
        if (rawCat != null && !rawCat.trim().isEmpty()) {
            categoryId = ValidationUtil.validateId(rawCat.trim(), "Category");
        }

        if (expenseRepository != null) {
            List<Expense> list;
            if (fromDate != null && toDate != null) {
                list = expenseRepository.findByUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(userId, fromDate, toDate);
            } else if (categoryId != null) {
                list = expenseRepository.findByUserIdAndCategoryIdOrderByExpenseDateDesc(userId, categoryId);
            } else {
                list = expenseRepository.findByUserIdOrderByExpenseDateDesc(userId);
            }
            list.forEach(this::enrichCategoryName);
            return list;
        }

        return expenseDAO.findByFilters(userId, fromDate, toDate, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<Expense> getAllExpensesPaged(int userId, int page, int pageSize)
            throws DAOException {

        if (expenseRepository != null) {
            Page<Expense> paged = expenseRepository.findByUserIdOrderByExpenseDateDesc(userId, PageRequest.of(Math.max(0, page - 1), pageSize));
            paged.getContent().forEach(this::enrichCategoryName);
            return new PagedResult<>(paged.getContent(), page, pageSize, (int) paged.getTotalElements());
        }

        int total  = expenseDAO.countByUserId(userId);
        int offset = (page - 1) * pageSize;
        List<Expense> items = expenseDAO.findByUserIdPaged(userId, offset, pageSize);
        return new PagedResult<>(items, page, pageSize, total);
    }

    private void enrichCategoryName(Expense expense) {
        if (categoryRepository != null && expense != null && expense.getCategoryName() == null) {
            categoryRepository.findById(expense.getCategoryId())
                    .ifPresent(c -> expense.setCategoryName(c.getName()));
        }
    }

    private Expense validateAndBuild(int userId, ExpenseDTO dto) throws ValidationException {
        ValidationUtil.validateNotEmpty(dto.getTitle(), "Title");
        BigDecimal amount   = ValidationUtil.validateAmount(dto.getAmount(), "Amount");
        LocalDate  date     = ValidationUtil.validateDate(dto.getExpenseDate(), "Expense date");
        int        catId    = ValidationUtil.validateId(dto.getCategoryId(), "Category");

        Expense expense = new Expense();
        expense.setTitle(dto.getTitle().trim());
        expense.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        expense.setAmount(amount);
        expense.setCategoryId(catId);
        expense.setUserId(userId);
        expense.setExpenseDate(date);
        return expense;
    }
}
