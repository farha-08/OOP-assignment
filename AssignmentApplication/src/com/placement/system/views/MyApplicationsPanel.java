// com.placement.system.views/MyApplicationsPanel.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.placement.system.models.Application;
import com.placement.system.models.Job;
import com.placement.system.models.Student;
import com.placement.system.models.Company;
import com.placement.system.models.User;
import com.placement.system.utils.SessionManager;
import com.placement.system.dao.ApplicationDAO;
import com.placement.system.dao.JobDAO;
import com.placement.system.dao.CompanyDAO;
import com.placement.system.dao.StudentDAO;

public class MyApplicationsPanel extends JPanel {
    
    // Color scheme matching the rest of the application
    private static final Color MAIN_BG = new Color(240, 240, 240);
    private static final Color ACCENT = new Color(0x54, 0x54, 0x54);  // #545454
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(200, 200, 200);
    
    private final DefaultTableModel model;
    private final JTable table;
    private final JLabel lblCount;
    private Student currentStudent;
    private List<Application> applications;
    private java.util.Map<Integer, String> companyNameCache = new java.util.HashMap<>();
    private java.util.Map<Integer, String> jobTitleCache = new java.util.HashMap<>();
    
    public MyApplicationsPanel() {
        setLayout(new BorderLayout());
        setBackground(MAIN_BG);
        
        // Load current student
        loadCurrentStudent();
        
        // Create main content panel with proper spacing
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(MAIN_BG);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Section header
        JPanel headerPanel = createSectionHeader("My Applications");
        mainContent.add(headerPanel, BorderLayout.NORTH);
        
        // Table card
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        
        // Create table model
        String[] columns = {"Company", "Role", "Applied On", "Status", "Actions"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only the Actions column might be clickable in a real implementation
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };
        
        // Create and style table
        table = new JTable(model);
        styleTable(table);
        
        // Add double-click listener to view application details
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    viewApplicationDetails(table.getSelectedRow());
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(CARD_BG);
        card.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel for actions
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(CARD_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton viewDetailsBtn = new JButton("View Details");
        styleButton(viewDetailsBtn);
        viewDetailsBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                viewApplicationDetails(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select an application to view details.", 
                    "No Selection", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton refreshBtn = new JButton("Refresh");
        styleButton(refreshBtn);
        refreshBtn.addActionListener(e -> refreshApplications());
        
        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(refreshBtn);
        
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        // Status label at bottom
        lblCount = new JLabel("Loading applications...");
        lblCount.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblCount.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            new EmptyBorder(8, 8, 8, 8)
        ));
        card.add(lblCount, BorderLayout.NORTH);
        
        mainContent.add(card, BorderLayout.CENTER);
        
        add(mainContent, BorderLayout.CENTER);
        
        // Load applications
        refreshApplications();
    }
    
