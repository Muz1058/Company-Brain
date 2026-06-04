package com.companybrain.view.components;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;


public class ModernButton extends JButton {

    public ModernButton(String text) {
        super(text);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setForeground(Color.WHITE);
        setBackground(new Color(63, 81, 181));
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setupHoverEffect();
    }

    public ModernButton(String text, Color bg) {
        this(text);
        setBackground(bg);
    }

    private void setupHoverEffect() {
        addMouseListener(new java.awt.event.MouseAdapter() {
            private Color originalBg;

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                originalBg = getBackground();
                setBackground(originalBg.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (originalBg != null) {
                    setBackground(originalBg);
                }
            }
        });
    }
}
