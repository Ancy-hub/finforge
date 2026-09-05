package com.finforge.service;

import com.finforge.dao.ExpenseDAO;
import com.finforge.dto.ExpenseDTO;
import com.finforge.dto.ExpenseFilterDTO;
import com.finforge.dto.PagedResult;
import com.finforge.exception.DAOException;
import com.finforge.exception.ValidationException;
import com.finforge.model.Expense;
import com.finforge.util.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Business-logic implementation for expense management.
 */
public class ExpenseServiceImpl implements ExpenseService {

    private static final Logger logger = LogManager.getLogger(ExpenseServiceImpl.class);

    private final ExpenseDAO expenseDAO;

    public ExpenseServiceImpl(ExpenseDAO expenseDAO) {
        this.expenseDAO = expenseDAO;
    }

    // -----------------------------------------------------------------------

    @Override
    public Expense addExpense(int userId, ExpenseDTO dto)
            throws ValidationException, DAOException {

        Expense expense = validateAndBuild(userId, dto);
        Expense saved   = expenseDAO.save(expense);
        logger.info("Expense added: id={} title='{}' userId={}",
                saved.getExpenseId(), saved.getTitle(), userId);
        return saved;
    }

    @Override
    public void updateExpense(int userId, ExpenseDTO dto)
            throws ValidationException, DAOException {

        int expenseId = ValidationUtil.validateId(dto.getExpenseId(), "Expense ID");

        if (!expenseDAO.existsByIdAndUserId(expenseId, userId)) {
            throw new ValidationException("Expense not found or access denied.");
        }

        Expense expense = validateAndBuild(userId, dto);
        expense.setExpenseId(expenseId);
        expenseDAO.update(expense);
    }

    @Override
    public void deleteExpense(int expenseId, int userId)
            throws ValidationException, DAOException {

        if (!expenseDAO.existsByIdAndUserId(expenseId, userId)) {
            throw new ValidationException("Expense not found or access denied.");
        }
        expenseDAO.delete(expenseId);
        logger.info("Expense deleted: id={} userId={}", expenseId, userId);
    }

    @Override
    public Expense getExpenseById(int expenseId, int userId)
            throws ValidationException, DAOException {

        return expenseDAO.findById(expenseId)
                .filter(e -> e.getUserId() == userId)
                .orElseThrow(() -> new ValidationException("Expense not found or access denied."));
    }

    @Override
    public List<Expense> getAllExpenses(int userId) throws DAOException {
        return expenseDAO.findAllByUserId(userId);
    }

    @Override
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
        logger.debug("Searching expenses: userId={} from={} to={} categoryId={}",
                userId, fromDate, toDate, categoryId);
        return expenseDAO.findByFilters(userId, fromDate, toDate, categoryId);
    }

    @Override
    public PagedResult<Expense> getAllExpensesPaged(int userId, int page, int pageSize)
            throws DAOException {

        int total  = expenseDAO.countByUserId(userId);
        int offset = (page - 1) * pageSize;
        List<Expense> items = expenseDAO.findByUserIdPaged(userId, offset, pageSize);
        return new PagedResult<>(items, page, pageSize, total);
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

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
