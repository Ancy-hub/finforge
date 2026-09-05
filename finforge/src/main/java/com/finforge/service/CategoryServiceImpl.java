package com.finforge.service;

import com.finforge.dao.CategoryDAO;
import com.finforge.dto.CategoryDTO;
import com.finforge.exception.DAOException;
import com.finforge.exception.ValidationException;
import com.finforge.model.Category;
import com.finforge.util.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Business-logic implementation for category management.
 */
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LogManager.getLogger(CategoryServiceImpl.class);

    private final CategoryDAO categoryDAO;

    public CategoryServiceImpl(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    // -----------------------------------------------------------------------

    @Override
    public Category addCategory(int userId, CategoryDTO dto)
            throws ValidationException, DAOException {

        ValidationUtil.validateNotEmpty(dto.getName(), "Category name");

        String trimmedName = dto.getName().trim();
        if (categoryDAO.existsByNameAndUserId(trimmedName, userId)) {
            throw new ValidationException("Category name",
                    "A category named '" + trimmedName + "' already exists.");
        }

        Category category = new Category();
        category.setName(trimmedName);
        category.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        category.setUserId(userId);

        Category saved = categoryDAO.save(category);
        logger.info("Category added: id={} name='{}' userId={}",
                saved.getCategoryId(), saved.getName(), userId);
        return saved;
    }

    @Override
    public void updateCategory(int userId, CategoryDTO dto)
            throws ValidationException, DAOException {

        int catId = ValidationUtil.validateId(dto.getCategoryId(), "Category ID");
        ValidationUtil.validateNotEmpty(dto.getName(), "Category name");

        if (!categoryDAO.existsByIdAndUserId(catId, userId)) {
            throw new ValidationException("Category not found or access denied.");
        }

        Category category = new Category();
        category.setCategoryId(catId);
        category.setName(dto.getName().trim());
        category.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        category.setUserId(userId);

        categoryDAO.update(category);
        logger.info("Category updated: id={} name='{}' userId={}", catId, category.getName(), userId);
    }

    @Override
    public void deleteCategory(int categoryId, int userId)
            throws ValidationException, DAOException {

        if (!categoryDAO.existsByIdAndUserId(categoryId, userId)) {
            throw new ValidationException("Category not found or access denied.");
        }
        categoryDAO.delete(categoryId);
        logger.info("Category deleted: id={} userId={}", categoryId, userId);
    }

    @Override
    public Category getCategoryById(int categoryId, int userId)
            throws ValidationException, DAOException {

        return categoryDAO.findById(categoryId)
                .filter(c -> c.getUserId() == userId)
                .orElseThrow(() -> new ValidationException("Category not found or access denied."));
    }

    @Override
    public List<Category> getAllCategories(int userId) throws DAOException {
        return categoryDAO.findAllByUserId(userId);
    }
}
