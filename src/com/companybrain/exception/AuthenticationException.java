package com.companybrain.exception;

/**
 * Exception thrown when authentication or login attempts fail.
 */
public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }
}
