package com.companybrain.dao;

import com.companybrain.model.User;
import java.util.List;

public interface UserDao {
    // existing
    User findByUsername(String username);
    void save(User user);

    // new
    User   findById(int id);
    User   findByEmployeeId(String employeeId);
    List<User> findAll();
    List<User> search(String keyword);
    void   updateStatus(int userId, boolean active);
    void   updatePassword(int userId, String newPasswordHash);
}