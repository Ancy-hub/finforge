package com.finforge.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for hashing and verifying passwords using SHA-256.
 */
public final class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";

    private PasswordUtil() {
        // Utility class — no instances
    }

    /**
     * Hashes a plain-text password using SHA-256 and returns the hex-encoded digest.
     *
     * @param plainPassword the raw password (never stored)
     * @return 64-character lowercase hex string
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password must not be null or empty.");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(64);
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) {
                    hex.append('0');
                }
                hex.append(h);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed by the JVM spec
            throw new RuntimeException("SHA-256 algorithm unavailable", e);
        }
    }

    /**
     * Verifies a plain-text password against a stored SHA-256 hash.
     *
     * @param plainPassword  the candidate password
     * @param storedHash     the hash retrieved from the database
     * @return {@code true} if the passwords match
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        return hashPassword(plainPassword).equals(storedHash);
    }
}
