package com.finforge.exception;

/**
 * Base checked exception for all application-level errors in Smart FinForge.
 */
public class FinForgeException extends Exception {

    private static final long serialVersionUID = 1L;

    public FinForgeException(String message) {
        super(message);
    }

    public FinForgeException(String message, Throwable cause) {
        super(message, cause);
    }
}
