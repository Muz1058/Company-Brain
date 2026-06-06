package com.companybrain.service;

import com.companybrain.exception.AuthenticationException;
import com.companybrain.exception.ValidationException;
import com.companybrain.model.User;

public interface AuthService {

    /**
     * Admin:    employeeId may be null/blank — only username+password checked.
     * Employee: all three fields must match.
     */
    User login(String employeeId, String username, String password)
            throws AuthenticationException;

    void logout();

    User getCurrentUser();

    /** Admin-only: creates a new employee account. */
    void createUser(String employeeId, String username, String password,
                    String role, User actingAdmin) throws ValidationException;

    /**
     * @deprecated Self-registration is disabled. Only admins can create accounts.
     * Kept so SignUpController still compiles during transition.
     * SignUpController and SignUpView should be deleted from the project entirely.
     */
    @Deprecated
    default void registerUser(String username, String password, String role)
            throws ValidationException {
        throw new ValidationException(
            "Self-registration is disabled. Please contact the administrator.");
    }
}