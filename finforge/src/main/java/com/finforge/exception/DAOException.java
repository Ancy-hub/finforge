package com.finforge.exception;

/**
 * Thrown when a JDBC / database operation fails.
 */
public class DAOException extends FinForgeException {

    private static final long serialVersionUID = 1L;

    public DAOException(String message) {
        super(message);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
