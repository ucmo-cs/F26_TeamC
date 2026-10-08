package edu.ucm.loantracker.exception;

/** Turned into an HTTP 404 response by GlobalExceptionHandler. */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
