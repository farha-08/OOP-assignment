// com.placement.system.views/CompanyDashboard.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.placement.system.models.Company;
import com.placement.system.models.User;
import com.placement.system.models.Job;
import com.placement.system.models.Application;
import com.placement.system.utils.SessionManager;
import com.placement.system.dao.CompanyDAO;
import com.placement.system.dao.JobDAO;
import com.placement.system.dao.ApplicationDAO;

public class CompanyDashboard extends BaseDashboard {
    
    // Add team's color scheme constants
    protected static final Color MAIN_BG = new Color(0xCF, 0xCF, 0xCF);      // #CFCFCF
    protected static final Color ACCENT = new Color(0x54, 0x54, 0x54);        // #545454
    protected static final Color BTN_BG = new Color(0x7D, 0x7D, 0x7D);        // #7D7D7D
    protected static final Color BTN_ACTIVE_BG = new Color(0x56, 0x56, 0x56);
    protected static final Color BORDER = new Color(160, 160, 160);           // Light gray border
    protected static final Color CARD_BG = new Color(0xE6, 0xE3, 0xD6);
    
    private Company company;
    private Map<String, JButton> menuButtons = new HashMap<>();
    private String currentActiveMenu = "Dashboard";
    
    public CompanyDashboard() {
        super("Company Dashboard");
    }
    
    @Override
    protected void initializeComponents() {
        // Initialize company FIRST, before any GUI creation
        User currentUser = SessionManager.getInstance().getCurrentUser();
        System.out.println("Initializing CompanyDashboard for user: " + currentUser);
        
        if (currentUser instanceof Company) {
            this.company = (Company) currentUser;
            System.out.println("Company cast successful: " + company.getCompanyName());
        } else {
            // Fallback - load from database using DAO
            System.out.println("User is not Company instance, loading from DAO with ID: " + currentUser.getId());
            this.company = CompanyDAO.getInstance().getCompany(currentUser.getId());
        }
        
        // Call parent initialization
        super.initializeComponents();
        
        // Set status message
        if (company != null) {
            setStatusMessage("Logged in as: " + company.getCompanyName());
        }
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        // Safety check
        if (company == null) {
            System.err.println("ERROR: company is null in addNotify!");
            // Try to reload from session
            User currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser instanceof Company) {
                this.company = (Company) currentUser;
            } else if (currentUser != null) {
                this.company = CompanyDAO.getInstance().getCompany(currentUser.getId());
            }
        }
        
