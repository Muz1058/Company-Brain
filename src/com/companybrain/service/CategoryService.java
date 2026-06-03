package com.companybrain.service;

import com.companybrain.dao.CategoryDao;
import com.companybrain.exception.ValidationException;
import com.companybrain.model.Category;
import com.companybrain.util.InputValidator;

import java.util.List;

/**
 * Business service managing categories.
 */
public class CategoryService {
    private final CategoryDao categoryDao;

    public CategoryService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    /**
     * Retrieves all categories.
     */
    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }

    /**
     * Finds a single category by ID.
     */
    public Category getCategoryById(int id) {
        return categoryDao.findById(id);
    }

    /**
     * Creates and saves a new category.
     */
    public void createCategory(String name) throws ValidationException {
        if (InputValidator.isEmpty(name)) {
            throw new ValidationException("Category name cannot be empty.");
        }

        // Check if name already exists (simple uniqueness validation)
        for (Category cat : categoryDao.findAll()) {
            if (cat.getName().equalsIgnoreCase(name.trim())) {
                throw new ValidationException("Category with this name already exists.");
            }
        }

        Category category = new Category(0, name.trim());
        categoryDao.save(category);
    }

    /**
     * Deletes a category by ID.
     */
    public void deleteCategory(int id) {
        categoryDao.delete(id);
    }
}
