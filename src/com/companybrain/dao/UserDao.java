package com.companybrain.dao;

import com.companybrain.model.User;

/**
 * Data Access Object interface for User persistence.
 */
public interface UserDao {
    /**
     * Finds a user by their username.
     */
    User findByUsername(String username);

    /**
     * Persists a new user.
     */
    void save(User user);
}
