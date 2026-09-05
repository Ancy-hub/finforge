package com.finforge.repository;

import com.finforge.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByUserIdOrderByNameAsc(int userId);
    Optional<Category> findByCategoryIdAndUserId(int categoryId, int userId);
    boolean existsByNameAndUserId(String name, int userId);
    boolean existsByCategoryIdAndUserId(int categoryId, int userId);
    void deleteByCategoryIdAndUserId(int categoryId, int userId);
}
