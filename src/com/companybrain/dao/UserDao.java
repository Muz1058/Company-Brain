package com.companybrain.dao;

import com.companybrain.model.User;


public interface UserDao {
    
    User findByUsername(String username);

    
    void save(User user);
}
