package com.finforge.service;

import com.finforge.dao.CategoryDAO;
import com.finforge.dao.UserDAO;
import com.finforge.dto.UserDTO;
import com.finforge.exception.DAOException;
import com.finforge.exception.DuplicateUserException;
import com.finforge.exception.InvalidCredentialsException;
import com.finforge.exception.UserNotFoundException;
import com.finforge.exception.ValidationException;
import com.finforge.model.Category;
import com.finforge.model.User;
import com.finforge.repository.CategoryRepository;
import com.finforge.repository.UserRepository;
import com.finforge.util.PasswordUtil;
import com.finforge.util.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Business-logic implementation for user management using Spring Data JPA.
 */
@Service
@Transactional
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

    private final UserRepository   userRepository;
    private final CategoryRepository categoryRepository;
    private final UserDAO          userDAO;
    private final CategoryDAO      categoryDAO;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, CategoryRepository categoryRepository) {
        this.userRepository     = userRepository;
        this.categoryRepository = categoryRepository;
        this.userDAO            = null;
        this.categoryDAO        = null;
    }

    public UserServiceImpl(UserDAO userDAO, CategoryDAO categoryDAO) {
        this.userRepository     = null;
        this.categoryRepository = null;
        this.userDAO            = userDAO;
        this.categoryDAO        = categoryDAO;
    }

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

        boolean userExists = (userRepository != null)
                ? userRepository.existsByUsername(dto.getUsername())
                : userDAO.existsByUsername(dto.getUsername());

        if (userExists) {
            throw new DuplicateUserException(
                    "Username '" + dto.getUsername() + "' is already taken.");
        }

        boolean emailExists = (userRepository != null)
                ? userRepository.existsByEmail(dto.getEmail())
                : userDAO.existsByEmail(dto.getEmail());

        if (emailExists) {
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

        User savedUser = (userRepository != null)
                ? userRepository.save(user)
                : userDAO.save(user);

        // Seed default categories for the new user
        if (categoryRepository != null) {
            for (String[] cat : DEFAULT_CATEGORIES) {
                Category category = new Category();
                category.setName(cat[0]);
                category.setDescription(cat[1]);
                category.setUserId(savedUser.getUserId());
                categoryRepository.save(category);
            }
        } else if (categoryDAO != null) {
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
    @Transactional(readOnly = true)
    public User login(String username, String password)
            throws ValidationException, InvalidCredentialsException, DAOException {

        ValidationUtil.validateNotEmpty(username, "Username");
        ValidationUtil.validateNotEmpty(password, "Password");

        User user;
        if (userRepository != null) {
            user = userRepository.findByUsername(username.trim())
                    .orElseThrow(InvalidCredentialsException::new);
        } else {
            user = userDAO.findByUsername(username.trim())
                    .orElseThrow(InvalidCredentialsException::new);
        }

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
    @Transactional(readOnly = true)
    public User getProfile(int userId) throws UserNotFoundException, DAOException {
        if (userRepository != null) {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
        }
        return userDAO.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public void updateProfile(int userId, UserDTO dto)
            throws ValidationException, UserNotFoundException, DAOException {

        ValidationUtil.validateNotEmpty(dto.getFirstName(), "First name");
        ValidationUtil.validateNotEmpty(dto.getLastName(),  "Last name");
        ValidationUtil.validateEmail(dto.getEmail());

        User existing;
        if (userRepository != null) {
            existing = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
        } else {
            existing = userDAO.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
        }

        existing.setFirstName(dto.getFirstName().trim());
        existing.setLastName(dto.getLastName().trim());
        existing.setEmail(dto.getEmail().trim().toLowerCase());
        existing.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);

        if (userRepository != null) {
            userRepository.save(existing);
        } else {
            userDAO.update(existing);
        }
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

        User user;
        if (userRepository != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
        } else {
            user = userDAO.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
        }

        if (!PasswordUtil.verifyPassword(currentPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect.");
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        if (userRepository != null) {
            user.setPasswordHash(newHash);
            userRepository.save(user);
        } else {
            userDAO.updatePassword(userId, newHash);
        }
        logger.info("Password changed for userId={}", userId);
    }
}
