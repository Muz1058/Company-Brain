package com.companybrain.service;

import com.companybrain.dao.UserDao;
import com.companybrain.exception.AuthenticationException;
import com.companybrain.exception.ValidationException;
import com.companybrain.model.User;
import com.companybrain.util.InputValidator;
import com.companybrain.util.PasswordHasher;

/**
 * Business service handling authentication logic.
 */
public class AuthService {
    private final UserDao userDao;
    private User currentUser;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Validates credentials and logs the user in.
     */
    public User login(String username, String password) throws AuthenticationException {
        if (InputValidator.isEmpty(username) || InputValidator.isEmpty(password)) {
            throw new AuthenticationException("Username and password cannot be empty.");
        }

        User user = userDao.findByUsername(username);
        if (user == null || !PasswordHasher.verify(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid username or password.");
        }

        currentUser = user;
        return currentUser;
    }

    /**
     * Logs the current user out.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Retrieves the current logged-in user.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Registers a new user with standard credentials.
     */
    public void registerUser(String username, String password, String role) throws ValidationException {
        if (!InputValidator.isValidUsername(username)) {
            throw new ValidationException("Username must be alphanumeric and 3-20 characters long.");
        }
        if (!InputValidator.isValidPassword(password)) {
            throw new ValidationException("Password must be at least 4 characters long.");
        }

        if (userDao.findByUsername(username) != null) {
            throw new ValidationException("Username already exists.");
        }

        User newUser = new User(0, username, PasswordHasher.hash(password), role);
        userDao.save(newUser);
    }
}
