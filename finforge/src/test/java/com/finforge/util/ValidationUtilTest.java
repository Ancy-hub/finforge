package com.finforge.util;

import com.finforge.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ValidationUtil}.
 */
@DisplayName("ValidationUtil Tests")
class ValidationUtilTest {

    // -----------------------------------------------------------------------
    // validateNotEmpty()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("validateNotEmpty() - should pass for a non-blank string")
    void validateNotEmpty_shouldPass_whenValueProvided() {
        assertDoesNotThrow(() ->
                ValidationUtil.validateNotEmpty("hello", "Field"));
    }

    @Test
    @DisplayName("validateNotEmpty() - should throw for null")
    void validateNotEmpty_shouldThrow_whenNull() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> ValidationUtil.validateNotEmpty(null, "Title"));
        assertEquals("Title", ex.getField());
    }

    @Test
    @DisplayName("validateNotEmpty() - should throw for empty string")
    void validateNotEmpty_shouldThrow_whenEmpty() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateNotEmpty("", "Source"));
    }

    @Test
    @DisplayName("validateNotEmpty() - should throw for whitespace-only string")
    void validateNotEmpty_shouldThrow_whenWhitespaceOnly() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateNotEmpty("   ", "Amount"));
    }

    // -----------------------------------------------------------------------
    // validateUsername()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("validateUsername() - should pass for valid username")
    void validateUsername_shouldPass_whenValid() {
        assertDoesNotThrow(() -> ValidationUtil.validateUsername("johndoe"));
        assertDoesNotThrow(() -> ValidationUtil.validateUsername("john_doe_99"));
        assertDoesNotThrow(() -> ValidationUtil.validateUsername("ABC"));
    }

    @Test
    @DisplayName("validateUsername() - should throw for username shorter than 3 chars")
    void validateUsername_shouldThrow_whenTooShort() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> ValidationUtil.validateUsername("ab"));
        assertEquals("Username", ex.getField());
        assertTrue(ex.getMessage().contains("3"));
    }

    @Test
    @DisplayName("validateUsername() - should throw for username longer than 50 chars")
    void validateUsername_shouldThrow_whenTooLong() {
        String longName = "a".repeat(51);
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateUsername(longName));
    }

    @ParameterizedTest(name = "invalid username: \"{0}\"")
    @ValueSource(strings = {"john doe", "john@doe", "john.doe", "john-doe", "john!doe"})
    @DisplayName("validateUsername() - should throw for usernames with invalid characters")
    void validateUsername_shouldThrow_whenContainsInvalidChars(String username) {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateUsername(username));
    }

    @Test
    @DisplayName("validateUsername() - should throw for blank username")
    void validateUsername_shouldThrow_whenBlank() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateUsername(""));
    }

    // -----------------------------------------------------------------------
    // validateEmail()
    // -----------------------------------------------------------------------

    @ParameterizedTest(name = "valid email: \"{0}\"")
    @ValueSource(strings = {
        "user@example.com",
        "user.name+tag@sub.domain.org",
        "a@b.io",
        "test_123@company.co.uk"
    })
    @DisplayName("validateEmail() - should pass for valid email addresses")
    void validateEmail_shouldPass_whenValid(String email) {
        assertDoesNotThrow(() -> ValidationUtil.validateEmail(email));
    }

    @ParameterizedTest(name = "invalid email: \"{0}\"")
    @ValueSource(strings = {
        "notanemail",
        "@nodomain.com",
        "missing@",
        "missing@domain",
        "two@@at.com",
        ""
    })
    @DisplayName("validateEmail() - should throw for invalid email addresses")
    void validateEmail_shouldThrow_whenInvalid(String email) {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateEmail(email));
    }

    @Test
    @DisplayName("validateEmail() - should throw for null email")
    void validateEmail_shouldThrow_whenNull() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateEmail(null));
    }

    // -----------------------------------------------------------------------
    // validatePassword()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("validatePassword() - should pass for password >= 8 characters")
    void validatePassword_shouldPass_whenSufficientLength() {
        assertDoesNotThrow(() -> ValidationUtil.validatePassword("12345678"));
        assertDoesNotThrow(() -> ValidationUtil.validatePassword("LongPassw0rd!"));
    }

    @Test
    @DisplayName("validatePassword() - should throw for password shorter than 8 characters")
    void validatePassword_shouldThrow_whenTooShort() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> ValidationUtil.validatePassword("short"));
        assertEquals("Password", ex.getField());
        assertTrue(ex.getMessage().contains("8"));
    }

    @Test
    @DisplayName("validatePassword() - should throw for blank password")
    void validatePassword_shouldThrow_whenBlank() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validatePassword(""));
    }

    @Test
    @DisplayName("validatePassword() - should throw for null password")
    void validatePassword_shouldThrow_whenNull() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validatePassword(null));
    }

    // -----------------------------------------------------------------------
    // validatePasswordsMatch()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("validatePasswordsMatch() - should pass when passwords are identical")
    void validatePasswordsMatch_shouldPass_whenIdentical() {
        assertDoesNotThrow(() ->
                ValidationUtil.validatePasswordsMatch("SecurePass1!", "SecurePass1!"));
    }

    @Test
    @DisplayName("validatePasswordsMatch() - should throw when passwords differ")
    void validatePasswordsMatch_shouldThrow_whenDifferent() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> ValidationUtil.validatePasswordsMatch("Pass1!", "Pass2@"));
        assertEquals("ConfirmPassword", ex.getField());
    }

    @Test
    @DisplayName("validatePasswordsMatch() - should throw when confirm password has trailing space")
    void validatePasswordsMatch_shouldThrow_whenTrailingSpaceDifference() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validatePasswordsMatch("Password1!", "Password1! "));
    }

    @Test
    @DisplayName("validatePasswordsMatch() - should be case-sensitive")
    void validatePasswordsMatch_shouldBeCaseSensitive() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validatePasswordsMatch("password1!", "PASSWORD1!"));
    }

    // -----------------------------------------------------------------------
    // validateAmount()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("validateAmount() - should return BigDecimal for valid positive amount")
    void validateAmount_shouldReturnBigDecimal_whenValid() throws Exception {
        BigDecimal result = ValidationUtil.validateAmount("150.75", "Amount");

        assertNotNull(result);
        assertEquals(0, new BigDecimal("150.75").compareTo(result));
    }

    @Test
    @DisplayName("validateAmount() - should accept whole number amounts")
    void validateAmount_shouldAcceptWholeNumber() throws Exception {
        BigDecimal result = ValidationUtil.validateAmount("500", "Amount");
        assertEquals(0, new BigDecimal("500").compareTo(result));
    }

    @Test
    @DisplayName("validateAmount() - should throw for zero amount")
    void validateAmount_shouldThrow_whenZero() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> ValidationUtil.validateAmount("0", "Amount"));
        assertEquals("Amount", ex.getField());
        assertTrue(ex.getMessage().contains("greater than zero"));
    }

    @Test
    @DisplayName("validateAmount() - should throw for negative amount")
    void validateAmount_shouldThrow_whenNegative() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateAmount("-10.50", "Amount"));
    }

    @Test
    @DisplayName("validateAmount() - should throw for non-numeric string")
    void validateAmount_shouldThrow_whenNonNumeric() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> ValidationUtil.validateAmount("abc", "Amount"));
        assertEquals("Amount", ex.getField());
        assertTrue(ex.getMessage().contains("numeric"));
    }

    @Test
    @DisplayName("validateAmount() - should throw for blank amount")
    void validateAmount_shouldThrow_whenBlank() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateAmount("", "Amount"));
    }

    @Test
    @DisplayName("validateAmount() - should throw for null amount")
    void validateAmount_shouldThrow_whenNull() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateAmount(null, "Amount"));
    }

    @Test
    @DisplayName("validateAmount() - should accept the smallest valid positive amount (0.01)")
    void validateAmount_shouldAccept_smallestPositiveAmount() throws Exception {
        BigDecimal result = ValidationUtil.validateAmount("0.01", "Amount");
        assertEquals(0, new BigDecimal("0.01").compareTo(result));
    }

    // -----------------------------------------------------------------------
    // validateDate()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("validateDate() - should return LocalDate for valid ISO date string")
    void validateDate_shouldReturnLocalDate_whenValid() throws Exception {
        LocalDate result = ValidationUtil.validateDate("2025-06-15", "Date");

        assertNotNull(result);
        assertEquals(LocalDate.of(2025, 6, 15), result);
    }

    @Test
    @DisplayName("validateDate() - should accept today's date")
    void validateDate_shouldAcceptToday() throws Exception {
        String today = LocalDate.now().toString();
        assertDoesNotThrow(() -> ValidationUtil.validateDate(today, "Date"));
    }

    @ParameterizedTest(name = "invalid date: \"{0}\"")
    @ValueSource(strings = {
        "15-06-2025",
        "2025/06/15",
        "June 15 2025",
        "2025-13-01",
        "2025-00-10",
        "not-a-date"
    })
    @DisplayName("validateDate() - should throw for invalid date formats")
    void validateDate_shouldThrow_whenInvalidFormat(String date) {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> ValidationUtil.validateDate(date, "Expense date"));
        assertEquals("Expense date", ex.getField());
    }

    @Test
    @DisplayName("validateDate() - should throw for blank date")
    void validateDate_shouldThrow_whenBlank() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateDate("", "Date"));
    }

    @Test
    @DisplayName("validateDate() - should throw for null date")
    void validateDate_shouldThrow_whenNull() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateDate(null, "Date"));
    }

    // -----------------------------------------------------------------------
    // validateId()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("validateId() - should return integer for valid positive ID string")
    void validateId_shouldReturnInt_whenValid() throws Exception {
        int result = ValidationUtil.validateId("42", "Category ID");
        assertEquals(42, result);
    }

    @Test
    @DisplayName("validateId() - should accept ID of 1 (minimum valid positive integer)")
    void validateId_shouldAccept_minimumValidId() throws Exception {
        assertEquals(1, ValidationUtil.validateId("1", "ID"));
    }

    @Test
    @DisplayName("validateId() - should throw for zero ID")
    void validateId_shouldThrow_whenZero() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> ValidationUtil.validateId("0", "Category ID"));
        assertEquals("Category ID", ex.getField());
    }

    @Test
    @DisplayName("validateId() - should throw for negative ID")
    void validateId_shouldThrow_whenNegative() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateId("-5", "ID"));
    }

    @Test
    @DisplayName("validateId() - should throw for non-integer string")
    void validateId_shouldThrow_whenNonInteger() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> ValidationUtil.validateId("abc", "ID"));
        assertTrue(ex.getMessage().contains("integer"));
    }

    @Test
    @DisplayName("validateId() - should throw for decimal string")
    void validateId_shouldThrow_whenDecimal() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateId("1.5", "ID"));
    }

    @Test
    @DisplayName("validateId() - should throw for blank ID")
    void validateId_shouldThrow_whenBlank() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateId("", "ID"));
    }

    @Test
    @DisplayName("validateId() - should trim whitespace before parsing")
    void validateId_shouldTrimWhitespace() throws Exception {
        assertEquals(7, ValidationUtil.validateId("  7  ", "ID"));
    }
}
