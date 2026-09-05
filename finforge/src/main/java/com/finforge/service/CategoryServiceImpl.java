package com.finforge.service;

import com.finforge.dao.CategoryDAO;
import com.finforge.dto.CategoryDTO;
import com.finforge.exception.DAOException;
import com.finforge.exception.ValidationException;
import com.finforge.model.Category;
import com.finforge.repository.CategoryRepository;
import com.finforge.util.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business-logic implementation for category management using Spring Data JPA.
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LogManager.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;
    private final CategoryDAO categoryDAO;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryDAO = null;
    }

    public CategoryServiceImpl(CategoryDAO categoryDAO) {
        this.categoryRepository = null;
        this.categoryDAO = categoryDAO;
    }

    @Override
    public Category addCategory(int userId, CategoryDTO dto)
            throws ValidationException, DAOException {

        ValidationUtil.validateNotEmpty(dto.getName(), "Category name");

        String trimmedName = dto.getName().trim();
        boolean exists = (categoryRepository != null)
                ? categoryRepository.existsByNameAndUserId(trimmedName, userId)
                : categoryDAO.existsByNameAndUserId(trimmedName, userId);

        if (exists) {
            throw new ValidationException("Category name",
                    "A category named '" + trimmedName + "' already exists.");
        }

        Category category = new Category();
        category.setName(trimmedName);
        category.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        category.setUserId(userId);

        Category saved = (categoryRepository != null)
                ? categoryRepository.save(category)
                : categoryDAO.save(category);

        logger.info("Category added: id={} name='{}' userId={}",
                saved.getCategoryId(), saved.getName(), userId);
        return saved;
    }

    @Override
    public void updateCategory(int userId, CategoryDTO dto)
            throws ValidationException, DAOException {

        int catId = ValidationUtil.validateId(dto.getCategoryId(), "Category ID");
        ValidationUtil.validateNotEmpty(dto.getName(), "Category name");

        boolean exists = (categoryRepository != null)
                ? categoryRepository.existsByCategoryIdAndUserId(catId, userId)
                : categoryDAO.existsByIdAndUserId(catId, userId);

        if (!exists) {
            throw new ValidationException("Category not found or access denied.");
        }

        Category category = new Category();
        category.setCategoryId(catId);
        category.setName(dto.getName().trim());
        category.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        category.setUserId(userId);

        if (categoryRepository != null) {
            categoryRepository.save(category);
        } else {
            categoryDAO.update(category);
        }
        logger.info("Category updated: id={} name='{}' userId={}", catId, category.getName(), userId);
    }

    @Override
    public void deleteCategory(int categoryId, int userId)
            throws ValidationException, DAOException {

        boolean exists = (categoryRepository != null)
                ? categoryRepository.existsByCategoryIdAndUserId(categoryId, userId)
                : categoryDAO.existsByIdAndUserId(categoryId, userId);

        if (!exists) {
            throw new ValidationException("Category not found or access denied.");
        }

        if (categoryRepository != null) {
            categoryRepository.deleteByCategoryIdAndUserId(categoryId, userId);
        } else {
            categoryDAO.delete(categoryId);
        }
        logger.info("Category deleted: id={} userId={}", categoryId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(int categoryId, int userId)
            throws ValidationException, DAOException {

        if (categoryRepository != null) {
            return categoryRepository.findByCategoryIdAndUserId(categoryId, userId)
                    .orElseThrow(() -> new ValidationException("Category not found or access denied."));
        }
        return categoryDAO.findById(categoryId)
                .filter(c -> c.getUserId() == userId)
                .orElseThrow(() -> new ValidationException("Category not found or access denied."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories(int userId) throws DAOException {
        if (categoryRepository != null) {
            return categoryRepository.findByUserIdOrderByNameAsc(userId);
        }
        return categoryDAO.findAllByUserId(userId);
    }
}
