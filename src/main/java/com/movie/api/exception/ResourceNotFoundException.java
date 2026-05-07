package com.movie.api.exception;

/**
 * Thrown when a requested resource (Movie, User, etc.) is not found.
 * Maps to HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " with id " + id + " not found");
    }
}
