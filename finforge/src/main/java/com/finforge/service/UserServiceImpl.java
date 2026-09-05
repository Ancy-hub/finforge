package com.finforge.service;

import com.finforge.dao.CategoryDAO;
import com.finforge.dao.CategoryDAOImpl;
import com.finforge.dao.UserDAO;
import com.finforge.dto.UserDTO;
import com.finforge.exception.DAOException;
import com.finforge.exception.DuplicateUserException;
import com.finforge.exception.InvalidCredentialsException;
import com.finforge.exception.UserNotFoundException;
import com.finforge.exception.ValidationException;
import com.finforge.model.Category;
import com.finforge.model.User;
import com.finforge.util.PasswordUtil;
import com.finforge.util.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

/**
 * Business-logic implementation for user management.
 */
public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private static final List<String[]> DEFAULT_CATEGORIES = Arrays.asList(
            new String[]{"Food",          "Food and dining expenses"},
            new String[]{"Travel",        "Transportation and travel expenses"},
            new String[]{"Rent",          "Rent and housing expenses"},
            new String[]{"Medical",       "Healthcare and medical expenses"},
            new String[]{"Shopping",      "Shopping and retail expenses"},
            new String[]{"Utilities",     "Utility bills and services"},
            new String[]{"Entertainment", "Entertainment and recreation"}
    );

    private final UserDAO     userDAO;
    private final CategoryDAO categoryDAO;

    /**
     * Primary constructor — injects both DAOs.
     */
    public UserServiceImpl(UserDAO userDAO, CategoryDAO categoryDAO) {
        this.userDAO     = userDAO;
        this.categoryDAO = categoryDAO;
    }

    /**
     * Convenience constructor for contexts where category seeding is not needed.
     */
    public UserServiceImpl(UserDAO userDAO) {
        this(userDAO, null);
    }

    // -----------------------------------------------------------------------

    @Override
    public User register(UserDTO dto)
            throws ValidationException, DuplicateUserException, DAOException {

        ValidationUtil.validateUsername(dto.getUsername());
        ValidationUtil.validateEmail(dto.getEmail());
        ValidationUtil.validatePassword(dto.getPassword());
        ValidationUtil.validatePasswordsMatch(dto.getPassword(), dto.getConfirmPassword());
        ValidationUtil.validateNotEmpty(dto.getFirstName(), "First name");
        ValidationUtil.validateNotEmpty(dto.getLastName(),  "Last name");

        if (userDAO.existsByUsername(dto.getUsername())) {
            throw new DuplicateUserException(
                    "Username '" + dto.getUsername() + "' is already taken.");
        }
        if (userDAO.existsByEmail(dto.getEmail())) {
            throw new DuplicateUserException(
                    "An account with email '" + dto.getEmail() + "' already exists.");
        }

        User user = new User();
        user.setUsername(dto.getUsername().trim());
        user.setEmail(dto.getEmail().trim().toLowerCase());
        user.setPasswordHash(PasswordUtil.hashPassword(dto.getPassword()));
        user.setFirstName(dto.getFirstName().trim());
        user.setLastName(dto.getLastName().trim());
        user.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);
        user.setActive(true);

        User savedUser = userDAO.save(user);

        // Seed default categories for the new user
        if (categoryDAO != null) {
            for (String[] cat : DEFAULT_CATEGORIES) {
                Category category = new Category();
                category.setName(cat[0]);
                category.setDescription(cat[1]);
                category.setUserId(savedUser.getUserId());
                categoryDAO.save(category);
            }
        }

        logger.info("User registered: id={} username='{}'",
                savedUser.getUserId(), savedUser.getUsername());
        return savedUser;
    }

    @Override
    public User login(String username, String password)
            throws ValidationException, InvalidCredentialsException, DAOException {

        ValidationUtil.validateNotEmpty(username, "Username");
        ValidationUtil.validateNotEmpty(password, "Password");

        User user = userDAO.findByUsername(username.trim())
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive()) {
            throw new InvalidCredentialsException("Your account is inactive. Please contact support.");
        }
        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        logger.info("User logged in: id={} username='{}'",
                user.getUserId(), user.getUsername());
        return user;
    }

    @Override
    public User getProfile(int userId) throws UserNotFoundException, DAOException {
        return userDAO.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public void updateProfile(int userId, UserDTO dto)
            throws ValidationException, UserNotFoundException, DAOException {

        ValidationUtil.validateNotEmpty(dto.getFirstName(), "First name");
        ValidationUtil.validateNotEmpty(dto.getLastName(),  "Last name");
        ValidationUtil.validateEmail(dto.getEmail());

        User existing = userDAO.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        existing.setFirstName(dto.getFirstName().trim());
        existing.setLastName(dto.getLastName().trim());
        existing.setEmail(dto.getEmail().trim().toLowerCase());
        existing.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);

        userDAO.update(existing);
    }

    @Override
    public void changePassword(int userId,
                               String currentPassword,
                               String newPassword,
                               String confirmPassword)
            throws ValidationException, InvalidCredentialsException, UserNotFoundException, DAOException {

        ValidationUtil.validateNotEmpty(currentPassword, "Current password");
        ValidationUtil.validatePassword(newPassword);
        ValidationUtil.validatePasswordsMatch(newPassword, confirmPassword);

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!PasswordUtil.verifyPassword(currentPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect.");
        }

        userDAO.updatePassword(userId, PasswordUtil.hashPassword(newPassword));
        logger.info("Password changed for userId={}", userId);
    }
}
