package com.companybrain.util;

/**
 * Utility for input validation.
 */
public class InputValidator {

    /**
     * Checks if a string is null or empty (including whitespace).
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Validates a username.
     */
    public static boolean isValidUsername(String username) {
        if (isEmpty(username)) {
            return false;
        }
        // Example rule: 3-20 characters, alphanumeric
        return username.matches("^[a-zA-Z0-9_]{3,20}$");
    }

    /**
     * Validates a password strength.
     */
    public static boolean isValidPassword(String password) {
        if (isEmpty(password)) {
            return false;
        }
        // Example rule: At least 4 characters
        return password.length() >= 4;
    }

    /**
     * Validates knowledge entry fields.
     */
    public static void validateKnowledgeEntry(String title, String content) throws IllegalArgumentException {
        if (isEmpty(title)) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        if (isEmpty(content)) {
            throw new IllegalArgumentException("Content cannot be empty.");
        }
    }
}
