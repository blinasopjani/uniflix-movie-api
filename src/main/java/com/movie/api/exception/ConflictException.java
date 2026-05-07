package com.movie.api.exception;

/**
 * Thrown when a business rule is violated (e.g., duplicate rating, already favorited).
 * Maps to HTTP 409 Conflict.
 */
public class ConflictException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ConflictException(String message) {
        super(message);
    }
}
