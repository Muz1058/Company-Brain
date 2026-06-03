package com.companybrain.exception;

/**
 * Exception thrown when validation of data models or inputs fails.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
