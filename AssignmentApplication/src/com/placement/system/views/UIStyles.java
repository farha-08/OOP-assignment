package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Shared styling helpers for buttons and other UI elements.  
 * This allows login/registration screens to match dashboard theme.
 */
public class UIStyles {
    // reuse the same color constants defined in BaseDashboard
    public static final Color MAIN_BG = BaseDashboard.MAIN_BG;
    public static final Color ACCENT = BaseDashboard.ACCENT;
    public static final Color BORDER = BaseDashboard.BORDER;

    /**
     * Creates a navigation-style button identical to the dashboard menu buttons.
     * Background is MAIN_BG, text is black and it highlights on hover.
     */
    public static JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBackground(MAIN_BG);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        
        // hover effect matches BaseDashboard implementation
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT);
                button.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(MAIN_BG);
                button.setForeground(Color.BLACK);
            }
        });
        return button;
    }

    /**
     * Creates a solid-color button used for emphasised actions (logout, etc.).
     */
    public static JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(6, 12, 6, 12)
        ));
        return button;
    }
}
