package com.companybrain.controller;

import com.companybrain.exception.ValidationException;
import com.companybrain.service.AuthService;
import com.companybrain.view.LoginView;
import com.companybrain.view.SignUpView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignUpController {
    private final SignUpView view;
    private final AuthService authService;
    private final LoginView loginView;

    public SignUpController(SignUpView view, AuthService authService, LoginView loginView) {
        this.view = view;
        this.authService = authService;
        this.loginView = loginView;

        this.view.addSignUpListener(new SignUpActionListener());
        this.view.addResetListener(new ResetActionListener());
        this.view.addBackToLoginListener(new BackToLoginActionListener());
    }

    private class SignUpActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = view.getUsername();
            String password = view.getPassword();
            String confirmPassword = view.getConfirmPassword();
            String role = view.getSelectedRole();

            if (username.isEmpty()) {
                view.showErrorMessage("Username cannot be empty.");
                return;
            }
            if (password.isEmpty()) {
                view.showErrorMessage("Password cannot be empty.");
                return;
            }
            if (confirmPassword.isEmpty()) {
                view.showErrorMessage("Confirm Password cannot be empty.");
                return;
            }
            if (!password.equals(confirmPassword)) {
                view.showErrorMessage("Passwords do not match.");
                return;
            }

            try {
                authService.registerUser(username, password, role);
                view.showInfoMessage("Account successfully created! Please login.");
                switchToLogin();
            } catch (ValidationException ex) {
                view.showErrorMessage(ex.getMessage());
            } catch (Exception ex) {
                view.showErrorMessage("An error occurred during registration: " + ex.getMessage());
            }
        }
    }

    private class ResetActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.clearFields();
        }
    }

    private class BackToLoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            switchToLogin();
        }
    }

    private void switchToLogin() {
        view.setVisible(false);
        view.dispose();
        loginView.setVisible(true);
    }
}
