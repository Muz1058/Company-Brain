package com.companybrain.service;

import com.companybrain.exception.AuthenticationException;
import com.companybrain.exception.ValidationException;
import com.companybrain.model.User;


public interface AuthService {
    
    
    User login(String username, String password) throws AuthenticationException;

    
    void logout();

    
    User getCurrentUser();

    
    void registerUser(String username, String password, String role) throws ValidationException;
}
