package com.companybrain.controller;

import com.companybrain.exception.ValidationException;
import com.companybrain.model.User;
import com.companybrain.service.AuthService;
import com.companybrain.service.UserManagementService;
import com.companybrain.view.UserManagementView;

/**
 * NEW FILE — wires all UserManagementView actions to UserManagementService.
 */
public class UserManagementController {

    private final UserManagementView   view;
    private final AuthService          auth;
    private final UserManagementService userMgmt;

    public UserManagementController(UserManagementView view,
                                    AuthService auth,
                                    UserManagementService userMgmt) {
        this.view     = view;
        this.auth     = auth;
        this.userMgmt = userMgmt;

        refreshTable();

        view.addCreateListener(e   -> createUser());
        view.addClearListener(e    -> view.clearForm());
        view.addSearchListener(e   -> searchUsers());
        view.addDisableListener(e  -> disableUser());
        view.addEnableListener(e   -> enableUser());
        view.addResetPwdListener(e -> resetPassword());
    }

    // ── Create ────────────────────────────────────────────────────────────────

    private void createUser() {
        try {
            auth.createUser(
                view.getFormEmployeeId(),
                view.getFormUsername(),
                view.getFormPassword(),
                view.getFormRole(),
                auth.getCurrentUser()
            );
            view.showMessage("User '" + view.getFormUsername() + "' created successfully.");
            view.clearForm();
            refreshTable();
        } catch (ValidationException ex) {
            view.showErrorMessage(ex.getMessage());
        }
    }

    // ── Search ────────────────────────────────────────────────────────────────

    private void searchUsers() {
        view.setUsers(userMgmt.searchUsers(view.getSearchQuery()));
    }

    // ── Disable ───────────────────────────────────────────────────────────────

    private void disableUser() {
        User target = view.getSelectedUser();
        if (target == null) { view.showErrorMessage("Please select a user first."); return; }
        if (!view.showConfirmDialog(
                "Are you sure you want to disable user '" + target.getUsername() + "'?")) return;
        try {
            userMgmt.disableUser(target.getId(), auth.getCurrentUser());
            view.showMessage("User '" + target.getUsername() + "' has been disabled.");
            refreshTable();
        } catch (ValidationException ex) {
            view.showErrorMessage(ex.getMessage());
        }
    }

    // ── Enable ────────────────────────────────────────────────────────────────

    private void enableUser() {
        User target = view.getSelectedUser();
        if (target == null) { view.showErrorMessage("Please select a user first."); return; }
        try {
            userMgmt.enableUser(target.getId(), auth.getCurrentUser());
            view.showMessage("User '" + target.getUsername() + "' has been enabled.");
            refreshTable();
        } catch (ValidationException ex) {
            view.showErrorMessage(ex.getMessage());
        }
    }

    // ── Reset Password ────────────────────────────────────────────────────────

    private void resetPassword() {
        User target = view.getSelectedUser();
        if (target == null) { view.showErrorMessage("Please select a user first."); return; }

        String newPwd = view.promptNewPassword();
        if (newPwd == null || newPwd.isBlank()) {
            view.showErrorMessage("Password cannot be empty.");
            return;
        }
        try {
            userMgmt.resetPassword(target.getId(), newPwd, auth.getCurrentUser());
            view.showMessage("Password reset for '" + target.getUsername() + "'.");
        } catch (ValidationException ex) {
            view.showErrorMessage(ex.getMessage());
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private void refreshTable() {
        view.setUsers(userMgmt.getAllUsers());
    }
}
