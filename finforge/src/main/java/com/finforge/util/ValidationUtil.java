package com.finforge.util;

import com.finforge.exception.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Utility class with reusable validation helpers.
 * Each method throws {@link ValidationException} on failure so callers
 * can catch it and surface the error message directly in the UI.
 */
public final class ValidationUtil {

    private ValidationUtil() {
        // Utility class — no instances
    }

    // ----------------------------------------------------------------
    // General
    // ----------------------------------------------------------------

    public static void validateNotEmpty(String value, String fieldName)
            throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName, fieldName + " cannot be empty.");
        }
    }

    // ----------------------------------------------------------------
    // User fields
    // ----------------------------------------------------------------

    public static void validateUsername(String username) throws ValidationException {
        validateNotEmpty(username, "Username");
        String trimmed = username.trim();
        if (trimmed.length() < 3 || trimmed.length() > 50) {
            throw new ValidationException("Username",
                    "Username must be between 3 and 50 characters.");
        }
        if (!trimmed.matches("^[A-Za-z0-9_]+$")) {
            throw new ValidationException("Username",
                    "Username may only contain letters, digits, and underscores.");
        }
    }

    public static void validateEmail(String email) throws ValidationException {
        validateNotEmpty(email, "Email");
        if (!email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Email", "Invalid email address format.");
        }
    }

    public static void validatePassword(String password) throws ValidationException {
        validateNotEmpty(password, "Password");
        if (password.length() < 8) {
            throw new ValidationException("Password",
                    "Password must be at least 8 characters long.");
        }
    }

    public static void validatePasswordsMatch(String password, String confirmPassword)
            throws ValidationException {
        if (!password.equals(confirmPassword)) {
            throw new ValidationException("ConfirmPassword",
                    "Passwords do not match.");
        }
    }

    // ----------------------------------------------------------------
    // Numeric / date fields
    // ----------------------------------------------------------------

    /**
     * Parses and validates a monetary amount string.
     *
     * @param amountStr raw string from the HTTP form
     * @param fieldName human-readable label for error messages
     * @return parsed {@link BigDecimal} value
     * @throws ValidationException if blank, non-numeric, or ≤ 0
     */
    public static BigDecimal validateAmount(String amountStr, String fieldName)
            throws ValidationException {
        validateNotEmpty(amountStr, fieldName);
        try {
            BigDecimal amount = new BigDecimal(amountStr.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException(fieldName,
                        fieldName + " must be greater than zero.");
            }
            return amount;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName,
                    fieldName + " must be a valid numeric value.");
        }
    }

    /**
     * Parses and validates a date string in ISO format (YYYY-MM-DD).
     *
     * @param dateStr   raw string from the HTTP form
     * @param fieldName human-readable label for error messages
     * @return parsed {@link LocalDate}
     * @throws ValidationException if blank or not a valid date
     */
    public static LocalDate validateDate(String dateStr, String fieldName)
            throws ValidationException {
        validateNotEmpty(dateStr, fieldName);
        try {
            return LocalDate.parse(dateStr.trim());
        } catch (DateTimeParseException e) {
            throw new ValidationException(fieldName,
                    fieldName + " must be a valid date in YYYY-MM-DD format.");
        }
    }

    /**
     * Parses and validates that an ID string represents a positive integer.
     *
     * @param idStr     raw string from the HTTP form or URL param
     * @param fieldName human-readable label for error messages
     * @return parsed positive integer
     * @throws ValidationException if blank or not a positive integer
     */
    public static int validateId(String idStr, String fieldName)
            throws ValidationException {
        validateNotEmpty(idStr, fieldName);
        try {
            int id = Integer.parseInt(idStr.trim());
            if (id <= 0) {
                throw new ValidationException(fieldName,
                        fieldName + " must be a positive integer.");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName,
                    fieldName + " must be a valid integer.");
        }
    }
}
