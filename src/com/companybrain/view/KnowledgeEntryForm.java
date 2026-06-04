package com.companybrain.view;

import com.companybrain.model.Category;
import com.companybrain.view.components.ModernButton;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class KnowledgeEntryForm extends JDialog {
    private JTextField txtTitle;
    private JTextArea txtDescription;
    private JComboBox<Category> cmbCategory;
    
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

        
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        
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

        
        JLabel lblCategory = new JLabel("Category:");
        lblCategory.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        fieldsPanel.add(lblCategory, gbc);

        cmbCategory = new JComboBox<>();
        cmbCategory.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        fieldsPanel.add(cmbCategory, gbc);

        
        JLabel lblDesc = new JLabel("Description:");
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        fieldsPanel.add(lblDesc, gbc);

        txtDescription = new JTextArea(12, 30);
        txtDescription.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridheight = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        fieldsPanel.add(scrollPane, gbc);

        
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

    public void setCategories(List<Category> categories) {
        cmbCategory.removeAllItems();
        for (Category category : categories) {
            cmbCategory.addItem(category);
        }
    }

    public Category getSelectedCategory() {
        return (Category) cmbCategory.getSelectedItem();
    }

    public void setSelectedCategoryId(int categoryId) {
        for (int i = 0; i < cmbCategory.getItemCount(); i++) {
            Category cat = cmbCategory.getItemAt(i);
            if (cat.getId() == categoryId) {
                cmbCategory.setSelectedIndex(i);
                break;
            }
        }
    }

    public String getEntryTitle() {
        return txtTitle.getText();
    }

    public void setEntryTitle(String title) {
        txtTitle.setText(title);
    }

    public String getEntryDescription() {
        return txtDescription.getText();
    }

    public void setEntryDescription(String description) {
        txtDescription.setText(description);
    }

    public boolean isSaveClicked() {
        return isSaveClicked;
    }
}
