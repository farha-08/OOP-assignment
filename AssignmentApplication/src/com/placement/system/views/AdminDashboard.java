// com.placement.system.views/AdminDashboard.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AdminDashboard extends BaseDashboard {
    
    // Add color constants (these should match your BaseDashboard or be defined here)
    protected static final Color MAIN_BG = new Color(0xCF, 0xCF, 0xCF);      // #CFCFCF
    protected static final Color ACCENT = new Color(0x54, 0x54, 0x54);        // #545454
    protected static final Color BTN_BG = new Color(0x7D, 0x7D, 0x7D);        // #7D7D7D
    protected static final Color BTN_ACTIVE_BG = new Color(0x56, 0x56, 0x56); 
    protected static final Color BORDER = new Color(160, 160, 160);           // Light gray border
    protected static final Color CARD_BG = new Color(0xE6, 0xE3, 0xD6);
    
    private Map<String, JButton> menuButtons = new HashMap<>();
    private String currentActiveMenu = "Dashboard";
    
    public AdminDashboard() {
        super("Admin Dashboard");
        // Menu setup moved to addNotify()
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        // This is called after the component hierarchy is fully created
        // Now it's safe to setup menu buttons
        setupMenuButtons();
        // Set initial active button
        setActiveButton("Dashboard");
        setStatusMessage("Logged in as: " + currentUser.getFullName());
    }
    
    private void setupMenuButtons() {
        // Add buttons to the horizontal menu
        String[] menuItems = {"Dashboard", "Students", "Applications", "Companies"};
        
        for (String item : menuItems) {
            JButton btn = createMenuButton(item, item.equals("Dashboard"));
            btn.addActionListener(e -> {
                handleMenuClick(item);
                setActiveButton(item);
            });
            menuPanel.add(btn);
            menuButtons.put(item, btn);
        }
    }
    
    /**
     * Creates a menu button with appropriate styling based on active state
     */
    private JButton createMenuButton(String text, boolean isActive) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Style based on active state
        if (isActive) {
            button.setBackground(BTN_ACTIVE_BG);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
            ));
        } else {
            button.setBackground(MAIN_BG);
            button.setForeground(Color.BLACK);
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
            ));
        }
        
        return button;
    }
    
    /**
     * Sets the active button and updates all button styles
     */
    private void setActiveButton(String activeMenuItem) {
        currentActiveMenu = activeMenuItem;
        
        // Update all buttons
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            String menuItem = entry.getKey();
            JButton btn = entry.getValue();
            
            if (menuItem.equals(activeMenuItem)) {
                // Active button style
                btn.setBackground(BTN_ACTIVE_BG);
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT, 1),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            } else {
                // Inactive button style
                btn.setBackground(MAIN_BG);
                btn.setForeground(Color.BLACK);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }
            btn.repaint();
        }
    }
    
    @Override
    protected void createContentArea() {
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(MAIN_BG);
        
        // Add your panels here
        contentPanel.add(new DashboardHomePanel(), "Dashboard");
        contentPanel.add(new ManageStudentsPanel(), "Students");
        contentPanel.add(new ManageOffersPanel(), "Applications");
        contentPanel.add(new ManageCompaniesPanel(), "Companies");
    }
    
    private void handleMenuClick(String menuItem) {
        contentLayout.show(contentPanel, menuItem);
        setStatusMessage("Viewing: " + menuItem);
    }
    
    /**
     * Creates a styled button matching team's design
     */
    protected JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 1),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    // ==================== INNER PANEL CLASSES ====================
    
    /**
     * Dashboard Home Panel
     */
    class DashboardHomePanel extends JPanel {
        public DashboardHomePanel() {
            setBackground(MAIN_BG);
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // === BIG RECTANGULAR PANEL WITH ACCENT HEADER ===
            JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
            mainContentPanel.setBackground(MAIN_BG);
            mainContentPanel.setBorder(BorderFactory.createLineBorder(BORDER));
            
            // Header with accent color
            JPanel accentHeaderPanel = new JPanel(new BorderLayout());
            accentHeaderPanel.setBackground(ACCENT);
            accentHeaderPanel.setPreferredSize(new Dimension(getWidth(), 35));
            accentHeaderPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            
            JLabel headerLabel = new JLabel("ADMIN PANEL");
            headerLabel.setForeground(Color.WHITE);
            headerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            accentHeaderPanel.add(headerLabel, BorderLayout.WEST);
            
            // Admin name on the right side of header
            JLabel adminIndicator = new JLabel("Administrator: " + currentUser.getFullName());
            adminIndicator.setForeground(new Color(255, 215, 0)); // Gold color
            adminIndicator.setFont(new Font("SansSerif", Font.BOLD, 12));
            accentHeaderPanel.add(adminIndicator, BorderLayout.EAST);
            
            // Content container
            JPanel contentContainer = new JPanel(new BorderLayout(10, 10));
            contentContainer.setBackground(MAIN_BG);
            contentContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Welcome message inside content container
            JPanel welcomeSubPanel = new JPanel(new BorderLayout());
            welcomeSubPanel.setBackground(CARD_BG);
            welcomeSubPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            
            JLabel welcomeLabel = new JLabel("Welcome to the Admin Control Panel, " + currentUser.getFullName() + "!");
            welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            welcomeSubPanel.add(welcomeLabel, BorderLayout.CENTER);
            
            // Quick Stats section
            JPanel statsSection = new JPanel(new BorderLayout());
            statsSection.setBackground(MAIN_BG);
            statsSection.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER),
                "System Overview",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12),
                Color.BLACK
            ));
            
            // Stats panel with grid layout
            JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
            statsPanel.setBackground(MAIN_BG);
            statsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Add the four stat cards
            statsPanel.add(createStatCard("Students", "156"));
            statsPanel.add(createStatCard("Companies", "24"));
            statsPanel.add(createStatCard("Active Offers", "18"));
            statsPanel.add(createStatCard("Applications", "342"));
            
            statsSection.add(statsPanel, BorderLayout.CENTER);
            
            // Application Status Section
            JPanel appStatusSection = new JPanel(new BorderLayout());
            appStatusSection.setBackground(MAIN_BG);
            appStatusSection.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER),
                "Application Status Breakdown",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12),
                Color.BLACK
            ));
            
            // Panel for status breakdown
            JPanel statusBreakdownPanel = new JPanel(new GridLayout(1, 3, 15, 15));
            statusBreakdownPanel.setBackground(MAIN_BG);
            statusBreakdownPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Status cards with appropriate colors
            statusBreakdownPanel.add(createStatusCard("Approved", "45", new Color(0, 128, 0)));
            statusBreakdownPanel.add(createStatusCard("Rejected", "23", new Color(178, 34, 34)));
            statusBreakdownPanel.add(createStatusCard("Pending", "78", new Color(255, 140, 0)));
            
            appStatusSection.add(statusBreakdownPanel, BorderLayout.CENTER);
            
            // Recent Activity section
            JPanel activityPanel = new JPanel(new BorderLayout());
            activityPanel.setBackground(MAIN_BG);
            activityPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER),
                "Recent System Activity",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12),
                Color.BLACK
            ));
            
            // Create a list for recent activities
            String[] activities = {
                "New company registered: Tech Corp Inc. - 10 minutes ago",
                "Student John Doe accepted offer from Google",
                "5 new applications submitted for Software Engineer positions",
                "Company Microsoft posted 3 new job offers",
                "Placement drive scheduled for next week"
            };
            
            JList<String> activityList = new JList<>(activities);
            activityList.setFont(new Font("SansSerif", Font.PLAIN, 12));
            activityList.setBackground(CARD_BG);
            activityList.setBorder(BorderFactory.createLineBorder(BORDER));
            activityList.setSelectionBackground(new Color(220, 240, 255));
            
            JScrollPane scrollPane = new JScrollPane(activityList);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            scrollPane.getViewport().setBackground(CARD_BG);
            activityPanel.add(scrollPane, BorderLayout.CENTER);
            
            // Quick action buttons at the bottom
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
            actionPanel.setBackground(MAIN_BG);
            
            JButton viewAllBtn = createStyledButton("View All Applications", BTN_BG);
            viewAllBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
            viewAllBtn.addActionListener(e -> {
                handleMenuClick("Applications");
                setActiveButton("Applications");
            });
            
            actionPanel.add(viewAllBtn);
            activityPanel.add(actionPanel, BorderLayout.SOUTH);
            
            // Combine sections using a vertical box layout
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setBackground(MAIN_BG);
            
            // Add stats section
            statsSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
            centerPanel.add(statsSection);
            centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            
            // Add application status section
            appStatusSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
            centerPanel.add(appStatusSection);
            centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            
            // Add activity panel
            activityPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
            centerPanel.add(activityPanel);
            
            // Add everything to content container
            contentContainer.add(welcomeSubPanel, BorderLayout.NORTH);
            contentContainer.add(centerPanel, BorderLayout.CENTER);
            
            // Assemble the main panel
            mainContentPanel.add(accentHeaderPanel, BorderLayout.NORTH);
            mainContentPanel.add(contentContainer, BorderLayout.CENTER);
            
            // Add the main panel to this DashboardHomePanel
            add(mainContentPanel, BorderLayout.CENTER);
            
            // Add bottom info panel
            JPanel bottomInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            bottomInfoPanel.setBackground(MAIN_BG);
            bottomInfoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)
            ));
            
            JLabel infoLabel = new JLabel("Last login: " + new java.util.Date().toString() + " | Admin console ready");
            infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
            bottomInfoPanel.add(infoLabel);
            
            add(bottomInfoPanel, BorderLayout.SOUTH);
        }
        
        /**
         * Creates a stat card with team's styling
         */
        private JPanel createStatCard(String title, String value) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(CARD_BG);
            
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
            ));
            
            // Title
            JLabel titleLabel = new JLabel(title);
            titleLabel.setForeground(Color.BLACK);
            titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            // Value
            JLabel valueLabel = new JLabel(value);
            valueLabel.setForeground(ACCENT);
            valueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
            valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            card.add(titleLabel, BorderLayout.NORTH);
            card.add(valueLabel, BorderLayout.CENTER);
            
            return card;
        }
        
        /**
         * Creates a colored status card for application status breakdown
         */
        private JPanel createStatusCard(String title, String value, Color color) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(CARD_BG);
            
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, color),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                )
            ));
            
            // Title
            JLabel titleLabel = new JLabel(title);
            titleLabel.setForeground(Color.BLACK);
            titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            // Value
            JLabel valueLabel = new JLabel(value);
            valueLabel.setForeground(color);
            valueLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
            valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            card.add(titleLabel, BorderLayout.NORTH);
            card.add(valueLabel, BorderLayout.CENTER);
            
            return card;
        }
    }
    
}