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


public class KnowledgeEntryController {
    private final KnowledgeService knowledgeService;
    private final CategoryService categoryService;

    public KnowledgeEntryController(KnowledgeService knowledgeService, CategoryService categoryService) {
        this.knowledgeService = knowledgeService;
        this.categoryService = categoryService;
    }

    public boolean showCreateForm(Frame parent, int authorId) {
        return showCreateForm(parent, authorId, null, null);
    }

    public boolean showCreateForm(Frame parent, int authorId, String initialTitle, String initialDescription) {
        KnowledgeEntryForm form = new KnowledgeEntryForm(parent, "New Knowledge Entry");
        
        List<Category> categories = categoryService.getAllCategories();
        form.setCategories(categories);

        if (initialTitle != null) {
            form.setEntryTitle(initialTitle);
        }
        if (initialDescription != null) {
            form.setEntryDescription(initialDescription);
        }

        while (true) {
            form.setVisible(true);
            
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
            }
        }
    }

    
    public boolean showEditForm(Frame parent, KnowledgeEntry entry) {
        KnowledgeEntryForm form = new KnowledgeEntryForm(parent, "Edit Knowledge Entry");
        
        
        List<Category> categories = categoryService.getAllCategories();
        form.setCategories(categories);
        
        
        form.setEntryTitle(entry.getTitle());
        form.setEntryDescription(entry.getDescription());
        form.setSelectedCategoryId(entry.getCategoryId());

        while (true) {
            form.setVisible(true); 

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
                
            }
        }
    }
}
