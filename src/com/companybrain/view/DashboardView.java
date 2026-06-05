package com.companybrain.view;

import com.companybrain.model.KnowledgeEntry;
import com.companybrain.view.components.ModernButton;
import com.companybrain.view.components.SearchField;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardView extends JFrame {

    private SearchField txtSearch;
    private ModernButton btnSearch;
    private JTable tblEntries;
    private DefaultTableModel tableModel;

    private ModernButton btnView;
    private ModernButton btnCreate;
    private ModernButton btnEdit;
    private ModernButton btnDelete;
    private ModernButton btnUpload;
    private ModernButton btnLogout;

    private JLabel lblUserSession;
    private List<KnowledgeEntry> currentEntriesList = new ArrayList<>();

    /** Callback invoked when the user double-clicks a table row. */
    private Runnable doubleClickHandler;

    public DashboardView() {
        setTitle("Company Brain - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 570);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // ── Header ───────────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(Color.WHITE);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        searchPanel.setBackground(Color.WHITE);
        txtSearch = new SearchField("Search by title or category...", 30);
        btnSearch = new ModernButton("Search");
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        JPanel sessionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        sessionPanel.setBackground(Color.WHITE);
        lblUserSession = new JLabel("Logged in as: N/A");
        lblUserSession.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        btnLogout = new ModernButton("Logout", new Color(231, 76, 60));
        sessionPanel.add(lblUserSession);
        sessionPanel.add(btnLogout);

        headerPanel.add(searchPanel,  BorderLayout.WEST);
        headerPanel.add(sessionPanel, BorderLayout.EAST);

        // ── Table ────────────────────────────────────────────────────────────
        String[] columnNames = {"ID", "Title", "Category", "Type", "Last Updated"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblEntries = new JTable(tableModel);
        tblEntries.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblEntries.setRowHeight(25);
        tblEntries.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblEntries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Double-click on a row triggers the view handler
        tblEntries.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && doubleClickHandler != null) {
                    int row = tblEntries.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tblEntries.setRowSelectionInterval(row, row);
                        doubleClickHandler.run();
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblEntries);

        // ── Action buttons ────────────────────────────────────────────────────
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setBackground(new Color(245, 246, 250));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnView   = new ModernButton("View Entry",   new Color(52, 152, 219));
        btnCreate = new ModernButton("New Entry",    new Color(46, 204, 113));
        btnEdit   = new ModernButton("Edit Entry",   new Color(241, 196, 15));
        btnDelete = new ModernButton("Delete Entry", new Color(231, 76, 60));
        btnUpload = new ModernButton("Upload File",  new Color(155, 89, 182));

        Dimension btnSize = new Dimension(150, 40);
        btnView.setMaximumSize(btnSize);
        btnCreate.setMaximumSize(btnSize);
        btnEdit.setMaximumSize(btnSize);
        btnDelete.setMaximumSize(btnSize);
        btnUpload.setMaximumSize(btnSize);

        actionsPanel.add(btnView);
        actionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        actionsPanel.add(btnCreate);
        actionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        actionsPanel.add(btnEdit);
        actionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        actionsPanel.add(btnDelete);
        actionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        actionsPanel.add(btnUpload);

        mainPanel.add(headerPanel,  BorderLayout.NORTH);
        mainPanel.add(scrollPane,   BorderLayout.CENTER);
        mainPanel.add(actionsPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Updates the session label to show username and a human-readable role.
     * Converts role strings: ADMIN → "Administrator", others → "User".
     */
    public void setSessionUser(String username, String role) {
        String displayRole = "ADMIN".equalsIgnoreCase(role) ? "Administrator" : "User";
        lblUserSession.setText("Logged in as: " + username + " (" + displayRole + ")");
    }

    /**
     * Legacy overload kept for compatibility; role display defaults to "User".
     */
    public void setSessionUser(String username) {
        setSessionUser(username, "VIEWER");
    }

    /**
     * Applies role-based visibility rules to the action buttons.
     * Non-ADMIN users cannot delete entries; the button is hidden and a tooltip
     * is added so the UI clearly communicates the restriction.
     */
    public void applyRolePermissions(String role) {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        btnDelete.setVisible(isAdmin);
        if (!isAdmin) {
            btnDelete.setToolTipText("Only administrators can delete entries.");
        }
    }

    public void setEntries(List<KnowledgeEntry> entries, Map<Integer, String> categoryMap) {
        this.currentEntriesList = entries;
        tableModel.setRowCount(0);
        for (KnowledgeEntry entry : entries) {
            String categoryName = categoryMap.getOrDefault(entry.getCategoryId(), "Unknown");
            String typeLabel    = entry.getEntryType() != null ? entry.getEntryType().name() : "TEXT";
            tableModel.addRow(new Object[]{
                entry.getId(),
                entry.getTitle(),
                categoryName,
                typeLabel,
                entry.getUpdatedAt().toString().replace('T', ' ').substring(0, 19)
            });
        }
    }

    public KnowledgeEntry getSelectedEntry() {
        int selectedRow = tblEntries.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < currentEntriesList.size()) {
            return currentEntriesList.get(selectedRow);
        }
        return null;
    }

    public String getSearchQuery() {
        return txtSearch.getSearchQuery();
    }

    /** Sets the handler that is invoked on a table row double-click. */
    public void setDoubleClickHandler(Runnable handler) {
        this.doubleClickHandler = handler;
    }

    public void addSearchListener(ActionListener listener) {
        btnSearch.addActionListener(listener);
        txtSearch.addActionListener(listener);
    }

    public void addViewListener(ActionListener listener)   { btnView.addActionListener(listener); }
    public void addCreateListener(ActionListener listener) { btnCreate.addActionListener(listener); }
    public void addEditListener(ActionListener listener)   { btnEdit.addActionListener(listener); }
    public void addDeleteListener(ActionListener listener) { btnDelete.addActionListener(listener); }
    public void addUploadListener(ActionListener listener) { btnUpload.addActionListener(listener); }
    public void addLogoutListener(ActionListener listener) { btnLogout.addActionListener(listener); }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean showConfirmDialog(String message) {
        return JOptionPane.showConfirmDialog(
            this, message, "Confirm Action", JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION;
    }
}
