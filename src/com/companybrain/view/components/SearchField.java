package com.companybrain.view.components;

import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;


public class SearchField extends JTextField implements FocusListener {
    private final String placeholder;
    private boolean isShowingPlaceholder;

    public SearchField(String placeholder, int columns) {
        super(columns);
        this.placeholder = placeholder;
        this.isShowingPlaceholder = true;
        
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setForeground(Color.GRAY);
        setText(placeholder);
        addFocusListener(this);
    }

    
    public String getSearchQuery() {
        return isShowingPlaceholder ? "" : getText();
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (isShowingPlaceholder) {
            isShowingPlaceholder = false;
            setText("");
            setForeground(Color.BLACK);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (getText().isEmpty()) {
            isShowingPlaceholder = true;
            setText(placeholder);
            setForeground(Color.GRAY);
        }
    }
}
