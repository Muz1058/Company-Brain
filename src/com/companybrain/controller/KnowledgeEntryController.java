package com.companybrain.controller;

import com.companybrain.exception.ValidationException;
import com.companybrain.model.Category;
import com.companybrain.model.KnowledgeEntry;
import com.companybrain.model.User;
import com.companybrain.service.CategoryService;
import com.companybrain.service.KnowledgeService;
import com.companybrain.view.EntryViewDialog;
import com.companybrain.view.KnowledgeEntryForm;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class KnowledgeEntryController {

    private final KnowledgeService knowledgeService;
    private final CategoryService  categoryService;

    public KnowledgeEntryController(KnowledgeService knowledgeService,
                                    CategoryService categoryService) {
        this.knowledgeService = knowledgeService;
        this.categoryService  = categoryService;
    }

    // ── View (read-only) ──────────────────────────────────────────────────────

    /**
     * Opens the appropriate viewer for the given entry:
     *  - TEXT entries → read-only {@link EntryViewDialog}
     *  - FILE entries → OS default application via {@link Desktop#open(File)}
     *
     * @param parent     parent frame (for dialog positioning)
     * @param entry      the entry to view
     * @param categoryMap a pre-fetched id→name map used to resolve the category label
     * @param authorName display name of the author (pass username or "Unknown")
     */
    public void showViewEntry(Frame parent,
                              KnowledgeEntry entry,
                              Map<Integer, String> categoryMap,
                              String authorName) {

        if (entry == null) return;

        if (entry.isFileEntry()) {
            openFileWithDesktop(parent, entry);
        } else {
            openTextViewDialog(parent, entry, categoryMap, authorName);
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void openTextViewDialog(Frame parent,
                                    KnowledgeEntry entry,
                                    Map<Integer, String> categoryMap,
                                    String authorName) {

        String categoryName = categoryMap.getOrDefault(entry.getCategoryId(), "Unknown");

        String createdAt = entry.getCreatedAt() != null
            ? entry.getCreatedAt().toString().replace('T', ' ')
            : "N/A";
        String updatedAt = entry.getUpdatedAt() != null
            ? entry.getUpdatedAt().toString().replace('T', ' ')
            : "N/A";

        EntryViewDialog dialog = new EntryViewDialog(
            parent,
            entry.getTitle(),
            categoryName,
            authorName,
            createdAt,
            updatedAt,
            entry.getEntryType().name(),
            entry.getDescription()
        );
        dialog.setVisible(true);
    }

    private void openFileWithDesktop(Frame parent, KnowledgeEntry entry) {
        String filePath = entry.getFilePath();

        // Guard: file path stored in DB must not be blank
        if (filePath == null || filePath.isBlank()) {
            JOptionPane.showMessageDialog(parent,
                "No file path is associated with this entry.",
                "Missing File Path",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        File file = new File(filePath);

        // Guard: file must exist on disk
        if (!file.exists()) {
            JOptionPane.showMessageDialog(parent,
                "The file could not be found:\n" + filePath +
                "\n\nIt may have been moved or deleted.",
                "File Not Found",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Guard: Desktop API must be supported on this platform
        if (!Desktop.isDesktopSupported()) {
            JOptionPane.showMessageDialog(parent,
                "Opening files is not supported on this platform.\n" +
                "File location: " + filePath,
                "Unsupported Operation",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        Desktop desktop = Desktop.getDesktop();

        // Guard: the OPEN action must be supported
        if (!desktop.isSupported(Desktop.Action.OPEN)) {
            JOptionPane.showMessageDialog(parent,
                "Your system does not support opening files automatically.\n" +
                "File location: " + filePath,
                "Unsupported Operation",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            desktop.open(file);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent,
                "Could not open the file:\n" + filePath +
                "\n\nReason: " + ex.getMessage() +
                "\n\nMake sure a compatible application is installed.",
                "Cannot Open File",
                JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            // Desktop.open throws this if the file does not exist (race condition)
            JOptionPane.showMessageDialog(parent,
                "The file no longer exists or is inaccessible:\n" + filePath,
                "File Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Create ────────────────────────────────────────────────────────────────

    public boolean showCreateForm(Frame parent, int authorId) {
        return showCreateForm(parent, authorId, null, null);
    }

    public boolean showCreateForm(Frame parent, int authorId,
                                  String initialTitle, String initialDescription) {
        KnowledgeEntryForm form = new KnowledgeEntryForm(parent, "New Knowledge Entry");

        List<Category> categories = categoryService.getAllCategories();
        form.setCategories(categories);

        if (initialTitle       != null) form.setEntryTitle(initialTitle);
        if (initialDescription != null) form.setEntryDescription(initialDescription);

        while (true) {
            form.setVisible(true);

            if (!form.isSaveClicked()) return false;

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
                JOptionPane.showMessageDialog(form, e.getMessage(),
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // ── Edit ──────────────────────────────────────────────────────────────────

    public boolean showEditForm(Frame parent, KnowledgeEntry entry) {
        KnowledgeEntryForm form = new KnowledgeEntryForm(parent, "Edit Knowledge Entry");

        List<Category> categories = categoryService.getAllCategories();
        form.setCategories(categories);

        form.setEntryTitle(entry.getTitle());
        form.setEntryDescription(entry.getDescription());
        form.setSelectedCategoryId(entry.getCategoryId());

        while (true) {
            form.setVisible(true);

            if (!form.isSaveClicked()) return false;

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
                JOptionPane.showMessageDialog(form, e.getMessage(),
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
