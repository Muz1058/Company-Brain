package com.companybrain.view;

import com.companybrain.model.User;
import com.companybrain.view.components.ModernButton;
import com.companybrain.view.components.SearchField;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class UserManagementView extends JDialog {

    // Create-user form
    private JTextField     txtEmpId;
    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private ModernButton   btnCreate;
    private ModernButton   btnClear;

    // Search + table
    private SearchField    txtSearch;
    private ModernButton   btnSearch;
    private JTable         tblUsers;
    private DefaultTableModel tableModel;
    private List<User>     currentList = new ArrayList<>();

    // Action buttons
    private ModernButton btnDisable;
    private ModernButton btnEnable;
    private ModernButton btnResetPwd;

    public UserManagementView(Frame parent) {
        super(parent, "User Management", true);
        setSize(980, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        root.setBackground(Color.WHITE);
        root.add(buildFormPanel(),   BorderLayout.WEST);
        root.add(buildTablePanel(),  BorderLayout.CENTER);
        root.add(buildActionPanel(), BorderLayout.EAST);
        add(root);
    }

    private JPanel buildFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(245, 246, 250));
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200,200,200)), "Create New User",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), new Color(44, 62, 80)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10);
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        addRow(p, g, 0, "Employee ID *:", txtEmpId    = new JTextField(14));
        addRow(p, g, 1, "Username *:",    txtUsername = new JTextField(14));
        addRow(p, g, 2, "Password *:",    txtPassword = new JPasswordField(14));

        label(g, 3);
        p.add(lbl("Role *:"), g);
        cmbRole = new JComboBox<>(new String[]{"EMPLOYEE","MANAGER","ADMIN"});
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.gridx = 1; p.add(cmbRole, g);

        JPanel btns = new JPanel(new GridLayout(1,2,8,0));
        btns.setBackground(new Color(245,246,250));
        btnCreate = new ModernButton("Create User", new Color(46,204,113));
        btnClear  = new ModernButton("Clear",       new Color(149,165,166));
        btns.add(btnCreate); btns.add(btnClear);
        g.gridx = 0; g.gridy = 4; g.gridwidth = 2;
        g.insets = new Insets(16,10,8,10);
        p.add(btns, g);
        return p;
    }

    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        p.setBackground(Color.WHITE);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        searchRow.setBackground(Color.WHITE);
        txtSearch = new SearchField("Search username, employee ID or role...", 28);
        btnSearch = new ModernButton("Search");
        searchRow.add(txtSearch); searchRow.add(btnSearch);

        String[] cols = {"ID","Employee ID","Username","Role","Status","Created"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblUsers = new JTable(tableModel);
        tblUsers.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblUsers.setRowHeight(24);
        tblUsers.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        int[] w = {40,110,130,100,80,150};
        for (int i = 0; i < w.length; i++) tblUsers.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        p.add(searchRow,                   BorderLayout.NORTH);
        p.add(new JScrollPane(tblUsers),   BorderLayout.CENTER);
        return p;
    }

    private JPanel buildActionPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(245,246,250));
        p.setBorder(BorderFactory.createEmptyBorder(10,8,10,8));

        btnDisable  = new ModernButton("Disable User",   new Color(231,76,60));
        btnEnable   = new ModernButton("Enable User",    new Color(46,204,113));
        btnResetPwd = new ModernButton("Reset Password", new Color(52,152,219));

        Dimension sz = new Dimension(155,38);
        for (ModernButton b : new ModernButton[]{btnDisable, btnEnable, btnResetPwd}) {
            b.setMaximumSize(sz); p.add(b); p.add(Box.createRigidArea(new Dimension(0,10)));
        }
        return p;
    }

    // ── Public API ────────────────────────────────────────────────────────────
    
    public String getFormEmployeeId() { return txtEmpId.getText().trim(); }
    public String getFormUsername()   { return txtUsername.getText().trim(); }
    public String getFormPassword()   { return new String(txtPassword.getPassword()); }
    public String getFormRole()       { return (String) cmbRole.getSelectedItem(); }

    public void clearForm() {
        txtEmpId.setText(""); txtUsername.setText("");
        txtPassword.setText(""); cmbRole.setSelectedIndex(0);
    }

    public void setUsers(List<User> users) {
        currentList = users;
        tableModel.setRowCount(0);
        for (User u : users) {
            String status  = u.isActive() ? "ACTIVE" : "DISABLED";
            String created = u.getCreatedAt() != null
                ? u.getCreatedAt().toString().replace('T',' ').substring(0,19) : "—";
            tableModel.addRow(new Object[]{
                u.getId(),
                u.getEmployeeId() != null ? u.getEmployeeId() : "—",
                u.getUsername(), u.getRole(), status, created
            });
        }
    }

    public User getSelectedUser() {
        int row = tblUsers.getSelectedRow();
        if (row >= 0 && row < currentList.size()) return currentList.get(row);
        return null;
    }

    public String getSearchQuery() { return txtSearch.getSearchQuery(); }

    public void addCreateListener(ActionListener l)   { btnCreate.addActionListener(l); }
    public void addClearListener(ActionListener l)    { btnClear.addActionListener(l); }
    public void addSearchListener(ActionListener l)   { btnSearch.addActionListener(l); txtSearch.addActionListener(l); }
    public void addDisableListener(ActionListener l)  { btnDisable.addActionListener(l); }
    public void addEnableListener(ActionListener l)   { btnEnable.addActionListener(l); }
    public void addResetPwdListener(ActionListener l) { btnResetPwd.addActionListener(l); }

    public void showMessage(String msg)      { JOptionPane.showMessageDialog(this, msg, "Info",  JOptionPane.INFORMATION_MESSAGE); }
    public void showErrorMessage(String msg) { JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }
    public boolean showConfirmDialog(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /** Password-reset popup — returns null if cancelled or blank. */
    public String promptNewPassword() {
        JPasswordField f = new JPasswordField(15);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        int r = JOptionPane.showConfirmDialog(this, new Object[]{"New Password:", f},
            "Reset Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r == JOptionPane.OK_OPTION) {
            String p = new String(f.getPassword());
            return p.isEmpty() ? null : p;
        }
        return null;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JLabel lbl(String t) { JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI",Font.PLAIN,12)); return l; }

    private void addRow(JPanel p, GridBagConstraints g, int row, String labelText, JComponent field) {
        label(g, row); p.add(lbl(labelText), g);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.gridx = 1; p.add(field, g);
    }

    private void label(GridBagConstraints g, int row) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 1;
    }
}
