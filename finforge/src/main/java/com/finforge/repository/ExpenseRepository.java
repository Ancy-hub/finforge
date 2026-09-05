package com.finforge.repository;

import com.finforge.model.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    List<Expense> findByUserIdOrderByExpenseDateDesc(int userId);
    Page<Expense> findByUserIdOrderByExpenseDateDesc(int userId, Pageable pageable);
    Optional<Expense> findByExpenseIdAndUserId(int expenseId, int userId);
    List<Expense> findByUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(int userId, LocalDate fromDate, LocalDate toDate);
    List<Expense> findByUserIdAndCategoryIdOrderByExpenseDateDesc(int userId, int categoryId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.userId = :userId AND e.expenseDate BETWEEN :fromDate AND :toDate")
    BigDecimal sumAmountByUserIdAndDateBetween(@Param("userId") int userId, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.userId = :userId")
    BigDecimal sumAmountByUserId(@Param("userId") int userId);

    void deleteByExpenseIdAndUserId(int expenseId, int userId);
}
