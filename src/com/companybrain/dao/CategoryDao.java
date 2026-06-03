package com.companybrain.dao;

import com.companybrain.model.Category;

import java.util.List;

/**
 * Data Access Object interface for Category persistence.
 */
public interface CategoryDao {
    /**
     * Retrieves all categories.
     */
    List<Category> findAll();

    /**
     * Finds a category by its unique ID.
     */
    Category findById(int id);

    /**
     * Persists a new category.
     */
    void save(Category category);

    /**
     * Deletes a category by its ID.
     */
    void delete(int id);
}