    /**
     * Load current student from session/database
     */
    private void loadCurrentStudent() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            if (user instanceof Student) {
                this.currentStudent = (Student) user;
            } else {
                // Load from DAO if not Student instance
                this.currentStudent = StudentDAO.getInstance().getStudent(user.getId());
                if (this.currentStudent != null) {
                    SessionManager.getInstance().setCurrentUser(this.currentStudent);
                }
            }
        }
    }
    
    /**
     * Refresh the table data (can be called from parent dashboard)
     */
    public void refreshApplications() {
        if (currentStudent == null) {
            loadCurrentStudent();
            if (currentStudent == null) {
                lblCount.setText("Please log in to view applications.");
                return;
            }
        }
        
        // Fetch applications from database
        ApplicationDAO appDAO = ApplicationDAO.getInstance();
        applications = appDAO.getApplicationsByStudent(currentStudent.getId());
        
        // Pre-cache job and company names
        cacheJobAndCompanyNames();
        
        // Refresh table display
        refreshTable();
    }
    
    /**
     * Cache job titles and company names for all applications
     */
    private void cacheJobAndCompanyNames() {
        JobDAO jobDAO = JobDAO.getInstance();
        CompanyDAO companyDAO = CompanyDAO.getInstance();
        
        for (Application app : applications) {
            // Cache job title
            if (!jobTitleCache.containsKey(app.getJobId())) {
                Job job = jobDAO.getJob(app.getJobId());
                if (job != null) {
                    jobTitleCache.put(app.getJobId(), job.getJobTitle());
                    
                    // Also cache company name for this job
                    if (!companyNameCache.containsKey(job.getCompanyId())) {
                        Company company = companyDAO.getCompany(job.getCompanyId());
                        if (company != null) {
                            companyNameCache.put(job.getCompanyId(), company.getCompanyName());
                        } else {
                            companyNameCache.put(job.getCompanyId(), "Unknown Company");
                        }
                    }
                } else {
                    jobTitleCache.put(app.getJobId(), "Job Deleted");
                }
            }
        }
    }
    
    /**
     * Get job title by job ID
     */
    private String getJobTitle(int jobId) {
        return jobTitleCache.getOrDefault(jobId, "Unknown Position");
    }
    
    /**
     * Get company name by company ID (from job)
     */
    private String getCompanyNameForApplication(Application app) {
        JobDAO jobDAO = JobDAO.getInstance();
        Job job = jobDAO.getJob(app.getJobId());
        if (job != null) {
            return companyNameCache.getOrDefault(job.getCompanyId(), "Unknown Company");
        }
        return "Unknown Company";
    }
    
    private void refreshTable() {
        model.setRowCount(0);
        
        if (applications == null || applications.isEmpty()) {
            lblCount.setText("0 application(s) total. Apply to jobs to see them here.");
            return;
        }
        
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd MMM yyyy");
        
        // Add rows to table
        for (Application app : applications) {
            model.addRow(new Object[]{
                getCompanyNameForApplication(app),
                getJobTitle(app.getJobId()),
                app.getApplicationDate().format(df),
                formatStatus(app.getStatus()),
                "View Details"
            });
        }
        
        // Update count label
        lblCount.setText(applications.size() + " application(s) total.");
        
        // Auto-resize columns after data load
        resizeTableColumns();
    }
    
    /**
     * View detailed information about a selected application
     */
    private void viewApplicationDetails(int selectedRow) {
        if (selectedRow < 0 || selectedRow >= applications.size()) return;
        
        Application app = applications.get(selectedRow);
        
        // Fetch job details
        JobDAO jobDAO = JobDAO.getInstance();
        Job job = jobDAO.getJob(app.getJobId());
        
        if (job == null) {
            JOptionPane.showMessageDialog(this, 
                "Job details not available.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get company name
        String companyName = getCompanyNameForApplication(app);
        
        // Create details dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Application Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(MAIN_BG);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ACCENT);
        JLabel titleLabel = new JLabel("  Application Details");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Application Info Section
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        infoPanel.setBackground(CARD_BG);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        infoPanel.add(createInfoLabel("Company:"));
        infoPanel.add(createInfoValue(companyName));
        
        infoPanel.add(createInfoLabel("Position:"));
        infoPanel.add(createInfoValue(job.getJobTitle()));
        
        infoPanel.add(createInfoLabel("Application Date:"));
        infoPanel.add(createInfoValue(app.getApplicationDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))));
        
        infoPanel.add(createInfoLabel("Status:"));
        infoPanel.add(createStatusValue(app.getStatus()));
        
        infoPanel.add(createInfoLabel("Application ID:"));
        infoPanel.add(createInfoValue(String.valueOf(app.getApplicationId())));
        
        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Job Details Section
        JPanel jobDetailsPanel = new JPanel(new BorderLayout());
        jobDetailsPanel.setBackground(CARD_BG);
        jobDetailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER), 
            "Job Details",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Dialog", Font.BOLD, 12)
        ));
        
        JTextArea jobDescArea = new JTextArea(job.getDescription());
        jobDescArea.setEditable(false);
        jobDescArea.setLineWrap(true);
        jobDescArea.setWrapStyleWord(true);
        jobDescArea.setFont(new Font("Dialog", Font.PLAIN, 11));
        jobDescArea.setMargin(new Insets(8, 8, 8, 8));
        jobDescArea.setBackground(CARD_BG);
        
        JScrollPane descScroll = new JScrollPane(jobDescArea);
        descScroll.setPreferredSize(new Dimension(450, 120));
        descScroll.setBorder(BorderFactory.createLineBorder(BORDER));
        
        jobDetailsPanel.add(descScroll, BorderLayout.CENTER);
        mainPanel.add(jobDetailsPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Additional Info
        JPanel additionalPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        additionalPanel.setBackground(CARD_BG);
        additionalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        additionalPanel.add(createInfoLabel("Location:"));
        additionalPanel.add(createInfoValue(job.getLocation() != null ? job.getLocation() : "N/A"));
        
        additionalPanel.add(createInfoLabel("Employment Type:"));
        additionalPanel.add(createInfoValue(job.getEmploymentType() != null ? job.getEmploymentType() : "N/A"));
        
        additionalPanel.add(createInfoLabel("Salary Range:"));
        additionalPanel.add(createInfoValue(job.getSalaryRange() != null ? job.getSalaryRange() : "Negotiable"));
        
        additionalPanel.add(createInfoLabel("Minimum CGPA:"));
        additionalPanel.add(createInfoValue(String.valueOf(job.getMinCgpa())));
        
        mainPanel.add(additionalPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(MAIN_BG);
        
        JButton closeBtn = new JButton("Close");
        styleButton(closeBtn);
        closeBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel);
        
        JScrollPane mainScroll = new JScrollPane(mainPanel);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        
        dialog.add(mainScroll, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Dialog", Font.BOLD, 12));
        label.setForeground(new Color(70, 70, 70));
        return label;
    }
    
    private JLabel createInfoValue(String value) {
        JLabel label = new JLabel(value);
        label.setFont(new Font("Dialog", Font.PLAIN, 12));
        return label;
    }
    
    private JLabel createStatusValue(String status) {
        JLabel label = new JLabel(formatStatus(status));
        label.setFont(new Font("Dialog", Font.BOLD, 12));
        
        // Color code based on status
        String formattedStatus = formatStatus(status);
        switch (formattedStatus) {
            case "Accepted":
                label.setForeground(new Color(0, 128, 0));
                break;
            case "Rejected":
                label.setForeground(new Color(178, 34, 34));
                break;
            case "Shortlisted":
                label.setForeground(new Color(255, 140, 0));
                break;
            case "Offered":
                label.setForeground(new Color(0, 102, 204));
                break;
            default:
                label.setForeground(new Color(100, 100, 100));
                break;
        }
        return label;
    }
    
    private void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(new Font("Dialog", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));
        table.getTableHeader().setBackground(ACCENT);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowGrid(true);
        table.setGridColor(BORDER);
        table.setSelectionBackground(new Color(220, 240, 255));
        table.setIntercellSpacing(new Dimension(8, 5));
        table.setRowMargin(2);
    }
    
    private void styleButton(JButton button) {
        button.setBackground(new Color(100, 100, 100));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Dialog", Font.PLAIN, 11));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            new EmptyBorder(6, 12, 6, 12)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    
    private void resizeTableColumns() {
        // Set preferred column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(150); // Company
        table.getColumnModel().getColumn(1).setPreferredWidth(180); // Role
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Applied On
        table.getColumnModel().getColumn(3).setPreferredWidth(90);  // Status
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Actions
    }
    
    private String formatStatus(String status) {
        if (status == null || status.isEmpty()) return "";
        
        // Handle different status formats
        String lower = status.toLowerCase();
        switch (lower) {
            case "applied":
                return "Applied";
            case "under_review":
            case "under review":
                return "Under Review";
            case "shortlisted":
                return "Shortlisted";
            case "rejected":
                return "Rejected";
            case "offered":
                return "Offered";
            case "accepted":
                return "Accepted";
            case "withdrawn":
                return "Withdrawn";
            default:
                return status;
        }
    }
    
    private JPanel createSectionHeader(String title) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(ACCENT);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(40, 40, 40)),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        
        bar.add(titleLabel, BorderLayout.WEST);
        
        return bar;
    }
    
    /**
     * Get the current student ID (for external use)
     */
    public String getStudentId() {
        return currentStudent != null ? String.valueOf(currentStudent.getId()) : null;
    }
    
    /**
     * Load applications for a specific student (called from dashboard)
     */
    public void loadApplicationsForStudent(String studentId) {
        if (currentStudent == null || !String.valueOf(currentStudent.getId()).equals(studentId)) {
            if (studentId != null) {
                try {
                    int id = Integer.parseInt(studentId);
                    currentStudent = StudentDAO.getInstance().getStudent(id);
                    if (currentStudent != null) {
                        SessionManager.getInstance().setCurrentUser(currentStudent);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid student ID format: " + studentId);
                }
            }
        }
        refreshApplications();
    }
}