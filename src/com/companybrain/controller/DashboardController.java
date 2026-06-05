package com.companybrain.controller;

import com.companybrain.model.Category;
import com.companybrain.model.KnowledgeEntry;
import com.companybrain.model.KnowledgeEntry.EntryType;
import com.companybrain.model.User;
import com.companybrain.service.AuthService;
import com.companybrain.service.CategoryService;
import com.companybrain.service.KnowledgeService;
import com.companybrain.service.KnowledgeServiceImpl;
import com.companybrain.view.DashboardView;
import com.companybrain.view.LoginView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardController {

    private final DashboardView          view;
    private final KnowledgeService       knowledgeService;
    private final CategoryService        categoryService;
    private final AuthService            authService;
    private final LoginView              loginView;
    private final KnowledgeEntryController entryController;

    public DashboardController(DashboardView view,
                               KnowledgeService knowledgeService,
                               CategoryService categoryService,
                               AuthService authService,
                               LoginView loginView) {
        this.view              = view;
        this.knowledgeService  = knowledgeService;
        this.categoryService   = categoryService;
        this.authService       = authService;
        this.loginView         = loginView;
        this.entryController   = new KnowledgeEntryController(knowledgeService, categoryService);

        User currentUser = authService.getCurrentUser();

        // ── Session label: "Logged in as: admin (Administrator)" ─────────────
        if (currentUser != null) {
            view.setSessionUser(currentUser.getUsername(), currentUser.getRole());
        }

        // ── Hide / disable buttons the current role cannot use ────────────────
        if (currentUser != null) {
            view.applyRolePermissions(currentUser.getRole());
        }

        // ── Load initial data ─────────────────────────────────────────────────
        refreshEntries();

        // ── Wire up listeners ─────────────────────────────────────────────────
        view.setDoubleClickHandler(this::handleViewEntry);   // double-click
        view.addViewListener(new ViewActionListener());
        view.addSearchListener(new SearchActionListener());
        view.addCreateListener(new CreateActionListener());
        view.addEditListener(new EditActionListener());
        view.addDeleteListener(new DeleteActionListener());
        view.addUploadListener(new UploadActionListener());
        view.addLogoutListener(new LogoutActionListener());
    }

    // ── View entry ────────────────────────────────────────────────────────────

    /**
     * Shared logic for both the "View Entry" button and the double-click handler.
     * Resolves the author display name and delegates to the entry controller.
     */
    private void handleViewEntry() {
        KnowledgeEntry selected = view.getSelectedEntry();
        if (selected == null) {
            view.showMessage("Please select an entry to view.");
            return;
        }

        // Fetch full record from DB to ensure we have the latest data
        KnowledgeEntry full = knowledgeService.getEntryById(selected.getId());
        if (full == null) {
            view.showErrorMessage("Could not load the entry from the database.");
            return;
        }

        Map<Integer, String> categoryMap = buildCategoryMap();

        // Resolve author name (use username of current user if ID matches, else show ID)
        User currentUser = authService.getCurrentUser();
        String authorName = (currentUser != null && currentUser.getId() == full.getAuthorId())
            ? currentUser.getUsername()
            : "User #" + full.getAuthorId();

        entryController.showViewEntry(view, full, categoryMap, authorName);
    }

    // ── Listeners ─────────────────────────────────────────────────────────────

    private class ViewActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            handleViewEntry();
        }
    }

    private class SearchActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String query        = view.getSearchQuery();
            List<KnowledgeEntry> results = knowledgeService.searchEntries(query);
            view.setEntries(results, buildCategoryMap());
        }
    }

    private class CreateActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int authorId = currentUserId();
            boolean saved = entryController.showCreateForm(view, authorId);
            if (saved) refreshEntries();
        }
    }

    private class EditActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            KnowledgeEntry selected = view.getSelectedEntry();
            if (selected == null) {
                view.showMessage("Please select an entry to edit.");
                return;
            }
            boolean saved = entryController.showEditForm(view, selected);
            if (saved) refreshEntries();
        }
    }

    private class DeleteActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            KnowledgeEntry selected = view.getSelectedEntry();
            if (selected == null) {
                view.showMessage("Please select an entry to delete.");
                return;
            }

            boolean confirmed = view.showConfirmDialog(
                "Are you sure you want to delete the entry:\n\"" + selected.getTitle() + "\"?"
            );
            if (!confirmed) return;

            try {
                // Backend enforces ADMIN-only; throws SecurityException otherwise
                knowledgeService.deleteEntry(selected.getId(), authService.getCurrentUser());
                refreshEntries();
            } catch (SecurityException ex) {
                // Should not normally reach here (button is hidden for non-admins)
                // but this is the backend safety net
                view.showErrorMessage("Access denied: " + ex.getMessage());
            }
        }
    }

    private class LogoutActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            authService.logout();
            view.setVisible(false);
            view.dispose();
            loginView.setVisible(true);
        }
    }

    private class UploadActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Upload Knowledge Entry File");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Supported Files (*.txt, *.csv, *.json, *.pdf, *.docx)",
                "txt", "csv", "json", "pdf", "docx"
            ));

            int choice = fileChooser.showOpenDialog(view);
            if (choice != JFileChooser.APPROVE_OPTION) return;

            java.io.File file = fileChooser.getSelectedFile();

            try {
                String title       = file.getName();
                String absolutePath = file.getAbsolutePath();

                // Read text content for preview / description field
                String content;
                try {
                    content = java.nio.file.Files.readString(
                        file.toPath(), java.nio.charset.StandardCharsets.UTF_8);
                } catch (Exception readEx) {
                    // Binary files (PDF, DOCX) cannot be read as plain text —
                    // use a placeholder description instead
                    content = "[Binary file: " + title + "]";
                }

                int authorId = currentUserId();

                // Show the create form pre-filled with the file name and content.
                // The controller will call the extended createEntry that stores
                // entry_type=FILE and the absolute file path.
                boolean saved = showCreateFormForFile(authorId, title, content, absolutePath);
                if (saved) refreshEntries();

            } catch (Exception ex) {
                view.showErrorMessage("Failed to process file: " + ex.getMessage());
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void refreshEntries() {
        view.setEntries(knowledgeService.getAllEntries(), buildCategoryMap());
    }

    private Map<Integer, String> buildCategoryMap() {
        Map<Integer, String> map = new HashMap<>();
        for (Category cat : categoryService.getAllCategories()) {
            map.put(cat.getId(), cat.getName());
        }
        return map;
    }

    private int currentUserId() {
        User u = authService.getCurrentUser();
        return u != null ? u.getId() : 1;
    }

    /**
     * Shows the create form for a file upload. On save it calls the extended
     * {@link KnowledgeServiceImpl#createEntry} overload that stores FILE type
     * and the absolute path, so "View Entry" / double-click can later open it.
     */
    private boolean showCreateFormForFile(int authorId, String initialTitle,
                                          String initialContent, String absolutePath) {
        // Delegate through the entry controller's form, then intercept the save
        // to add FILE-type metadata. We do this by calling the extended service
        // method directly after the form is confirmed.
        com.companybrain.view.KnowledgeEntryForm form =
            new com.companybrain.view.KnowledgeEntryForm(view, "Upload: New File Entry");

        form.setCategories(categoryService.getAllCategories());
        form.setEntryTitle(initialTitle);
        form.setEntryDescription(initialContent);

        while (true) {
            form.setVisible(true);
            if (!form.isSaveClicked()) return false;

            com.companybrain.model.Category selectedCat = form.getSelectedCategory();
            int categoryId = selectedCat != null ? selectedCat.getId() : 0;

            try {
                // Use the extended overload: entry_type = FILE, file_path = absolutePath
                ((KnowledgeServiceImpl) knowledgeService).createEntry(
                    form.getEntryTitle(),
                    form.getEntryDescription(),
                    categoryId,
                    authorId,
                    EntryType.FILE,
                    absolutePath
                );
                return true;
            } catch (com.companybrain.exception.ValidationException ex) {
                JOptionPane.showMessageDialog(form, ex.getMessage(),
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
