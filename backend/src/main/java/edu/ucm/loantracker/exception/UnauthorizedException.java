package edu.ucm.loantracker.exception;

/** Turned into an HTTP 401 response by GlobalExceptionHandler. */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
