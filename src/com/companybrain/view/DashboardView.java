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

/**
 * CHANGED:
 *  - Added btnUserMgmt button
 *  - applyRolePermissions() now handles ADMIN / MANAGER / EMPLOYEE fully
 *  - setSessionUser(username, role) shows role as-is, e.g. "ali (EMPLOYEE)"
 */
public class DashboardView extends JFrame {

    private SearchField  txtSearch;
    private ModernButton btnSearch;
    private JTable       tblEntries;
    private DefaultTableModel tableModel;

    private ModernButton btnView;
    private ModernButton btnCreate;
    private ModernButton btnEdit;
    private ModernButton btnDelete;
    private ModernButton btnUpload;
    private ModernButton btnUserMgmt;   // NEW
    private ModernButton btnLogout;

    private JLabel lblUserSession;
    private List<KnowledgeEntry> currentEntriesList = new ArrayList<>();
    private Runnable doubleClickHandler;

    public DashboardView() {
        setTitle("Company Brain - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 590);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // Header
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

        // Table
        String[] cols = {"ID", "Title", "Category", "Type", "Last Updated"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblEntries = new JTable(tableModel);
        tblEntries.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblEntries.setRowHeight(25);
        tblEntries.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblEntries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblEntries.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && doubleClickHandler != null) {
                    int row = tblEntries.rowAtPoint(e.getPoint());
                    if (row >= 0) { tblEntries.setRowSelectionInterval(row, row); doubleClickHandler.run(); }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblEntries);

        // Action buttons
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setBackground(new Color(245, 246, 250));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnView     = new ModernButton("View Entry",      new Color(52,  152, 219));
        btnCreate   = new ModernButton("New Entry",       new Color(46,  204, 113));
        btnEdit     = new ModernButton("Edit Entry",      new Color(241, 196, 15));
        btnDelete   = new ModernButton("Delete Entry",    new Color(231, 76,  60));
        btnUpload   = new ModernButton("Upload File",     new Color(155, 89,  182));
        btnUserMgmt = new ModernButton("User Management", new Color(44,  62,  80));  // NEW

        Dimension sz = new Dimension(160, 38);
        for (ModernButton b : new ModernButton[]{btnView, btnCreate, btnEdit, btnDelete, btnUpload, btnUserMgmt}) {
            b.setMaximumSize(sz);
            actionsPanel.add(b);
            actionsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        mainPanel.add(headerPanel,  BorderLayout.NORTH);
        mainPanel.add(scrollPane,   BorderLayout.CENTER);
        mainPanel.add(actionsPanel, BorderLayout.EAST);
        add(mainPanel);
    }

    // ── Role-based visibility ─────────────────────────────────────────────────
    /**
     * ADMIN    : all visible
     * MANAGER  : delete + userMgmt hidden
     * EMPLOYEE : create + edit + delete + upload + userMgmt hidden
     */
    public void applyRolePermissions(String role) {
        boolean isAdmin    = "ADMIN".equalsIgnoreCase(role);
        boolean isManager  = "MANAGER".equalsIgnoreCase(role);

        btnCreate.setVisible(isAdmin || isManager);
        btnEdit.setVisible(isAdmin || isManager);
        btnDelete.setVisible(isAdmin);
        btnUpload.setVisible(isAdmin || isManager);
        btnUserMgmt.setVisible(isAdmin);
    }

    // ── Session label: "Logged in as: ali (EMPLOYEE)" ─────────────────────────
    public void setSessionUser(String username, String role) {
        lblUserSession.setText("Logged in as: " + username + " (" + role + ")");
    }

    // Legacy overload
    public void setSessionUser(String username) {
        setSessionUser(username, "USER");
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    public void setEntries(List<KnowledgeEntry> entries, Map<Integer, String> categoryMap) {
        this.currentEntriesList = entries;
        tableModel.setRowCount(0);
        for (KnowledgeEntry entry : entries) {
            String cat  = categoryMap.getOrDefault(entry.getCategoryId(), "Unknown");
            String type = entry.getEntryType() != null ? entry.getEntryType().name() : "TEXT";
            String upd  = entry.getUpdatedAt().toString().replace('T', ' ').substring(0, 19);
            tableModel.addRow(new Object[]{entry.getId(), entry.getTitle(), cat, type, upd});
        }
    }

    public KnowledgeEntry getSelectedEntry() {
        int row = tblEntries.getSelectedRow();
        if (row >= 0 && row < currentEntriesList.size()) return currentEntriesList.get(row);
        return null;
    }

    public String getSearchQuery() { return txtSearch.getSearchQuery(); }

    public void setDoubleClickHandler(Runnable r) { this.doubleClickHandler = r; }

    public void addSearchListener(ActionListener l)   { btnSearch.addActionListener(l); txtSearch.addActionListener(l); }
    public void addViewListener(ActionListener l)      { btnView.addActionListener(l); }
    public void addCreateListener(ActionListener l)    { btnCreate.addActionListener(l); }
    public void addEditListener(ActionListener l)      { btnEdit.addActionListener(l); }
    public void addDeleteListener(ActionListener l)    { btnDelete.addActionListener(l); }
    public void addUploadListener(ActionListener l)    { btnUpload.addActionListener(l); }
    public void addUserMgmtListener(ActionListener l)  { btnUserMgmt.addActionListener(l); }  // NEW
    public void addLogoutListener(ActionListener l)    { btnLogout.addActionListener(l); }

    public void showMessage(String msg)      { JOptionPane.showMessageDialog(this, msg, "Information", JOptionPane.INFORMATION_MESSAGE); }
    public void showErrorMessage(String msg) { JOptionPane.showMessageDialog(this, msg, "Error",       JOptionPane.ERROR_MESSAGE); }
    public boolean showConfirmDialog(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}