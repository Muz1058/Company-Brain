package com.companybrain.service;

import com.companybrain.exception.ValidationException;
import com.companybrain.model.User;
import java.util.List;

public interface UserManagementService {
    List<User> getAllUsers();
    List<User> searchUsers(String keyword);
    void disableUser(int userId, User actingAdmin)  throws ValidationException;
    void enableUser(int userId, User actingAdmin)   throws ValidationException;
    void resetPassword(int userId, String newPassword, User actingAdmin) throws ValidationException;
}
