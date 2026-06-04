package com.companybrain.dao;

import com.companybrain.model.Category;

import java.util.List;


public interface CategoryDao {
    
    List<Category> findAll();

    
    Category findById(int id);

    
    void save(Category category);

    
    void delete(int id);
}
