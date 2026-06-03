package com.companybrain.view;

import com.companybrain.model.KnowledgeEntry;
import com.companybrain.view.components.ModernButton;
import com.companybrain.view.components.SearchField;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Swing JFrame for the Dashboard Window.
 */
public class DashboardView extends JFrame {
    private SearchField txtSearch;
    private ModernButton btnSearch;
    private JTable tblEntries;
    private DefaultTableModel tableModel;
    
    private ModernButton btnCreate;
    private ModernButton btnEdit;
    private ModernButton btnDelete;
    private ModernButton btnLogout;
    
    private JLabel lblUserSession;
    private List<KnowledgeEntry> currentEntriesList = new ArrayList<>();

    public DashboardView() {
        setTitle("Company Brain - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 550);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // Header Panel (Search, User Session, Logout)
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(Color.WHITE);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        searchPanel.setBackground(Color.WHITE);
        txtSearch = new SearchField("Search knowledge by title, content or tags...", 30);
        btnSearch = new ModernButton("Search");
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        JPanel sessionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        sessionPanel.setBackground(Color.WHITE);
        lblUserSession = new JLabel("Logged in as: N/A");
        lblUserSession.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        btnLogout = new ModernButton("Logout", new Color(231, 76, 60)); // Crimson red
        sessionPanel.add(lblUserSession);
        sessionPanel.add(btnLogout);

        headerPanel.add(searchPanel, BorderLayout.WEST);
        headerPanel.add(sessionPanel, BorderLayout.EAST);

        // Content Table Panel
        String[] columnNames = {"ID", "Title", "Tags", "Last Updated"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only cells
            }
        };
        tblEntries = new JTable(tableModel);
        tblEntries.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblEntries.setRowHeight(25);
        tblEntries.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(tblEntries);

        // Sidebar / Actions Panel
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setBackground(new Color(245, 246, 250));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnCreate = new ModernButton("New Entry", new Color(46, 204, 113)); // Emerald green
        btnEdit = new ModernButton("Edit Entry", new Color(241, 196, 15));   // Warm yellow
        btnDelete = new ModernButton("Delete Entry", new Color(231, 76, 60)); // Crimson red

        // Add padding around buttons
        btnCreate.setMaximumSize(new Dimension(150, 40));
        btnEdit.setMaximumSize(new Dimension(150, 40));
        btnDelete.setMaximumSize(new Dimension(150, 40));

        actionsPanel.add(btnCreate);
        actionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        actionsPanel.add(btnEdit);
        actionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        actionsPanel.add(btnDelete);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(actionsPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    public void setSessionUser(String username) {
        lblUserSession.setText("Logged in as: " + username);
    }

    public void setEntries(List<KnowledgeEntry> entries) {
        this.currentEntriesList = entries;
        tableModel.setRowCount(0);
        for (KnowledgeEntry entry : entries) {
            tableModel.addRow(new Object[]{
                    entry.getId(),
                    entry.getTitle(),
                    entry.getTags(),
                    entry.getUpdatedAt().toString().replace('T', ' ').substring(0, 19)
            });
        }
    }

    public KnowledgeEntry getSelectedEntry() {
        int selectedRow = tblEntries.getSelectedRow();
        if (selectedRow >= 0) {
            return currentEntriesList.get(selectedRow);
        }
        return null;
    }

    public String getSearchQuery() {
        return txtSearch.getSearchQuery();
    }

    // Action Listener Wire-ups
    public void addSearchListener(ActionListener listener) {
        btnSearch.addActionListener(listener);
        txtSearch.addActionListener(listener); // Enter key triggers search too
    }

    public void addCreateListener(ActionListener listener) {
        btnCreate.addActionListener(listener);
    }

    public void addEditListener(ActionListener listener) {
        btnEdit.addActionListener(listener);
    }

    public void addDeleteListener(ActionListener listener) {
        btnDelete.addActionListener(listener);
    }

    public void addLogoutListener(ActionListener listener) {
        btnLogout.addActionListener(listener);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean showConfirmDialog(String message) {
        int result = JOptionPane.showConfirmDialog(this, message, "Confirm Action", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
}
