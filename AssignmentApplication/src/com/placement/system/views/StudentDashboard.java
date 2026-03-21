// com.placement.system.views/StudentDashboard.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.placement.system.models.Student;
import com.placement.system.models.User;
import com.placement.system.models.Application;
import com.placement.system.models.Offer;
import com.placement.system.utils.SessionManager;
import com.placement.system.dao.StudentDAO;
import com.placement.system.dao.ApplicationDAO;
import com.placement.system.dao.OfferDAO;

public class StudentDashboard extends BaseDashboard {
    
    // Color scheme constants
    protected static final Color MAIN_BG = new Color(0xCF, 0xCF, 0xCF);      // #CFCFCF
    protected static final Color ACCENT = new Color(0x54, 0x54, 0x54);        // #545454
    protected static final Color BTN_BG = new Color(0x7D, 0x7D, 0x7D);        // #7D7D7D
    protected static final Color BTN_ACTIVE_BG = new Color(0x56, 0x56, 0x56);
    protected static final Color BORDER = new Color(160, 160, 160);           // Light gray border
    protected static final Color CARD_BG = new Color(0xE6, 0xE3, 0xD6);
    
    private Map<String, JButton> menuButtons = new HashMap<>();
    private String currentActiveMenu = "Dashboard";
    private Student student; // Store the actual student object
    
    public StudentDashboard() {
        super("Student Dashboard");
        // Student initialization moved to initializeComponents()
    }
    
    @Override
    protected void initializeComponents() {
        // Initialize student FIRST, before any GUI creation
        User currentUser = SessionManager.getInstance().getCurrentUser();
        System.out.println("Initializing StudentDashboard for user: " + currentUser);
        
        if (currentUser instanceof Student) {
            this.student = (Student) currentUser;
            System.out.println("Student cast successful: " + student.getFullName());
        } else {
            // Fallback - load from database using DAO
            System.out.println("User is not Student instance, loading from DAO with ID: " + currentUser.getId());
            this.student = StudentDAO.getInstance().getStudent(currentUser.getId());
        }
        
        // Call parent initialization
        super.initializeComponents();
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        setupMenuButtons();
        setActiveButton("Dashboard");
        setStatusMessage("Logged in as: " + student.getFullName());
    }
    
    private void setupMenuButtons() {
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
    
    private JButton createMenuButton(String text, boolean isActive) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
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
    
    private void setActiveButton(String activeMenuItem) {
        currentActiveMenu = activeMenuItem;
        
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            String menuItem = entry.getKey();
            JButton btn = entry.getValue();
            
            if (menuItem.equals(activeMenuItem)) {
                btn.setBackground(BTN_ACTIVE_BG);
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT, 1),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            } else {
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
        // Safety check - student should not be null here
        if (student == null) {
            System.err.println("ERROR: student is null in createContentArea!");
            // Try one more time to load from session
            User currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser instanceof Student) {
                this.student = (Student) currentUser;
            } else if (currentUser != null) {
                this.student = StudentDAO.getInstance().getStudent(currentUser.getId());
            }
            
            if (student == null) {
                JOptionPane.showMessageDialog(this,
                    "Failed to load student data. Please log in again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(MAIN_BG);
        
        // Create panels with real data
        MyApplicationsPanel applicationsPanel = new MyApplicationsPanel();
        applicationsPanel.loadApplicationsForStudent(String.valueOf(student.getId()));
        
        MyOffersPanel offersPanel = new MyOffersPanel();
        offersPanel.loadOffersForStudent(currentUser.getId());
        
        // Add panels
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
            
            JLabel headerLabel = new JLabel("STUDENT PANEL");
            headerLabel.setForeground(Color.WHITE);
            headerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            accentHeaderPanel.add(headerLabel, BorderLayout.WEST);
            
            // Status indicator based on placement status
            JLabel statusIndicator = new JLabel("● " + student.getPlacementStatus());
            if ("Placed".equals(student.getPlacementStatus())) {
                statusIndicator.setForeground(new Color(46, 204, 113)); // Green
            } else if ("Offered".equals(student.getPlacementStatus())) {
                statusIndicator.setForeground(new Color(241, 176, 59)); // Orange
            } else {
                statusIndicator.setForeground(new Color(52, 152, 219)); // Blue
            }
            statusIndicator.setFont(new Font("SansSerif", Font.PLAIN, 11));
            accentHeaderPanel.add(statusIndicator, BorderLayout.EAST);
            
            // Content container
            JPanel contentContainer = new JPanel(new BorderLayout(10, 10));
            contentContainer.setBackground(MAIN_BG);
            contentContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Welcome message
            JPanel welcomeSubPanel = new JPanel(new BorderLayout());
            welcomeSubPanel.setBackground(CARD_BG);
            welcomeSubPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            
            JLabel welcomeLabel = new JLabel("Welcome back, " + student.getFullName() + "!");
            welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            welcomeSubPanel.add(welcomeLabel, BorderLayout.CENTER);
            
            // ===== FETCH REAL DATA FROM DATABASE =====
            ApplicationDAO appDAO = ApplicationDAO.getInstance();
            OfferDAO offerDAO = OfferDAO.getInstance();
            
            List<Application> applications = appDAO.getApplicationsByStudent(student.getId());
            List<Offer> offers = offerDAO.getOffersByStudent(student.getId());
            
            // Calculate statistics
            int totalApplications = applications.size();
            int pendingOffers = 0;
            int acceptedOffers = 0;
            
            for (Offer offer : offers) {
                if ("Pending".equals(offer.getStatus())) {
                    pendingOffers++;
                } else if ("Accepted".equals(offer.getStatus())) {
                    acceptedOffers++;
                }
            }
            
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
            
            statsPanel.add(createStatCard("My Applications", String.valueOf(totalApplications)));
            statsPanel.add(createStatCard("Pending Offers", String.valueOf(pendingOffers)));
            statsPanel.add(createStatCard("Placement Status", student.getPlacementStatus()));
            statsPanel.add(createStatCard("Accepted Offers", String.valueOf(acceptedOffers)));
            
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
            
            // Build recent activities from real data
            DefaultListModel<String> activityModel = new DefaultListModel<>();
            
            // Add recent applications (last 3)
            int count = 0;
            for (Application app : applications) {
                if (count++ < 3) {
                    activityModel.addElement("Applied for Job #" + app.getJobId() + 
                                           " - " + app.getStatus() + 
                                           " (" + app.getApplicationDate().toLocalDate() + ")");
                }
            }
            
            // Add recent offers
            for (Offer offer : offers) {
                activityModel.addElement("Offer received - " + offer.getStatus() + 
                                       " (Deadline: " + offer.getAcceptanceDeadline() + ")");
            }
            
            // If no activities, show placeholder
            if (activityModel.isEmpty()) {
                activityModel.addElement("No recent activity");
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
            
            // Combine stats and activity
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, statsSection, activityPanel);
            splitPane.setDividerLocation(200);
            splitPane.setBorder(null);
            
            // Add everything to content container
            contentContainer.add(welcomeSubPanel, BorderLayout.NORTH);
            contentContainer.add(splitPane, BorderLayout.CENTER);
            
            // Assemble the main panel
            mainContentPanel.add(accentHeaderPanel, BorderLayout.NORTH);
            mainContentPanel.add(contentContainer, BorderLayout.CENTER);
            
            add(mainContentPanel, BorderLayout.CENTER);
            
            // Bottom info panel with last login
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