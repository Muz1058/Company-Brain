package com.companybrain.controller;

import com.companybrain.model.KnowledgeEntry;
import com.companybrain.service.AuthService;
import com.companybrain.service.KnowledgeService;
import com.companybrain.view.DashboardView;
import com.companybrain.view.LoginView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Controller managing dashboard operations: viewing, searching, and deleting entries, and signing out.
 */
public class DashboardController {
    private final DashboardView view;
    private final KnowledgeService knowledgeService;
    private final AuthService authService;
    private final LoginView loginView;
    private final KnowledgeEntryController entryController;

    public DashboardController(DashboardView view, KnowledgeService knowledgeService, AuthService authService, LoginView loginView) {
        this.view = view;
        this.knowledgeService = knowledgeService;
        this.authService = authService;
        this.loginView = loginView;
        this.entryController = new KnowledgeEntryController(knowledgeService);

        // Set user session info
        if (authService.getCurrentUser() != null) {
            view.setSessionUser(authService.getCurrentUser().getUsername());
        }

        // Load initial entry list
        refreshEntries();

        // Wire ActionListeners
        view.addSearchListener(new SearchActionListener());
        view.addCreateListener(new CreateActionListener());
        view.addEditListener(new EditActionListener());
        view.addDeleteListener(new DeleteActionListener());
        view.addLogoutListener(new LogoutActionListener());
    }

    private void refreshEntries() {
        List<KnowledgeEntry> entries = knowledgeService.getAllEntries();
        view.setEntries(entries);
    }

    private class SearchActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String query = view.getSearchQuery();
            List<KnowledgeEntry> searchResults = knowledgeService.searchEntries(query);
            view.setEntries(searchResults);
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
}
