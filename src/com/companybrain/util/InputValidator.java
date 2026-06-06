package com.companybrain.util;

public class InputValidator {

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isValidUsername(String username) {
        if (isEmpty(username)) return false;
        return username.matches("^[a-zA-Z0-9_]{3,20}$");
    }

    public static boolean isValidPassword(String password) {
        return !isEmpty(password) && password.length() >= 4;
    }

    // ── NEW ───────────────────────────────────────────────────────────────────
    public static boolean isValidEmployeeId(String empId) {
        if (isEmpty(empId)) return false;
        return empId.trim().matches("^[A-Za-z0-9\\-]{3,20}$");
    }
    // ─────────────────────────────────────────────────────────────────────────

    public static void validateKnowledgeEntry(String title, String content)
            throws IllegalArgumentException {
        if (isEmpty(title))   throw new IllegalArgumentException("Title cannot be empty.");
        if (isEmpty(content)) throw new IllegalArgumentException("Content cannot be empty.");
    }
}