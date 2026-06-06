package com.companybrain.service;

import com.companybrain.dao.UserDao;
import com.companybrain.exception.ValidationException;
import com.companybrain.model.User;
import com.companybrain.util.InputValidator;
import com.companybrain.util.PasswordHasher;
import java.util.List;

public class UserManagementServiceImpl implements UserManagementService {

    private final UserDao userDao;

    public UserManagementServiceImpl(UserDao userDao) { this.userDao = userDao; }

    @Override public List<User> getAllUsers()             { return userDao.findAll(); }
    @Override public List<User> searchUsers(String kw)   {
        return InputValidator.isEmpty(kw) ? getAllUsers() : userDao.search(kw.trim());
    }

    @Override
    public void disableUser(int userId, User admin) throws ValidationException {
        requireAdmin(admin);
        if (admin.getId() == userId)
            throw new ValidationException("You cannot disable your own account.");
        User target = require(userDao.findById(userId));
        if (!target.isActive()) throw new ValidationException("User is already disabled.");
        userDao.updateStatus(userId, false);
    }

    @Override
    public void enableUser(int userId, User admin) throws ValidationException {
        requireAdmin(admin);
        User target = require(userDao.findById(userId));
        if (target.isActive()) throw new ValidationException("User is already active.");
        userDao.updateStatus(userId, true);
    }

    @Override
    public void resetPassword(int userId, String newPassword, User admin) throws ValidationException {
        requireAdmin(admin);
        if (!InputValidator.isValidPassword(newPassword))
            throw new ValidationException("Password must be at least 4 characters.");
        require(userDao.findById(userId));
        userDao.updatePassword(userId, PasswordHasher.hash(newPassword));
    }

    private void requireAdmin(User actor) throws ValidationException {
        if (actor == null || !actor.isAdmin())
            throw new ValidationException("Only administrators can perform this action.");
    }

    private User require(User user) throws ValidationException {
        if (user == null) throw new ValidationException("User not found.");
        return user;
    }
}
