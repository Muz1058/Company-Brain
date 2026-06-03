package com.companybrain.service;

import com.companybrain.dao.CategoryDao;
import com.companybrain.exception.ValidationException;
import com.companybrain.model.Category;
import com.companybrain.util.InputValidator;

import java.util.List;

/**
 * Concrete implementation of the CategoryService interface.
 */
public class CategoryServiceImpl implements CategoryService {
    private final CategoryDao categoryDao;

    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }

    @Override
    public Category getCategoryById(int id) {
        return categoryDao.findById(id);
    }

    @Override
    public void createCategory(String name) throws ValidationException {
        if (InputValidator.isEmpty(name)) {
            throw new ValidationException("Category name cannot be empty.");
        }

        // Check if name already exists
        for (Category cat : categoryDao.findAll()) {
            if (cat.getName().equalsIgnoreCase(name.trim())) {
                throw new ValidationException("Category with this name already exists.");
            }
        }

        Category category = new Category(0, name.trim());
        categoryDao.save(category);
    }

    @Override
    public void deleteCategory(int id) {
        categoryDao.delete(id);
    }
}
