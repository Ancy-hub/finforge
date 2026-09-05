package com.finforge.exception;

/**
 * Thrown when input data fails validation rules.
 */
public class ValidationException extends FinForgeException {

    private static final long serialVersionUID = 1L;

    private final String field;

    public ValidationException(String message) {
        super(message);
        this.field = null;
    }

    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    /**
     * Returns the name of the field that failed validation, or {@code null} if unspecified.
     */
    public String getField() {
        return field;
    }
}
