package com.companybrain.service;

import com.companybrain.exception.ValidationException;
import com.companybrain.model.Category;

import java.util.List;


public interface CategoryService {

    
    List<Category> getAllCategories();

    
    Category getCategoryById(int id);

    
    void createCategory(String name) throws ValidationException;

    
    void deleteCategory(int id);
}
