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

    public AuthServiceImpl(UserDao userDao) { this.userDao = userDao; }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Override
    public User login(String employeeId, String username, String password)
            throws AuthenticationException {

        if (InputValidator.isEmpty(username) || InputValidator.isEmpty(password)) {
            throw new AuthenticationException("Username and password cannot be empty.");
        }

        boolean isAdminLogin = "admin".equalsIgnoreCase(username.trim());

        // Employee ID mandatory for non-admin
        if (!isAdminLogin && InputValidator.isEmpty(employeeId)) {
            throw new AuthenticationException("Employee ID is required.");
        }

        User user = userDao.findByUsername(username.trim());

        // Generic message — don't reveal whether username exists
        if (user == null) {
            throw new AuthenticationException("Invalid credentials.");
        }

        // Disabled check — specific message per spec
        if (!user.isActive()) {
            throw new AuthenticationException(
                "Your account has been disabled. Please contact the administrator.");
        }

        // Password check
        if (!PasswordHasher.verify(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid credentials.");
        }

        // Employee ID check (skip for admin)
        if (!isAdminLogin) {
            String stored = user.getEmployeeId();
            if (stored == null || !stored.equalsIgnoreCase(employeeId.trim())) {
                throw new AuthenticationException("Invalid credentials.");
            }
        }

        currentUser = user;
        return currentUser;
    }

    @Override
    public void logout() { currentUser = null; }

    @Override
    public User getCurrentUser() { return currentUser; }

    // ── Create User (admin-only) ──────────────────────────────────────────────

    @Override
    public void createUser(String employeeId, String username, String password,
                           String role, User actingAdmin) throws ValidationException {

        if (actingAdmin == null || !actingAdmin.isAdmin()) {
            throw new ValidationException("Only administrators can create user accounts.");
        }
        if (InputValidator.isEmpty(employeeId)) {
            throw new ValidationException("Employee ID is required.");
        }
        if (!InputValidator.isValidEmployeeId(employeeId)) {
            throw new ValidationException(
                "Employee ID must be 3-20 alphanumeric characters (hyphens allowed).");
        }
        if (!InputValidator.isValidUsername(username)) {
            throw new ValidationException(
                "Username must be 3-20 alphanumeric/underscore characters.");
        }
        if (!InputValidator.isValidPassword(password)) {
            throw new ValidationException("Password must be at least 4 characters.");
        }
        if (InputValidator.isEmpty(role)) {
            throw new ValidationException("Role is required.");
        }
        if (userDao.findByUsername(username.trim()) != null) {
            throw new ValidationException("Username '" + username.trim() + "' is already taken.");
        }
        if (userDao.findByEmployeeId(employeeId.trim().toUpperCase()) != null) {
            throw new ValidationException(
                "Employee ID '" + employeeId.trim().toUpperCase() + "' is already assigned.");
        }

        User newUser = new User(0, employeeId.trim().toUpperCase(), username.trim(),
                                PasswordHasher.hash(password), role.toUpperCase(), true, null);
        userDao.save(newUser);
    }
}