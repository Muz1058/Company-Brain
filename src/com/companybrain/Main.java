package com.companybrain;

import com.companybrain.controller.LoginController;
import com.companybrain.dao.UserDaoImpl;
import com.companybrain.database.DatabaseManager;
import com.companybrain.service.AuthService;
import com.companybrain.view.LoginView;

import javax.swing.*;

/**
 * Entry point for the Company Brain Swing Application.
 */
public class Main {

    public static void main(String[] args) {
        // 1. Initialize SQLite database schemas
        try {
            DatabaseManager.initializeDatabase();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                    "Database initialization error: " + e.getMessage(), 
                    "Database Failure", 
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // 2. Configure standard look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback silently to default cross-platform Swing UI
        }

        // 3. Launch Login screen on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            AuthService authService = new AuthService(new UserDaoImpl());
            new LoginController(loginView, authService);
            loginView.setVisible(true);
        });
    }
}
