package com.companybrain.controller;

import com.companybrain.model.Category;
import com.companybrain.model.KnowledgeEntry;
import com.companybrain.service.AuthService;
import com.companybrain.service.CategoryService;
import com.companybrain.service.KnowledgeService;
import com.companybrain.view.DashboardView;
import com.companybrain.view.LoginView;

import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DashboardController {
    private final DashboardView view;
    private final KnowledgeService knowledgeService;
    private final CategoryService categoryService;
    private final AuthService authService;
    private final LoginView loginView;
    private final KnowledgeEntryController entryController;

    public DashboardController(DashboardView view, KnowledgeService knowledgeService, CategoryService categoryService, AuthService authService, LoginView loginView) {
        this.view = view;
        this.knowledgeService = knowledgeService;
        this.categoryService = categoryService;
        this.authService = authService;
        this.loginView = loginView;
        this.entryController = new KnowledgeEntryController(knowledgeService, categoryService);

        
        if (authService.getCurrentUser() != null) {
            view.setSessionUser(authService.getCurrentUser().getUsername());
        }

        
        refreshEntries();

        
        view.addSearchListener(new SearchActionListener());
        view.addCreateListener(new CreateActionListener());
        view.addEditListener(new EditActionListener());
        view.addDeleteListener(new DeleteActionListener());
        view.addUploadListener(new UploadActionListener());
        view.addLogoutListener(new LogoutActionListener());
    }

    private void refreshEntries() {
        List<KnowledgeEntry> entries = knowledgeService.getAllEntries();
        
        
        List<Category> categories = categoryService.getAllCategories();
        Map<Integer, String> categoryMap = new HashMap<>();
        for (Category cat : categories) {
            categoryMap.put(cat.getId(), cat.getName());
        }

        view.setEntries(entries, categoryMap);
    }

    private class SearchActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String query = view.getSearchQuery();
            List<KnowledgeEntry> searchResults = knowledgeService.searchEntries(query);
            
            
            List<Category> categories = categoryService.getAllCategories();
            Map<Integer, String> categoryMap = new HashMap<>();
            for (Category cat : categories) {
                categoryMap.put(cat.getId(), cat.getName());
            }

            view.setEntries(searchResults, categoryMap);
        }
    }

    private class CreateActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int currentUserId = authService.getCurrentUser() != null ? authService.getCurrentUser().getId() : 1;
            boolean saved = entryController.showCreateForm(view, currentUserId);
            if (saved) {
                refreshEntries();
            }
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
            if (saved) {
                refreshEntries();
            }
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

            boolean confirmed = view.showConfirmDialog("Are you sure you want to delete the entry: \"" + selected.getTitle() + "\"?");
            if (confirmed) {
                knowledgeService.deleteEntry(selected.getId());
                refreshEntries();
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
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files (*.txt, *.csv, *.json)", "txt", "csv", "json"));
            
            int userSelection = fileChooser.showOpenDialog(view);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToUpload = fileChooser.getSelectedFile();
                try {
                    String title = fileToUpload.getName();
                    String content = java.nio.file.Files.readString(fileToUpload.toPath(), java.nio.charset.StandardCharsets.UTF_8);
                    
                    int currentUserId = authService.getCurrentUser() != null ? authService.getCurrentUser().getId() : 1;
                    boolean saved = entryController.showCreateForm(view, currentUserId, title, content);
                    if (saved) {
                        refreshEntries();
                    }
                } catch (Exception ex) {
                    view.showErrorMessage("Failed to read file: " + ex.getMessage());
                }
            }
        }
    }
}
