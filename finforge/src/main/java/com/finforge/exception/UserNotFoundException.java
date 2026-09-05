package com.finforge.exception;

/**
 * Thrown when a lookup for a user returns no result.
 */
public class UserNotFoundException extends FinForgeException {

    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(int userId) {
        super("User not found with ID: " + userId);
    }
}