        // Now it's safe to setup menu buttons
        setupMenuButtons();
        // Set initial active button
        setActiveButton("Dashboard");
    }
    
    private void setupMenuButtons() {
        // Add buttons to the horizontal menu
        String[] menuItems = {"Dashboard", "My Offers", "Create Offer"};
        
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
        // Safety check
        if (company == null) {
            System.err.println("ERROR: company is null in createContentArea!");
            JOptionPane.showMessageDialog(this,
                "Failed to load company data. Please log in again.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(MAIN_BG);
        
        // Add your panels here
        contentPanel.add(new DashboardHomePanel(), "Dashboard");
        contentPanel.add(new CompanyOffersPanel(() -> {
            handleMenuClick("Create Offer");
        }), "My Offers");
        contentPanel.add(new CreateOfferPanel(() -> {
            handleMenuClick("My Offers");
            refreshOffersPanel();
            // Set active button back to My Offers after creation
            setActiveButton("My Offers");
        }), "Create Offer");
    }
    
    private void refreshOffersPanel() {
        Component myOffersPanel = contentPanel.getComponent(1);
        if (myOffersPanel instanceof CompanyOffersPanel) {
            ((CompanyOffersPanel) myOffersPanel).refresh();
        }
    }
    
    private void handleMenuClick(String menuItem) {
        contentLayout.show(contentPanel, menuItem);
        setStatusMessage("Viewing: " + menuItem);
        setActiveButton(menuItem);
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
     * Dashboard Home Panel with REAL data from database
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
            
            JLabel headerLabel = new JLabel("COMPANY PANEL");
            headerLabel.setForeground(Color.WHITE);
            headerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            accentHeaderPanel.add(headerLabel, BorderLayout.WEST);
            
            // Company name on the right side of header
            JLabel companyIndicator = new JLabel(company.getCompanyName());
            companyIndicator.setForeground(new Color(255, 215, 0)); // Gold color
            companyIndicator.setFont(new Font("SansSerif", Font.BOLD, 12));
            accentHeaderPanel.add(companyIndicator, BorderLayout.EAST);
            
            // Content container
            JPanel contentContainer = new JPanel(new BorderLayout(10, 10));
            contentContainer.setBackground(MAIN_BG);
            contentContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Welcome message with company status
            JPanel welcomeSubPanel = new JPanel(new BorderLayout());
            welcomeSubPanel.setBackground(CARD_BG);
            welcomeSubPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            
            // Create a panel for welcome message and verification status
            JPanel welcomeWrapper = new JPanel(new GridLayout(2, 1, 0, 5));
            welcomeWrapper.setBackground(CARD_BG);
            
            JLabel welcomeLabel = new JLabel("Welcome to Your Company Dashboard, " + company.getCompanyName() + "!");
            welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel statusLabel = new JLabel(company.isVerified() ? "✓ Verified Company" : "⏳ Pending Verification");
            statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            statusLabel.setForeground(company.isVerified() ? new Color(46, 204, 113) : new Color(241, 176, 59));
            statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            welcomeWrapper.add(welcomeLabel);
            welcomeWrapper.add(statusLabel);
            welcomeSubPanel.add(welcomeWrapper, BorderLayout.CENTER);
            
            // ===== FETCH REAL DATA FROM DATABASE =====
            JobDAO jobDAO = JobDAO.getInstance();
            ApplicationDAO appDAO = ApplicationDAO.getInstance();
            
            // Get company's jobs
            List<Job> companyJobs = jobDAO.getJobsByCompany(company.getId());
            int totalOffers = companyJobs.size();
            
            // Count active offers (jobs that are still active)
            int activeOffers = 0;
            int totalApplicants = 0;
            
            for (Job job : companyJobs) {
                if (job.isActive()) {
                    activeOffers++;
                }
                // Count applicants for each job
                totalApplicants += appDAO.getApplicationCountForJob(job.getJobId());
            }
            
            // Quick Stats section
            JPanel statsSection = new JPanel(new BorderLayout());
            statsSection.setBackground(MAIN_BG);
            statsSection.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER),
                "Company Overview",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12),
                Color.BLACK
            ));
            
            // Stats panel with grid layout
            JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
            statsPanel.setBackground(MAIN_BG);
            statsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Add stat cards with real data
            statsPanel.add(createStatCard("Total Offers", String.valueOf(totalOffers)));
            statsPanel.add(createStatCard("Active Offers", String.valueOf(activeOffers)));
            statsPanel.add(createStatCard("Total Applicants", String.valueOf(totalApplicants)));
            
            // Add verification status badge
            statsPanel.add(createStatusBadge("Account Status", company.isVerified() ? "Verified" : "Pending"));
            
            statsSection.add(statsPanel, BorderLayout.CENTER);
            
            // Recent Activity section - build from actual applications
            JPanel activityPanel = new JPanel(new BorderLayout());
            activityPanel.setBackground(MAIN_BG);
            activityPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER),
                "Recent Applications",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12),
                Color.BLACK
            ));
            
            // Build recent activities from real data
            DefaultListModel<String> activityModel = new DefaultListModel<>();
            
            // Get recent applications for company's jobs
            for (Job job : companyJobs) {
                List<Application> applications = appDAO.getApplicationsByJob(job.getJobId());
                for (Application app : applications) {
                    // Get student name (you might want to create a method to fetch student name)
                    activityModel.addElement("Student applied for " + job.getJobTitle() + 
                                           " - Status: " + app.getStatus() + 
                                           " (" + app.getApplicationDate().toLocalDate() + ")");
                }
            }
            
            // If no activities, show placeholder
            if (activityModel.isEmpty()) {
                activityModel.addElement("No recent applications yet");
            }
            
            JList<String> activityList = new JList<>(activityModel);
            activityList.setFont(new Font("SansSerif", Font.PLAIN, 12));
            activityList.setBackground(CARD_BG);
            activityList.setBorder(BorderFactory.createLineBorder(BORDER));
            activityList.setSelectionBackground(new Color(220, 240, 255));
            
            JScrollPane scrollPane = new JScrollPane(activityList);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            scrollPane.getViewport().setBackground(CARD_BG);
            activityPanel.add(scrollPane, BorderLayout.CENTER);
            
            // Quick action buttons
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
            actionPanel.setBackground(MAIN_BG);
            
            JButton viewAllBtn = createStyledButton("View All Applications", BTN_BG);
            viewAllBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
            viewAllBtn.addActionListener(e -> handleMenuClick("My Offers"));
            
            actionPanel.add(viewAllBtn);
            activityPanel.add(actionPanel, BorderLayout.SOUTH);
            
            // Combine stats and activity in a split view
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, statsSection, activityPanel);
            splitPane.setDividerLocation(190);
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
            
            JLabel infoLabel = new JLabel("Last login: " + new java.util.Date().toString() + " | Company portal ready");
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
         * Creates a status badge for non-numeric values
         */
        private JPanel createStatusBadge(String title, String status) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(CARD_BG);

            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
            ));

            // Title
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Status text
            JLabel statusLabel;
            if ("Verified".equalsIgnoreCase(status)) {
                statusLabel = new JLabel("✓ Verified");
                statusLabel.setForeground(new Color(46, 204, 113));
            } else if ("Pending".equalsIgnoreCase(status)) {
                statusLabel = new JLabel("⏳ Pending");
                statusLabel.setForeground(new Color(241, 176, 59));
            } else if ("Suspended".equalsIgnoreCase(status)) {
                statusLabel = new JLabel("✖ Suspended");
                statusLabel.setForeground(new Color(178, 34, 34));
            } else {
                statusLabel = new JLabel(status);
                statusLabel.setForeground(ACCENT);
            }

            statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

            card.add(titleLabel, BorderLayout.NORTH);
            card.add(statusLabel, BorderLayout.CENTER);

            return card;
        }
    }
}