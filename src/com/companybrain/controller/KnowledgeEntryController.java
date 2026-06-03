package com.companybrain.controller;

import com.companybrain.exception.ValidationException;
import com.companybrain.model.Category;
import com.companybrain.model.KnowledgeEntry;
import com.companybrain.service.CategoryService;
import com.companybrain.service.KnowledgeService;
import com.companybrain.view.KnowledgeEntryForm;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Controller managing the JDialog CRUD form for Knowledge Entries.
 */
public class KnowledgeEntryController {
    private final KnowledgeService knowledgeService;
    private final CategoryService categoryService;

    public KnowledgeEntryController(KnowledgeService knowledgeService, CategoryService categoryService) {
        this.knowledgeService = knowledgeService;
        this.categoryService = categoryService;
    }

    /**
     * Launches the form in create mode. Returns true if successfully saved.
     */
    public boolean showCreateForm(Frame parent, int authorId) {
        KnowledgeEntryForm form = new KnowledgeEntryForm(parent, "New Knowledge Entry");
        
        // Fetch categories to populate dropdown
        List<Category> categories = categoryService.getAllCategories();
        form.setCategories(categories);

        while (true) {
            form.setVisible(true); // Modal blocks execution here
            
            if (!form.isSaveClicked()) {
                return false;
            }

            Category selectedCategory = form.getSelectedCategory();
            int categoryId = selectedCategory != null ? selectedCategory.getId() : 0;

            try {
                knowledgeService.createEntry(
                        form.getEntryTitle(),
                        form.getEntryDescription(),
                        categoryId,
                        authorId
                );
                return true;
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(form, e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
                // Keep the dialog open for correction
            }
        }
    }

    /**
     * Launches the form in edit mode. Returns true if successfully updated.
     */
    public boolean showEditForm(Frame parent, KnowledgeEntry entry) {
        KnowledgeEntryForm form = new KnowledgeEntryForm(parent, "Edit Knowledge Entry");
        
        // Fetch categories to populate dropdown
        List<Category> categories = categoryService.getAllCategories();
        form.setCategories(categories);
        
        // Populate fields
        form.setEntryTitle(entry.getTitle());
        form.setEntryDescription(entry.getDescription());
        form.setSelectedCategoryId(entry.getCategoryId());

        while (true) {
            form.setVisible(true); // Modal blocks execution here

            if (!form.isSaveClicked()) {
                return false;
            }

            Category selectedCategory = form.getSelectedCategory();
            int categoryId = selectedCategory != null ? selectedCategory.getId() : 0;

            try {
                knowledgeService.updateEntry(
                        entry.getId(),
                        form.getEntryTitle(),
                        form.getEntryDescription(),
                        categoryId
                );
                return true;
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(form, e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
                // Keep the dialog open for correction
            }
        }
    }
}
