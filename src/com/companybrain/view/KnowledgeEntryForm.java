package com.companybrain.view;

import com.companybrain.view.components.ModernButton;

import javax.swing.*;
import java.awt.*;

/**
 * Swing JDialog for creating or editing a KnowledgeEntry.
 */
public class KnowledgeEntryForm extends JDialog {
    private JTextField txtTitle;
    private JTextArea txtContent;
    private JTextField txtTags;
    
    private ModernButton btnSave;
    private ModernButton btnCancel;
    
    private boolean isSaveClicked = false;

    public KnowledgeEntryForm(Frame parent, String dialogTitle) {
        super(parent, dialogTitle, true);
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setResizable(true);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // Fields Panel
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel lblTitle = new JLabel("Title:");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        fieldsPanel.add(lblTitle, gbc);

        txtTitle = new JTextField();
        txtTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        fieldsPanel.add(txtTitle, gbc);

        // Tags
        JLabel lblTags = new JLabel("Tags:");
        lblTags.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        fieldsPanel.add(lblTags, gbc);

        txtTags = new JTextField();
        txtTags.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtTags.setToolTipText("Comma-separated values, e.g. java,swing,sqlite");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        fieldsPanel.add(txtTags, gbc);

        // Content
        JLabel lblContent = new JLabel("Content:");
        lblContent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        fieldsPanel.add(lblContent, gbc);

        txtContent = new JTextArea(12, 30);
        txtContent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtContent);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridheight = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        fieldsPanel.add(scrollPane, gbc);

        // Bottom Action Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionPanel.setBackground(Color.WHITE);
        
        btnSave = new ModernButton("Save", new Color(46, 204, 113));
        btnCancel = new ModernButton("Cancel", new Color(149, 165, 166));

        btnSave.addActionListener(e -> {
            isSaveClicked = true;
            dispose();
        });

        btnCancel.addActionListener(e -> {
            isSaveClicked = false;
            dispose();
        });

        actionPanel.add(btnCancel);
        actionPanel.add(btnSave);

        mainPanel.add(fieldsPanel, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    public String getEntryTitle() {
        return txtTitle.getText();
    }

    public void setEntryTitle(String title) {
        txtTitle.setText(title);
    }

    public String getEntryContent() {
        return txtContent.getText();
    }

    public void setEntryContent(String content) {
        txtContent.setText(content);
    }

    public String getEntryTags() {
        return txtTags.getText();
    }

    public void setEntryTags(String tags) {
        txtTags.setText(tags);
    }

    public boolean isSaveClicked() {
        return isSaveClicked;
    }
}
