package com.companybrain.service;

import com.companybrain.exception.AuthenticationException;
import com.companybrain.exception.ValidationException;
import com.companybrain.model.User;

/**
 * Service interface for handling user authentication and sessions.
 */
public interface AuthService {
    
    /**
     * Authenticates a user against stored credentials.
     */
    User login(String username, String password) throws AuthenticationException;

    /**
     * Terminate the active session.
     */
    void logout();

    /**
     * Retrieves details of the logged in user.
     */
    User getCurrentUser();

    /**
     * Registers a new user.
     */
    void registerUser(String username, String password, String role) throws ValidationException;
}
