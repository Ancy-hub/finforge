package com.finforge.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PasswordUtil}.
 * These tests guard the correctness and security of password hashing.
 */
@DisplayName("PasswordUtil Tests")
class PasswordUtilTest {

    // -----------------------------------------------------------------------
    // hashPassword()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("hashPassword() - should return a 64-character hex string for any input")
    void hashPassword_shouldReturn64CharHexString() {
        String hash = PasswordUtil.hashPassword("SomePassword1!");

        assertNotNull(hash);
        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]{64}"),
                "Hash must be lowercase hex");
    }

    @Test
    @DisplayName("hashPassword() - same input should always produce the same hash (deterministic)")
    void hashPassword_shouldBeDeterministic() {
        String first  = PasswordUtil.hashPassword("MyPassword@99");
        String second = PasswordUtil.hashPassword("MyPassword@99");

        assertEquals(first, second);
    }

    @Test
    @DisplayName("hashPassword() - different passwords should produce different hashes")
    void hashPassword_shouldProduceDifferentHashes_forDifferentPasswords() {
        String hash1 = PasswordUtil.hashPassword("PasswordOne1!");
        String hash2 = PasswordUtil.hashPassword("PasswordTwo2@");

        assertNotEquals(hash1, hash2);
    }

    @Test
    @DisplayName("hashPassword() - passwords differing only by case should produce different hashes")
    void hashPassword_shouldBeCaseSensitive() {
        String lower = PasswordUtil.hashPassword("password");
        String upper = PasswordUtil.hashPassword("PASSWORD");
        String mixed = PasswordUtil.hashPassword("Password");

        assertNotEquals(lower, upper);
        assertNotEquals(lower, mixed);
        assertNotEquals(upper, mixed);
    }

    @Test
    @DisplayName("hashPassword() - a single character difference should produce a completely different hash")
    void hashPassword_avalancheEffect_oneCharDifference() {
        String hash1 = PasswordUtil.hashPassword("SecurePass1!");
        String hash2 = PasswordUtil.hashPassword("SecurePass1@");

        assertNotEquals(hash1, hash2);
        // SHA-256 avalanche: expect > 10 characters to differ
        int differences = 0;
        for (int i = 0; i < hash1.length(); i++) {
            if (hash1.charAt(i) != hash2.charAt(i)) differences++;
        }
        assertTrue(differences > 10,
                "SHA-256 avalanche effect: at least 10 hex chars should differ");
    }

    @Test
    @DisplayName("hashPassword() - hash must be lowercase hex only")
    void hashPassword_shouldBeLowercaseHexOnly() {
        String hash = PasswordUtil.hashPassword("TestPassword1!");

        assertEquals(hash.toLowerCase(), hash,
                "Hash must not contain uppercase characters");
        assertTrue(hash.matches("[0-9a-f]+"),
                "Hash must contain only hex characters [0-9a-f]");
    }

    @Test
    @DisplayName("hashPassword() - should handle special characters without throwing")
    void hashPassword_shouldHandleSpecialCharacters() {
        assertDoesNotThrow(() -> PasswordUtil.hashPassword("P@$$w0rd!#&*()"));
        assertDoesNotThrow(() -> PasswordUtil.hashPassword("Sécurité€£¥"));
        assertDoesNotThrow(() -> PasswordUtil.hashPassword("密码123!"));
    }

    @Test
    @DisplayName("hashPassword() - should handle minimum-length password (8 chars)")
    void hashPassword_shouldHandleMinimumLengthPassword() {
        String hash = PasswordUtil.hashPassword("Abcd1234");

        assertNotNull(hash);
        assertEquals(64, hash.length());
    }

    @Test
    @DisplayName("hashPassword() - should throw IllegalArgumentException for null input")
    void hashPassword_shouldThrow_whenNull() {
        assertThrows(IllegalArgumentException.class,
                () -> PasswordUtil.hashPassword(null));
    }

    @Test
    @DisplayName("hashPassword() - should throw IllegalArgumentException for empty string")
    void hashPassword_shouldThrow_whenEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> PasswordUtil.hashPassword(""));
    }

    @Test
    @DisplayName("hashPassword() - output should match independent SHA-256 computation via MessageDigest")
    void hashPassword_algorithmVerification_matchesDirectMessageDigest()
            throws NoSuchAlgorithmException {
        // Cross-verify: PasswordUtil must produce the same bytes as a direct
        // MessageDigest.getInstance("SHA-256") call with UTF-8 encoding.
        // This catches wrong-algorithm bugs (e.g. SHA-1 instead of SHA-256)
        // without relying on a manually typed hex constant.
        String input = "CrossCheckValue_42!";
        String fromUtil = PasswordUtil.hashPassword(input);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] raw = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder expected = new StringBuilder(64);
        for (byte b : raw) {
            expected.append(String.format("%02x", b));
        }

        assertEquals(expected.toString(), fromUtil,
                "PasswordUtil must use SHA-256 with UTF-8 byte encoding");
    }

    // -----------------------------------------------------------------------
    // verifyPassword()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("verifyPassword() - should return true when plain password matches stored hash")
    void verifyPassword_shouldReturnTrue_whenPasswordMatches() {
        String plain = "MySecurePass1!";
        String hash  = PasswordUtil.hashPassword(plain);

        assertTrue(PasswordUtil.verifyPassword(plain, hash));
    }

    @Test
    @DisplayName("verifyPassword() - should return false when plain password does not match hash")
    void verifyPassword_shouldReturnFalse_whenPasswordDoesNotMatch() {
        String hash = PasswordUtil.hashPassword("CorrectPassword1!");

        assertFalse(PasswordUtil.verifyPassword("WrongPassword1!", hash));
    }

    @Test
    @DisplayName("verifyPassword() - should return false when plain password is null")
    void verifyPassword_shouldReturnFalse_whenPlainIsNull() {
        String hash = PasswordUtil.hashPassword("SomePassword1!");

        assertFalse(PasswordUtil.verifyPassword(null, hash));
    }

    @Test
    @DisplayName("verifyPassword() - should return false when stored hash is null")
    void verifyPassword_shouldReturnFalse_whenStoredHashIsNull() {
        assertFalse(PasswordUtil.verifyPassword("SomePassword1!", null));
    }

    @Test
    @DisplayName("verifyPassword() - should return false when both arguments are null")
    void verifyPassword_shouldReturnFalse_whenBothNull() {
        assertFalse(PasswordUtil.verifyPassword(null, null));
    }

    @Test
    @DisplayName("verifyPassword() - should return false for empty string against valid hash")
    void verifyPassword_shouldReturnFalse_whenPlainIsEmpty() {
        String hash = PasswordUtil.hashPassword("RealPassword1!");

        assertFalse(PasswordUtil.verifyPassword("", hash));
    }

    @Test
    @DisplayName("verifyPassword() - hash-then-verify round-trip should always succeed")
    void verifyPassword_roundTrip_shouldAlwaysSucceed() {
        String[] passwords = {
            "SimplePass1!",
            "P@$$w0rd#2025",
            "LongPasswordWithSpecialChars!@#$%^&*()",
            "  spaces  around  "
        };

        for (String password : passwords) {
            String hash = PasswordUtil.hashPassword(password);
            assertTrue(PasswordUtil.verifyPassword(password, hash),
                    "Round-trip failed for: " + password);
        }
    }

    @Test
    @DisplayName("verifyPassword() - should be case-sensitive for plain password comparison")
    void verifyPassword_shouldBeCaseSensitive() {
        String original = "MyPassword1!";
        String hash     = PasswordUtil.hashPassword(original);

        assertFalse(PasswordUtil.verifyPassword("MYPASSWORD1!", hash));
        assertFalse(PasswordUtil.verifyPassword("mypassword1!", hash));
        assertTrue(PasswordUtil.verifyPassword("MyPassword1!", hash));
    }
}
