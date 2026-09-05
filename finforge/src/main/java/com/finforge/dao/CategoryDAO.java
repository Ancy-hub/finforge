package com.finforge.dao;

import com.finforge.exception.DAOException;
import com.finforge.model.Category;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for {@link Category} persistence operations.
 */
public interface CategoryDAO {

    /** Persists a new category and returns it with the generated ID. */
    Category save(Category category) throws DAOException;

    /** Finds a category by its primary key. */
    Optional<Category> findById(int categoryId) throws DAOException;

    /** Returns all categories for a given user, ordered alphabetically. */
    List<Category> findAllByUserId(int userId) throws DAOException;

    /** Updates an existing category record. */
    void update(Category category) throws DAOException;

    /** Deletes a category by primary key. */
    void delete(int categoryId) throws DAOException;

    /** Returns {@code true} if the category belongs to the specified user. */
    boolean existsByIdAndUserId(int categoryId, int userId) throws DAOException;

    /** Returns {@code true} if the user already has a category with the same name. */
    boolean existsByNameAndUserId(String name, int userId) throws DAOException;
}
