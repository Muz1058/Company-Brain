package com.companybrain;

import com.companybrain.controller.LoginController;
import com.companybrain.dao.UserDaoImpl;
import com.companybrain.database.DatabaseManager;
import com.companybrain.service.AuthService;
import com.companybrain.service.AuthServiceImpl;
import com.companybrain.view.LoginView;

import javax.swing.*;


public class Main {

    public static void main(String[] args) {
        
        try {
            DatabaseManager.initializeDatabase();
        } catch (Exception e) {
        	 e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                    "Database initialization error: " + e.getMessage(), 
                    "Database Failure", 
                    JOptionPane.ERROR_MESSAGE);
            
            System.exit(1);
        }

        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            
        }

        
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            AuthService authService = new AuthServiceImpl(new UserDaoImpl());
            new LoginController(loginView, authService);
            loginView.setVisible(true);
        });
    }
}
