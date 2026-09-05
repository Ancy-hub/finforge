package com.finforge.exception;

/**
 * Thrown when login credentials (username / password) are incorrect.
 */
public class InvalidCredentialsException extends FinForgeException {

    private static final long serialVersionUID = 1L;

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException() {
        super("Invalid username or password.");
    }
}
