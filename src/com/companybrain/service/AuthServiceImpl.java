package com.companybrain.service;

import com.companybrain.dao.UserDao;
import com.companybrain.exception.AuthenticationException;
import com.companybrain.exception.ValidationException;
import com.companybrain.model.User;
import com.companybrain.util.InputValidator;
import com.companybrain.util.PasswordHasher;


public class AuthServiceImpl implements AuthService {
    private final UserDao userDao;
    private User currentUser;

    public AuthServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
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

    @Override
    public void logout() {
        currentUser = null;
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
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
