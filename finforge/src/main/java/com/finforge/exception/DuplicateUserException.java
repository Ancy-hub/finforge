package com.finforge.exception;

/**
 * Thrown when a registration attempt conflicts with an existing username or e-mail.
 */
public class DuplicateUserException extends FinForgeException {

    private static final long serialVersionUID = 1L;

    public DuplicateUserException(String message) {
        super(message);
    }
}
