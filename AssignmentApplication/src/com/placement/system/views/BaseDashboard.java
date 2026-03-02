// com.placement.system.views/BaseDashboard.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import com.placement.system.models.User;
import com.placement.system.utils.SessionManager;

public abstract class BaseDashboard extends JFrame {
    
    // ===== UI THEME (matching the team's implementation) =====
    protected static final Color MAIN_BG = new Color(0xCF, 0xCF, 0xCF);      // #CFCFCF
    protected static final Color ACCENT = new Color(0x54, 0x54, 0x54);       // #545454
    protected static final Color BTN_BG = new Color(0x7D, 0x7D, 0x7D);       // #7D7D7D
    protected static final Color CARD_BG = new Color(0xE6, 0xE3, 0xD6);      // beige-grey
    protected static final Color BORDER = new Color(0x9A, 0x9A, 0x9A);
    
    protected User currentUser;
    protected JPanel menuPanel;
    protected JPanel contentPanel;
    protected CardLayout contentLayout;
    protected JLabel userLabel;
    protected JPanel statusBar;
    
    public BaseDashboard(String title) {
        this.currentUser = SessionManager.getInstance().getCurrentUser();
        initializeComponents();
        
        setTitle(title + " - " + currentUser.getRole());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        
        // Set Look and Feel
        UIManager.put("control", MAIN_BG);
        UIManager.put("text", Color.BLACK);
        
        // Main layout
        setLayout(new BorderLayout());
        
        // Create components
        JPanel headerPanel = createHeaderPanel();
        createMenuPanel();
        createContentArea();
        createStatusBar();
        
        // Assemble the frame
        Container container = getContentPane();
        
        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.setBackground(MAIN_BG);
        northContainer.add(headerPanel, BorderLayout.NORTH);
        northContainer.add(menuPanel, BorderLayout.SOUTH);
        
        container.add(northContainer, BorderLayout.NORTH);
        container.add(contentPanel, BorderLayout.CENTER);
        container.add(statusBar, BorderLayout.SOUTH);
        
        // Set the content pane background
        container.setBackground(MAIN_BG);
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ACCENT);
        header.setBorder(new EmptyBorder(6, 10, 6, 10));
        
        // Left side - Logo/Title
        JLabel titleLabel = new JLabel("Student Placement System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        
        // Right side - User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(ACCENT);
        rightPanel.setOpaque(true);
        
        userLabel = new JLabel("Logged in as: " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userLabel.setForeground(Color.WHITE);
        
        JButton logoutBtn = createStyledButton("Logout", BTN_BG);
        logoutBtn.addActionListener(e -> logout());
        
        rightPanel.add(userLabel);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(logoutBtn);
        
        header.add(titleLabel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    /**
     * Creates the horizontal menu panel
     * Subclasses should override this to add their specific menu buttons
     */
    protected void createMenuPanel() {
        menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        menuPanel.setBackground(MAIN_BG);
        menuPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
    }
    
    /**
     * Creates status bar at bottom of window
     */
    private void createStatusBar() {
        statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(MAIN_BG);
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusLabel.setBorder(new EmptyBorder(4, 8, 4, 8));
        
        JLabel dateLabel = new JLabel(new java.util.Date().toString());
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        dateLabel.setBorder(new EmptyBorder(4, 8, 4, 8));
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(dateLabel, BorderLayout.EAST);
    }
    
    protected abstract void createContentArea();
    
    protected void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Logout Confirmation", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.getInstance().logout();
            dispose();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        }
    }
    
    /**
     * Creates a styled navigation button for the horizontal menu
     * Matches the team's navButton style
     */
    protected JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBackground(MAIN_BG);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        
        // Hover effect
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
     * Creates a styled button with solid background (like team's greyButton)
     */
    protected JButton createStyledButton(String text, Color bgColor) {
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
    
    /**
     * Creates a classic JPanel with etched border for containing components
     */
    protected JPanel createEtchedPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        panel.setBackground(MAIN_BG);
        return panel;
    }
    
    /**
     * Creates a titled block with border (like team's titledBlock method)
     */
    protected JPanel createTitledBlock(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12),
                Color.BLACK
        ));
        return panel;
    }
    
    /**
     * Creates a section bar with accent color (like team's sectionBar)
     */
    protected JPanel createSectionBar(String text) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(ACCENT);
        bar.setBorder(new EmptyBorder(4, 8, 4, 8));
        bar.add(createHeaderLabel(text), BorderLayout.WEST);
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        return bar;
    }
    
    /**
     * Creates a header label with white text (like team's headerLabel)
     */
    protected JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        return label;
    }
    
    /**
     * Wraps a component with padding (like team's wrapCard method)
     */
    protected JPanel wrapInPadding(JComponent component) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(MAIN_BG);
        wrap.setBorder(new EmptyBorder(8, 8, 8, 8));
        wrap.add(component, BorderLayout.CENTER);
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        return wrap;
    }
    
    /**
     * Styles a form field (like team's styleField method)
     */
    protected void styleField(JComponent field) {
        field.setPreferredSize(new Dimension(250, 28));
        field.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        if (field instanceof JTextField) {
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(4, 6, 4, 6)
            ));
        } else if (field instanceof JComboBox) {
            ((JComboBox<?>) field).setBackground(Color.WHITE);
            ((JComboBox<?>) field).setBorder(BorderFactory.createLineBorder(BORDER));
        } else if (field instanceof JPasswordField) {
            ((JPasswordField) field).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(4, 6, 4, 6)
            ));
        }
    }
    
    /**
     * Helper to update status bar message
     */
    protected void setStatusMessage(String message) {
        if (statusBar != null && statusBar.getComponentCount() > 0) {
            Component comp = ((BorderLayout)statusBar.getLayout()).getLayoutComponent(BorderLayout.WEST);
            if (comp instanceof JLabel) {
                ((JLabel) comp).setText(message);
            }
        }
    }
    
    protected void initializeComponents() {
        // This method intentionally left empty for subclasses to override
    }
}