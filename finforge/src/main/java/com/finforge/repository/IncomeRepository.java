package com.finforge.repository;

import com.finforge.model.Income;
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
public interface IncomeRepository extends JpaRepository<Income, Integer> {
    List<Income> findByUserIdOrderByIncomeDateDesc(int userId);
    Page<Income> findByUserIdOrderByIncomeDateDesc(int userId, Pageable pageable);
    Optional<Income> findByIncomeIdAndUserId(int incomeId, int userId);
    List<Income> findByUserIdAndIncomeDateBetweenOrderByIncomeDateDesc(int userId, LocalDate fromDate, LocalDate toDate);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.userId = :userId AND i.incomeDate BETWEEN :fromDate AND :toDate")
    BigDecimal sumAmountByUserIdAndDateBetween(@Param("userId") int userId, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.userId = :userId")
    BigDecimal sumAmountByUserId(@Param("userId") int userId);

    void deleteByIncomeIdAndUserId(int incomeId, int userId);
}
