package com.finforge.service;

import com.finforge.dto.UserDTO;
import com.finforge.exception.DAOException;
import com.finforge.exception.DuplicateUserException;
import com.finforge.exception.InvalidCredentialsException;
import com.finforge.exception.UserNotFoundException;
import com.finforge.exception.ValidationException;
import com.finforge.model.User;

/**
 * Service interface for user management operations.
 */
public interface UserService {

    /**
     * Registers a new user after validating the DTO and checking for duplicates.
     * Default expense categories are seeded for the new user.
     *
     * @return the newly created {@link User} with its generated ID
     */
    User register(UserDTO dto)
            throws ValidationException, DuplicateUserException, DAOException;

    /**
     * Authenticates a user by username and password.
     *
     * @return the authenticated {@link User}
     * @throws InvalidCredentialsException if credentials are wrong or account is inactive
     */
    User login(String username, String password)
            throws ValidationException, InvalidCredentialsException, DAOException;

    /**
     * Returns the full profile of the given user.
     */
    User getProfile(int userId)
            throws UserNotFoundException, DAOException;

    /**
     * Updates profile fields (name, email, phone) for the given user.
     */
    void updateProfile(int userId, UserDTO dto)
            throws ValidationException, UserNotFoundException, DAOException;

    /**
     * Changes the password after verifying the current password.
     */
    void changePassword(int userId, String currentPassword, String newPassword, String confirmPassword)
            throws ValidationException, InvalidCredentialsException, UserNotFoundException, DAOException;
}
