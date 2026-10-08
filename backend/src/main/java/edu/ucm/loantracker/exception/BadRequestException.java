package edu.ucm.loantracker.exception;

/** Turned into an HTTP 400 response by GlobalExceptionHandler. */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
