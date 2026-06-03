package com.companybrain.service;

import com.companybrain.exception.ValidationException;
import com.companybrain.model.Category;

import java.util.List;

/**
 * Service interface for managing category operations.
 */
public interface CategoryService {

    /**
     * Gets all category rows.
     */
    List<Category> getAllCategories();

    /**
     * Retrieves a category by ID.
     */
    Category getCategoryById(int id);

    /**
     * Creates and saves a new category.
     */
    void createCategory(String name) throws ValidationException;

    /**
     * Deletes an existing category.
     */
    void deleteCategory(int id);
}
