package com.companybrain.controller;

import com.companybrain.dao.CategoryDaoImpl;
import com.companybrain.dao.KnowledgeEntryDaoImpl;
import com.companybrain.exception.AuthenticationException;
import com.companybrain.model.User;
import com.companybrain.service.*;
import com.companybrain.view.DashboardView;
import com.companybrain.view.LoginView;
import com.companybrain.view.SignUpView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class LoginController {
    private final LoginView view;
    private final AuthService authService;

    public LoginController(LoginView view, AuthService authService) {
        this.view = view;
        this.authService = authService;
        this.view.addLoginListener(new LoginActionListener());
        this.view.addResetListener(new ResetActionListener());
        this.view.addSignUpListener(new SignUpActionListener());
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = view.getUsername();
            String password = view.getPassword();

            if (username.isEmpty()) {
                view.showErrorMessage("Username cannot be empty.");
                return;
            }
            if (password.isEmpty()) {
                view.showErrorMessage("Password cannot be empty.");
                return;
            }

            try {
                User user = authService.login(username, password);
                
                view.setVisible(false);
                view.clearFields();

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

    private class ResetActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.clearFields();
        }
    }

    private class SignUpActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.setVisible(false);
            SignUpView signUpView = new SignUpView();
            new SignUpController(signUpView, authService, view);
            signUpView.setVisible(true);
        }
    }
}
