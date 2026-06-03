package com.companybrain.controller;

import com.companybrain.dao.CategoryDaoImpl;
import com.companybrain.dao.KnowledgeEntryDaoImpl;
import com.companybrain.exception.AuthenticationException;
import com.companybrain.model.User;
import com.companybrain.service.*;
import com.companybrain.view.DashboardView;
import com.companybrain.view.LoginView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controller managing the Authentication UI logic and transitions.
 */
public class LoginController {
    private final LoginView view;
    private final AuthService authService;

    public LoginController(LoginView view, AuthService authService) {
        this.view = view;
        this.authService = authService;
        this.view.addLoginListener(new LoginActionListener());
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = view.getUsername();
            String password = view.getPassword();

            try {
                User user = authService.login(username, password);
                
                // Hide login screen
                view.setVisible(false);
                view.clearFields();

                // Initialize Dashboard MVC
                DashboardView dashboardView = new DashboardView();
                KnowledgeService knowledgeService = new KnowledgeServiceImpl(new KnowledgeEntryDaoImpl());
                CategoryService categoryService = new CategoryServiceImpl(new CategoryDaoImpl());
                
                new DashboardController(dashboardView, knowledgeService, categoryService, authService, view);
                dashboardView.setVisible(true);

            } catch (AuthenticationException ex) {
                view.showErrorMessage(ex.getMessage());
            }
        }
    }
}
