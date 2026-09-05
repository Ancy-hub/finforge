package com.finforge.dao;

import com.finforge.exception.DAOException;
import com.finforge.model.User;

import java.util.Optional;

/**
 * Data Access Object interface for {@link User} persistence operations.
 */
public interface UserDAO {

    /**
     * Persists a new user and returns the saved entity with its generated ID.
     */
    User save(User user) throws DAOException;

    /**
     * Finds a user by primary key.
     */
    Optional<User> findById(int userId) throws DAOException;

    /**
     * Finds a user by username (case-insensitive).
     */
    Optional<User> findByUsername(String username) throws DAOException;

    /**
     * Finds a user by e-mail address (case-insensitive).
     */
    Optional<User> findByEmail(String email) throws DAOException;

    /**
     * Updates profile fields (first name, last name, phone, email) for an existing user.
     */
    void update(User user) throws DAOException;

    /**
     * Updates the stored password hash for the given user.
     */
    void updatePassword(int userId, String newPasswordHash) throws DAOException;

    /**
     * Returns {@code true} if a user with the given username already exists.
     */
    boolean existsByUsername(String username) throws DAOException;

    /**
     * Returns {@code true} if a user with the given e-mail address already exists.
     */
    boolean existsByEmail(String email) throws DAOException;
}
