package com.companybrain.view;

import com.companybrain.view.components.ModernButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * CHANGED:
 *  - Added Employee ID field (above Username)
 *  - Removed "Create New Account" button
 *  - Layout height adjusted
 */
public class LoginView extends JFrame {

    private JTextField     txtEmployeeId;   // NEW
    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private ModernButton   btnLogin;
    private ModernButton   btnReset;
    // btnSignUp REMOVED

    public LoginView() {
        setTitle("Company Brain - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 310);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        mainPanel.setBackground(new Color(245, 246, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel lblTitle = new JLabel("Company Brain", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        // Employee ID  ← NEW
        JLabel lblEmp = new JLabel("Employee ID:");
        lblEmp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        mainPanel.add(lblEmp, gbc);

        txtEmployeeId = new JTextField(15);
        txtEmployeeId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtEmployeeId.setToolTipText("Leave blank if you are the admin");
        gbc.gridx = 1; gbc.gridy = 1;
        mainPanel.add(txtEmployeeId, gbc);

        // Username
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(lblUser, gbc);

        txtUsername = new JTextField(15);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.gridy = 2;
        mainPanel.add(txtUsername, gbc);

        // Password
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(lblPass, gbc);

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.gridy = 3;
        mainPanel.add(txtPassword, gbc);

        // Buttons  (Sign Up button REMOVED)
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(new Color(245, 246, 250));
        btnLogin = new ModernButton("Sign In");
        btnReset = new ModernButton("Reset", new Color(149, 165, 166));
        btnPanel.add(btnLogin);
        btnPanel.add(btnReset);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 8, 8, 8);
        mainPanel.add(btnPanel, gbc);

        add(mainPanel);
    }

    public String getEmployeeId() { return txtEmployeeId.getText().trim(); }  // NEW
    public String getUsername()   { return txtUsername.getText().trim(); }
    public String getPassword()   { return new String(txtPassword.getPassword()); }

    public void addLoginListener(ActionListener l) { btnLogin.addActionListener(l); }
    public void addResetListener(ActionListener l) { btnReset.addActionListener(l); }
    // addSignUpListener REMOVED

    public void clearFields() {
        txtEmployeeId.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
    }

    public void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Login Error", JOptionPane.ERROR_MESSAGE);
    }
}