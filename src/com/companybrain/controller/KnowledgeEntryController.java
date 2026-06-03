package com.companybrain.controller;

import com.companybrain.exception.ValidationException;
import com.companybrain.model.KnowledgeEntry;
import com.companybrain.service.KnowledgeService;
import com.companybrain.view.KnowledgeEntryForm;

import javax.swing.*;
import java.awt.*;

/**
 * Controller managing the JDialog CRUD form for Knowledge Entries.
 */
public class KnowledgeEntryController {
    private final KnowledgeService knowledgeService;

    public KnowledgeEntryController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    /**
     * Launches the form in create mode. Returns true if successfully saved.
     */
    public boolean showCreateForm(Frame parent, int authorId) {
        KnowledgeEntryForm form = new KnowledgeEntryForm(parent, "New Knowledge Entry");
        
        while (true) {
            form.setVisible(true); // Modal blocks execution here
            
            if (!form.isSaveClicked()) {
                return false;
            }

            try {
                knowledgeService.createEntry(
                        form.getEntryTitle(),
                        form.getEntryContent(),
                        form.getEntryTags(),
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
        form.setEntryTitle(entry.getTitle());
        form.setEntryContent(entry.getContent());
        form.setEntryTags(entry.getTags());

        while (true) {
            form.setVisible(true); // Modal blocks execution here

            if (!form.isSaveClicked()) {
                return false;
            }

            try {
                knowledgeService.updateEntry(
                        entry.getId(),
                        form.getEntryTitle(),
                        form.getEntryContent(),
                        form.getEntryTags()
                );
                return true;
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(form, e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
                // Keep the dialog open for correction
            }
        }
    }
}
