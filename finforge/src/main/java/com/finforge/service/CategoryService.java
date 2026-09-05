package com.finforge.service;

import com.finforge.dto.CategoryDTO;
import com.finforge.exception.DAOException;
import com.finforge.exception.ValidationException;
import com.finforge.model.Category;

import java.util.List;

/**
 * Service interface for category management operations.
 */
public interface CategoryService {

    /** Validates and persists a new category for the given user. */
    Category addCategory(int userId, CategoryDTO dto) throws ValidationException, DAOException;

    /** Validates and updates an existing category owned by the given user. */
    void updateCategory(int userId, CategoryDTO dto) throws ValidationException, DAOException;

    /** Deletes a category by ID after verifying ownership. */
    void deleteCategory(int categoryId, int userId) throws ValidationException, DAOException;

    /** Returns a single category by ID (must belong to the given user). */
    Category getCategoryById(int categoryId, int userId) throws ValidationException, DAOException;

    /** Returns all categories for the given user. */
    List<Category> getAllCategories(int userId) throws DAOException;
}
