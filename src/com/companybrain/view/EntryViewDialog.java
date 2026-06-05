package com.companybrain.view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * A read-only dialog that displays all fields of a knowledge entry.
 * No editing is possible; this dialog is purely for viewing.
 */
public class EntryViewDialog extends JDialog {

    public EntryViewDialog(Frame parent,
                           String title,
                           String categoryName,
                           String authorName,
                           String createdAt,
                           String updatedAt,
                           String entryType,
                           String description) {
        super(parent, "View Entry: " + title, true);
        setSize(580, 520);
        setLocationRelativeTo(parent);
        setResizable(true);
        initComponents(title, categoryName, authorName, createdAt, updatedAt, entryType, description);
    }

    private void initComponents(String title, String categoryName, String authorName,
                                 String createdAt, String updatedAt,
                                 String entryType, String description) {

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        root.setBackground(Color.WHITE);

        // ── Meta panel (top) ────────────────────────────────────────────────
        JPanel metaPanel = new JPanel(new GridBagLayout());
        metaPanel.setBackground(Color.WHITE);
        metaPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Entry Details",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11),
            new Color(100, 100, 100)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(5, 8, 5, 8);
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.fill    = GridBagConstraints.HORIZONTAL;

        addMetaRow(metaPanel, gbc, 0, "Title:",    title);
        addMetaRow(metaPanel, gbc, 1, "Category:", categoryName);
        addMetaRow(metaPanel, gbc, 2, "Author:",   authorName);
        addMetaRow(metaPanel, gbc, 3, "Type:",     entryType);
        addMetaRow(metaPanel, gbc, 4, "Created:",  createdAt);
        addMetaRow(metaPanel, gbc, 5, "Updated:",  updatedAt);

        // ── Description panel (centre) ───────────────────────────────────────
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBackground(Color.WHITE);
        descPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Description",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11),
            new Color(100, 100, 100)
        ));

        JTextArea txtDesc = new JTextArea(description);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setEditable(false);                    // read-only
        txtDesc.setBackground(new Color(248, 249, 250));
        txtDesc.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane scrollPane = new JScrollPane(txtDesc);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        descPanel.add(scrollPane, BorderLayout.CENTER);

        // ── Close button (bottom) ────────────────────────────────────────────
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setBackground(new Color(52, 152, 219));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnClose);

        root.add(metaPanel,   BorderLayout.NORTH);
        root.add(descPanel,   BorderLayout.CENTER);
        root.add(buttonPanel, BorderLayout.SOUTH);

        add(root);
    }

    /** Adds a label + read-only value field row to the meta grid. */
    private void addMetaRow(JPanel panel, GridBagConstraints gbc,
                            int row, String labelText, String value) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(80, 80, 80));
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(lbl, gbc);

        JTextField field = new JTextField(value == null ? "" : value);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setEditable(false);
        field.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        field.setBackground(new Color(248, 249, 250));
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        panel.add(field, gbc);
    }
}
