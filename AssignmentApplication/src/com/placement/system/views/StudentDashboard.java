// com.placement.system.views/StudentDashboard.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class StudentDashboard extends BaseDashboard {
    
    // Add team's color scheme constants (matching CompanyDashboard)
    protected static final Color MAIN_BG = new Color(0xCF, 0xCF, 0xCF);      // #CFCFCF
    protected static final Color ACCENT = new Color(0x54, 0x54, 0x54);        // #545454
    protected static final Color BTN_BG = new Color(0x7D, 0x7D, 0x7D);        // #7D7D7D
    protected static final Color BTN_ACTIVE_BG = new Color(0x56, 0x56, 0x56);
    protected static final Color BORDER = new Color(160, 160, 160);           // Light gray border
    protected static final Color CARD_BG = new Color(0xE6, 0xE3, 0xD6);
    
    private Map<String, JButton> menuButtons = new HashMap<>();
    private String currentActiveMenu = "Dashboard";
    
    public StudentDashboard() {
        super("Student Dashboard");
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
        String[] menuItems = {"Dashboard", "Profile", "Browse Offers", "My Applications", "My Offers",  "Policy"};
        
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
        
        MyApplicationsPanel applicationsPanel = new MyApplicationsPanel();
        MyOffersPanel offersPanel = new MyOffersPanel();
        // Load applications for the current student
        String studentId = currentUser.getId() + "";
        applicationsPanel.loadApplicationsForStudent(studentId);
        
        // Load offers for the current student
        offersPanel.loadOffersForStudent(currentUser.getId());
        
        // Add your panels here
        contentPanel.add(new DashboardHomePanel(), "Dashboard");
        contentPanel.add(new StudentProfilePanel(), "Profile");
        contentPanel.add(new JobBrowserPanel(), "Browse Offers");
        contentPanel.add(applicationsPanel, "My Applications");
        contentPanel.add(offersPanel, "My Offers");
        contentPanel.add(new PolicyPanel(), "Policy");
    }
    
    private void handleMenuClick(String menuItem) {
        contentLayout.show(contentPanel, menuItem);
        setStatusMessage("Viewing: " + menuItem);
    }
    
    // Inner class for dashboard home
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
            
            JLabel headerLabel = new JLabel("STUDENT PANEL");
            headerLabel.setForeground(Color.WHITE);
            headerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            accentHeaderPanel.add(headerLabel, BorderLayout.WEST);
            
            // Status indicator on the right
            JLabel statusIndicator = new JLabel("● Online");
            statusIndicator.setForeground(new Color(144, 238, 144));
            statusIndicator.setFont(new Font("SansSerif", Font.PLAIN, 11));
            accentHeaderPanel.add(statusIndicator, BorderLayout.EAST);
            
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
            
            JLabel welcomeLabel = new JLabel("Welcome to the Student Placement System, " + currentUser.getFullName() + "!");
            welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            welcomeSubPanel.add(welcomeLabel, BorderLayout.CENTER);
            
            // Quick Stats section
            JPanel statsSection = new JPanel(new BorderLayout());
            statsSection.setBackground(MAIN_BG);
            statsSection.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER),
                "Quick Statistics",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12),
                Color.BLACK
            ));
            
            // Stats panel
            JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
            statsPanel.setBackground(MAIN_BG);
            statsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // For now using placeholders - you can replace with actual data later
            statsPanel.add(createStatCard("My Applications", "3"));
            statsPanel.add(createStatCard("Pending Offers", "4"));
            statsPanel.add(createStatCard("Placement Status", "Not Placed"));
            
            statsSection.add(statsPanel, BorderLayout.CENTER);
            
            // Recent Activity section
            JPanel activityPanel = new JPanel(new BorderLayout());
            activityPanel.setBackground(MAIN_BG);
            activityPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER),
                "Recent Activity",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12),
                Color.BLACK
            ));
            
            // Create a list for recent activities
            String[] activities = {
                "Applied for Software Engineer at Google - 2 days ago",
                "Application shortlisted for Data Analyst at Microsoft",
                "New job posting: Frontend Developer at Amazon",
                "Profile viewed by 3 companies this week"
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
            
            // Combine stats and activity in a split view
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, statsSection, activityPanel);
            splitPane.setDividerLocation(200);
            splitPane.setBorder(null);
            
            // Add everything to content container
            contentContainer.add(welcomeSubPanel, BorderLayout.NORTH);
            contentContainer.add(splitPane, BorderLayout.CENTER);
            
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
            
            JLabel infoLabel = new JLabel("Last login: " + new java.util.Date().toString() + " | System ready");
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
            
            // Value with conditional formatting
            JLabel valueLabel = new JLabel(value);
            valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            if (title.equals("Placement Status")) {
                // Status text
                valueLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                
                // Color-code based on status
                if (value.equals("Placed")) {
                    valueLabel.setForeground(new Color(0, 128, 0));
                } else if (value.equals("Not Placed")) {
                    valueLabel.setForeground(new Color(178, 34, 34));
                } else if (value.equals("Offered")) {
                    valueLabel.setForeground(new Color(255, 140, 0));
                } else {
                    valueLabel.setForeground(Color.BLACK);
                }
            } else {
                // Numeric values
                valueLabel.setForeground(ACCENT);
                valueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
            }
            
            card.add(titleLabel, BorderLayout.NORTH);
            card.add(valueLabel, BorderLayout.CENTER);
            
            return card;
        }
    }
}