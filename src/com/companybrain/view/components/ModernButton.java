package com.companybrain.view.components;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

/**
 * A styled modern JButton for a cleaner interface.
 */
public class ModernButton extends JButton {

    public ModernButton(String text) {
        super(text);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setForeground(Color.WHITE);
        setBackground(new Color(63, 81, 181)); // Modern Indigo
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public ModernButton(String text, Color bg) {
        this(text);
        setBackground(bg);
    }
}
