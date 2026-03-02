// com.placement.system.views/MyApplicationsPanel.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MyApplicationsPanel extends JPanel {
    
    // Color scheme matching the rest of the application
    private static final Color MAIN_BG = new Color(240, 240, 240);
    private static final Color ACCENT = new Color(0x54, 0x54, 0x54);  // #545454
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(200, 200, 200);
    
    private final DefaultTableModel model;
    private final JTable table;
    private final JLabel lblCount;
    private String studentId;
    
    public MyApplicationsPanel() {
        setLayout(new BorderLayout());
        setBackground(MAIN_BG);
        
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
        String[] columns = {"Company", "Role", "Applied On", "Status"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
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
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(CARD_BG);
        card.add(scrollPane, BorderLayout.CENTER);
        
        // Status label at bottom
        lblCount = new JLabel("0 application(s) total.");
        lblCount.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblCount.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            new EmptyBorder(8, 8, 8, 8)
        ));
        card.add(lblCount, BorderLayout.SOUTH);
        
        mainContent.add(card, BorderLayout.CENTER);
        
        add(mainContent, BorderLayout.CENTER);
    }
    
    /**
     * Load applications for a specific student
     */
    public void loadApplicationsForStudent(String studentId) {
        this.studentId = studentId;
        refreshTable();
    }
    
    /**
     * Refresh the table data (can be called from parent dashboard)
     */
    public void refreshApplications() {
        refreshTable();
    }
    
    private void refreshTable() {
        model.setRowCount(0);
        
        if (studentId == null) {
            lblCount.setText("0 application(s) total.");
            return;
        }
        
        // Get applications from mock service
        List<ApplicationView> applications = MockApplicationService.getApplicationsForStudent(studentId);
        
        // Add rows to table
        for (ApplicationView app : applications) {
            model.addRow(new Object[]{
                app.getCompanyName(),
                app.getJobTitle(),
                app.getApplicationDate(),
                formatStatus(app.getStatus())
            });
        }
        
        // Update count label
        lblCount.setText(applications.size() + " application(s) total.");
        
        // Auto-resize columns after data load
        resizeTableColumns();
    }
    
    private void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Dialog", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));
        table.getTableHeader().setBackground(ACCENT);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowGrid(true);
        table.setGridColor(BORDER);
        table.setSelectionBackground(new Color(220, 240, 255));
        table.setIntercellSpacing(new Dimension(5, 5));
        table.setRowMargin(2);
    }
    
    private void resizeTableColumns() {
        // Set preferred column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(150); // Company
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Role
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Applied On
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Status
    }
    
    private String formatStatus(String status) {
        if (status == null || status.isEmpty()) return "";
        
        // Convert to proper case (e.g., "APPLIED" -> "Applied")
        String lower = status.toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
    
    private JPanel createSectionHeader(String title) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(ACCENT);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(40, 40, 40)),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        
        bar.add(titleLabel, BorderLayout.WEST);
        
        return bar;
    }
    
    // ==================== MOCK DATA MODELS (Replace with your actual models) ====================
    
    public static class ApplicationView {
        private String companyName;
        private String jobTitle;
        private String applicationDate;
        private String status;
        
        public ApplicationView(String companyName, String jobTitle, String applicationDate, String status) {
            this.companyName = companyName;
            this.jobTitle = jobTitle;
            this.applicationDate = applicationDate;
            this.status = status;
        }
        
        public String getCompanyName() { return companyName; }
        public String getJobTitle() { return jobTitle; }
        public String getApplicationDate() { return applicationDate; }
        public String getStatus() { return status; }
    }
    
    public static class MockApplicationService {
        
        private static List<ApplicationView> mockApplications = new ArrayList<>();
        
        static {
            // Add some mock data
            mockApplications.add(new ApplicationView(
                "Google", 
                "Software Engineer Intern", 
                LocalDate.now().minusDays(5).toString(), 
                "APPLIED"
            ));
            mockApplications.add(new ApplicationView(
                "Microsoft", 
                "Data Analyst", 
                LocalDate.now().minusDays(12).toString(), 
                "SHORTLISTED"
            ));
            mockApplications.add(new ApplicationView(
                "Amazon", 
                "Cloud Engineer", 
                LocalDate.now().minusDays(3).toString(), 
                "UNDER_REVIEW"
            ));
            mockApplications.add(new ApplicationView(
                "TechCorp", 
                "Frontend Developer", 
                LocalDate.now().minusDays(20).toString(), 
                "REJECTED"
            ));
            mockApplications.add(new ApplicationView(
                "Startup Inc", 
                "Full Stack Developer", 
                LocalDate.now().minusDays(1).toString(), 
                "APPLIED"
            ));
        }
        
        public static List<ApplicationView> getApplicationsForStudent(String studentId) {
            // In a real implementation, you would filter by studentId
            // For now, return all mock data
            return mockApplications;
        }
    }
}