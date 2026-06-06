package com.companybrain.controller;

import com.companybrain.dao.CategoryDaoImpl;
import com.companybrain.dao.KnowledgeEntryDaoImpl;
import com.companybrain.dao.UserDaoImpl;
import com.companybrain.exception.AuthenticationException;
import com.companybrain.model.User;
import com.companybrain.service.*;
import com.companybrain.view.DashboardView;
import com.companybrain.view.LoginView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * CHANGED:
 *  - Login now passes employeeId to authService.login()
 *  - Employee ID validated before calling service
 *  - SignUpActionListener removed
 *  - DashboardController now receives UserManagementService
 */
public class LoginController {

    private final LoginView   view;
    private final AuthService authService;

    public LoginController(LoginView view, AuthService authService) {
        this.view        = view;
        this.authService = authService;
        view.addLoginListener(new LoginAction());
        view.addResetListener(e -> view.clearFields());
        // No sign-up listener
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String employeeId = view.getEmployeeId();
            String username   = view.getUsername();
            String password   = view.getPassword();

            if (username.isEmpty()) { view.showErrorMessage("Username cannot be empty."); return; }
            if (password.isEmpty()) { view.showErrorMessage("Password cannot be empty."); return; }

            boolean isAdmin = "admin".equalsIgnoreCase(username);
            if (!isAdmin && employeeId.isEmpty()) {
                view.showErrorMessage("Employee ID is required.");
                return;
            }

            try {
                User user = authService.login(employeeId, username, password);
                view.setVisible(false);
                view.clearFields();

                DashboardView dashboard = new DashboardView();
                KnowledgeService     knowledge = new KnowledgeServiceImpl(new KnowledgeEntryDaoImpl());
                CategoryService      category  = new CategoryServiceImpl(new CategoryDaoImpl());
                UserManagementService userMgmt = new UserManagementServiceImpl(new UserDaoImpl());

                new DashboardController(dashboard, knowledge, category, authService, userMgmt, view);
                dashboard.setVisible(true);

            } catch (AuthenticationException ex) {
                view.showErrorMessage(ex.getMessage());
            }
        }
    }
}