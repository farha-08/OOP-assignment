// com.placement.system.views/JobBrowserPanel.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import com.placement.system.models.Student;
import com.placement.system.models.Job;
import com.placement.system.models.Application;
import com.placement.system.models.Company;
import com.placement.system.models.User;
import com.placement.system.utils.SessionManager;
import com.placement.system.dao.StudentDAO;
import com.placement.system.dao.JobDAO;
import com.placement.system.dao.ApplicationDAO;
import com.placement.system.dao.CompanyDAO;

public class JobBrowserPanel extends JPanel {
    
    // Color palette matching the system
    private static final Color MAIN_BG = Color.decode("#CFCFCF");
    private static final Color ACCENT  = Color.decode("#545454");
    private static final Color BTN_BG  = Color.decode("#7D7D7D");
    private static final Color ROW_ALT = new Color(0xE2E2E2);
    private static final Font TEXTAREA_FONT = new Font("SansSerif", Font.PLAIN, 12);
    
    // Current student from database
    private Student currentStudent;
    private List<Job> allJobs;
    private Map<Integer, String> companyNameCache = new HashMap<>(); // Cache company names by ID
    
    // UI Components
    private JTextField txtSearch = new JTextField();
    private JComboBox<String> cbType = new JComboBox<>();
    private JComboBox<String> cbLocation = new JComboBox<>();
    private JComboBox<String> cbCompany = new JComboBox<>();
    private JComboBox<String> cbTime = new JComboBox<>();
    
    private JLabel lblStatus = new JLabel(" ");
    
    private DefaultTableModel model;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    
    public JobBrowserPanel() {
        loadCurrentStudent();
        loadJobsFromDatabase();
        initializePanel();
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
                    // Update session with Student object
                    SessionManager.getInstance().setCurrentUser(this.currentStudent);
                }
            }
        }
        
        // Fallback - should not happen in production
        if (this.currentStudent == null) {
            System.err.println("Warning: No student found in session for JobBrowserPanel");
        }
    }
    
    /**
     * Load all active jobs from database
     */
    private void loadJobsFromDatabase() {
        try {
            allJobs = JobDAO.getInstance().getAllJobs(true); // Only active jobs
            
            // Filter jobs that are still within deadline
            allJobs = allJobs.stream()
                .filter(job -> job.getApplicationDeadline() != null && 
                       !job.getApplicationDeadline().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
            
            // Pre-cache company names for all jobs
            cacheCompanyNames();
                
        } catch (Exception e) {
            System.err.println("Error loading jobs from database: " + e.getMessage());
            allJobs = new ArrayList<>();
        }
    }
    
    /**
     * Cache company names for all jobs to avoid repeated DB queries
     */
    private void cacheCompanyNames() {
        CompanyDAO companyDAO = CompanyDAO.getInstance();
        for (Job job : allJobs) {
            if (!companyNameCache.containsKey(job.getCompanyId())) {
                Company company = companyDAO.getCompany(job.getCompanyId());
                if (company != null) {
                    companyNameCache.put(job.getCompanyId(), company.getCompanyName());
                } else {
                    companyNameCache.put(job.getCompanyId(), "Unknown Company");
                }
            }
        }
        
        // Also pre-load all company names for filter dropdown
        List<Company> allCompanies = companyDAO.getAllCompanies();
        for (Company company : allCompanies) {
            companyNameCache.putIfAbsent(company.getId(), company.getCompanyName());
        }
    }
    
    /**
     * Get company name by company ID
     */
    private String getCompanyName(int companyId) {
        return companyNameCache.getOrDefault(companyId, "Company " + companyId);
    }
    
    /**
     * Get all unique company names for filter dropdown
     */
    private Set<String> getAllCompanyNames() {
        Set<String> companyNames = new TreeSet<>();
        for (Job job : allJobs) {
            String name = getCompanyName(job.getCompanyId());
            companyNames.add(name);
        }
        return companyNames;
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(MAIN_BG);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Main content panel with border
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(MAIN_BG);
        center.setBorder(BorderFactory.createLineBorder(ACCENT));
        
        // Top wrapper for header + filters
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(MAIN_BG);
        
        // Section header
        JPanel sectionHeader = new JPanel(new BorderLayout());
        sectionHeader.setBackground(ACCENT);
        JLabel sec = new JLabel("  Recruitment Offers");
        sec.setForeground(Color.WHITE);
        sec.setFont(new Font("SansSerif", Font.BOLD, 13));
        sectionHeader.add(sec, BorderLayout.CENTER);
        
        topSection.add(sectionHeader, BorderLayout.NORTH);
        topSection.add(buildFiltersPanel(), BorderLayout.CENTER);
        
        center.add(topSection, BorderLayout.NORTH);
        
        // Table setup
        String[] cols = {"Company", "Title", "Type", "Salary", "Location", "Deadline", "Eligible"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        
        table = new JTable(model);
        styleTable(table);
        
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        // Custom renderers
        table.getColumnModel().getColumn(6).setCellRenderer(new EligibleRenderer());
        
        AlternateRowRenderer alt = new AlternateRowRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 6) table.getColumnModel().getColumn(i).setCellRenderer(alt);
        }
        
        // Load data
        reloadRows(allJobs);
        
        // Double-click listener
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    openSelectedJob();
                }
            }
        });
        
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, ACCENT));
        center.add(sp, BorderLayout.CENTER);
        
        // Bottom panel with status and buttons
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(MAIN_BG);
        lblStatus.setBorder(new EmptyBorder(6, 10, 6, 10));
        bottom.add(lblStatus, BorderLayout.WEST);
        
        JButton reset = new JButton("Reset Filters");
        styleButton(reset, 140);
        reset.addActionListener(e -> resetFilters());
        
        JButton viewDetails = new JButton("View Details");
        styleButton(viewDetails, 140);
        viewDetails.addActionListener(e -> openSelectedJob());
        
        JPanel bottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        bottomRight.setBackground(MAIN_BG);
        bottomRight.add(reset);
        bottomRight.add(viewDetails);
        
        bottom.add(bottomRight, BorderLayout.EAST);
        center.add(bottom, BorderLayout.SOUTH);
        
        add(center, BorderLayout.CENTER);
        
        updateStatus();
        applyFilters();
    }
    
    private JPanel buildFiltersPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(MAIN_BG);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(MAIN_BG);
        
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 0, 8, 12);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 1: Search + Type
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        row.add(label("Search:"), gc);
        
        gc.gridx = 1; gc.gridy = 0; gc.weightx = 1;
        txtSearch.setToolTipText("Job title...");
        row.add(txtSearch, gc);
        
        gc.gridx = 2; gc.gridy = 0; gc.weightx = 0;
        row.add(label("Type:"), gc);
        
        gc.gridx = 3; gc.gridy = 0; gc.weightx = 0.5;
        row.add(cbType, gc);
        
        // Row 2: Location + Company
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0;
        row.add(label("Location:"), gc);
        
        gc.gridx = 1; gc.gridy = 1; gc.weightx = 1;
        row.add(cbLocation, gc);
        
        gc.gridx = 2; gc.gridy = 1; gc.weightx = 0;
        row.add(label("Company:"), gc);
        
        gc.gridx = 3; gc.gridy = 1; gc.weightx = 0.5;
        row.add(cbCompany, gc);
        
        // Row 3: Time
        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0;
        row.add(label("Time:"), gc);
        
        gc.gridx = 1; gc.gridy = 2; gc.weightx = 1;
        row.add(cbTime, gc);
        
        fillFiltersFromData();
        
        // Add document listener for search
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });
        
        // Add action listeners for combo boxes
        ActionListener filterListener = e -> applyFilters();
        cbType.addActionListener(filterListener);
        cbLocation.addActionListener(filterListener);
        cbCompany.addActionListener(filterListener);
        cbTime.addActionListener(filterListener);
        
        p.add(row, BorderLayout.CENTER);
        
        JPanel sep = new JPanel();
        sep.setPreferredSize(new Dimension(1, 1));
        sep.setBackground(ACCENT);
        p.add(sep, BorderLayout.SOUTH);
        
        return p;
    }
    
    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.BLACK);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return l;
    }
    
    private void fillFiltersFromData() {
        if (allJobs == null) return;
        
        Set<String> types = allJobs.stream()
            .map(j -> j.getEmploymentType())
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(TreeSet::new));
        cbType.removeAllItems();
        cbType.addItem("All Types");
        for (String t : types) cbType.addItem(t);
        
        Set<String> locs = allJobs.stream()
            .map(j -> j.getLocation())
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(TreeSet::new));
        cbLocation.removeAllItems();
        cbLocation.addItem("All Locations");
        for (String loc : locs) cbLocation.addItem(loc);
        
        // Use actual company names from CompanyDAO
        Set<String> companies = getAllCompanyNames();
        cbCompany.removeAllItems();
        cbCompany.addItem("All Companies");
        for (String c : companies) cbCompany.addItem(c);
        
        cbTime.removeAllItems();
        cbTime.addItem("Any time");
        cbTime.addItem("Past month");
        cbTime.addItem("Past week");
        cbTime.addItem("Past 24 hours");
    }
    
    private void resetFilters() {
        txtSearch.setText("");
        cbType.setSelectedIndex(0);
        cbLocation.setSelectedIndex(0);
        cbCompany.setSelectedIndex(0);
        cbTime.setSelectedIndex(0);
        applyFilters();
    }
    
    private void reloadRows(List<Job> jobs) {
        model.setRowCount(0);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        if (jobs == null) return;
        
        for (Job job : jobs) {
            boolean eligible = isStudentEligible(job);
            model.addRow(new Object[]{
                    getCompanyName(job.getCompanyId()),
                    job.getJobTitle(),
                    job.getEmploymentType(),
                    job.getSalaryRange() != null ? job.getSalaryRange() : "Negotiable",
                    job.getLocation(),
                    job.getApplicationDeadline() != null ? 
                        job.getApplicationDeadline().format(df) : "N/A",
                    eligible ? "Yes" : "No"
            });
        }
    }
    
    /**
     * Check if current student is eligible for a job
     */
    private boolean isStudentEligible(Job job) {
        if (currentStudent == null) return false;
        
        // Check if student is already placed or blocked
        if ("Placed".equals(currentStudent.getPlacementStatus()) ||
            "Blocked".equals(currentStudent.getPlacementStatus())) {
            return false;
        }
        
        // Check CGPA requirement
        if (currentStudent.getCgpa() < job.getMinCgpa()) {
            return false;
        }
        
        // Check if student has already applied
        ApplicationDAO appDAO = ApplicationDAO.getInstance();
        if (appDAO.hasApplied(currentStudent.getId(), job.getJobId())) {
            return false;
        }
        
        return true;
    }
    
    private void applyFilters() {
        if (allJobs == null) return;
        
        RowFilter<DefaultTableModel, Integer> rf = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                int modelRow = entry.getIdentifier();
                if (modelRow >= allJobs.size()) return false;
                
                Job job = allJobs.get(modelRow);
                
                // Search filter
                String q = txtSearch.getText().trim().toLowerCase(Locale.ROOT);
                if (!q.isEmpty()) {
                    String hay = (job.getJobTitle() + " " + job.getDescription()).toLowerCase(Locale.ROOT);
                    if (!hay.contains(q)) return false;
                }
                
                // Type filter
                String typePick = (String) cbType.getSelectedItem();
                if (typePick != null && !typePick.equals("All Types")) {
                    if (job.getEmploymentType() == null || !job.getEmploymentType().equals(typePick)) return false;
                }
                
                // Location filter
                String locPick = (String) cbLocation.getSelectedItem();
                if (locPick != null && !locPick.equals("All Locations")) {
                    if (job.getLocation() == null || !job.getLocation().equals(locPick)) return false;
                }
                
                // Company filter - using actual company name
                String compPick = (String) cbCompany.getSelectedItem();
                if (compPick != null && !compPick.equals("All Companies")) {
                    String companyName = getCompanyName(job.getCompanyId());
                    if (!companyName.equals(compPick)) return false;
                }
                
                // Time filter
                String timePick = (String) cbTime.getSelectedItem();
                if (timePick != null && !timePick.equals("Any time")) {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime cutoff;
                    switch (timePick) {
                        case "Past month" -> cutoff = now.minusDays(30);
                        case "Past week" -> cutoff = now.minusDays(7);
                        case "Past 24 hours" -> cutoff = now.minusHours(24);
                        default -> cutoff = null;
                    }
                    if (cutoff != null && job.getPostedDate() != null && 
                        job.getPostedDate().isBefore(cutoff)) return false;
                }
                
                return true;
            }
        };
        
        sorter.setRowFilter(rf);
        updateStatus();
    }
    
    private void updateStatus() {
        int shown = table.getRowCount();
        int total = allJobs != null ? allJobs.size() : 0;
        lblStatus.setText(shown + " offer(s) found out of " + total + " total. Double-click to view details.");
    }
    
    private void openSelectedJob() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an offer first.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        if (modelRow >= allJobs.size()) return;
        
        Job selected = allJobs.get(modelRow);
        showJobDetailsDialog(selected);
    }
    
    private void showJobDetailsDialog(Job job) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Job Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(700, 650);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(MAIN_BG);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Get company name
        String companyName = getCompanyName(job.getCompanyId());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ACCENT);
        JLabel titleLabel = new JLabel("  " + job.getJobTitle() + " at " + companyName);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Job Details
        mainPanel.add(createInfoSection("Job Details", new String[][]{
            {"Company:", companyName},
            {"Title:", job.getJobTitle()},
            {"Department:", job.getDepartment() != null ? job.getDepartment() : "N/A"},
            {"Type:", job.getEmploymentType() != null ? job.getEmploymentType() : "N/A"},
            {"Salary:", job.getSalaryRange() != null ? job.getSalaryRange() : "Negotiable"},
            {"Location:", job.getLocation() != null ? job.getLocation() : "N/A"},
            {"Vacancies:", String.valueOf(job.getVacancies())},
            {"Deadline:", job.getApplicationDeadline() != null ? 
                job.getApplicationDeadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "N/A"},
            {"Posted:", job.getPostedDate() != null ? 
                job.getPostedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "N/A"}
        }));
        
        // Description
        JTextArea descArea = new JTextArea(job.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(TEXTAREA_FONT);
        descArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT), "Description"));
        descScroll.setPreferredSize(new Dimension(650, 120));
        mainPanel.add(descScroll);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Eligibility Criteria
        mainPanel.add(createInfoSection("Eligibility Criteria", new String[][]{
            {"Minimum CGPA:", String.valueOf(job.getMinCgpa())},
            {"Status:", job.isActive() ? "Active" : "Closed"}
        }));
        
        // Your Eligibility
        boolean eligible = isStudentEligible(job);
        boolean alreadyApplied = checkIfApplied(job.getJobId());
        
        JPanel eligPanel = new JPanel(new BorderLayout());
        eligPanel.setBackground(MAIN_BG);
        eligPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT), "Your Eligibility"));
        
        String statusMessage;
        Color statusColor;
        if (alreadyApplied) {
            statusMessage = "Status: Already Applied";
            statusColor = new Color(255, 140, 0); // Orange
        } else if (eligible) {
            statusMessage = "Status: Eligible - You meet all requirements!";
            statusColor = new Color(0x0A6E2A); // Green
        } else {
            statusMessage = "Status: Not Eligible";
            statusColor = Color.RED;
        }
        
        JLabel eligStatus = new JLabel(statusMessage);
        eligStatus.setFont(new Font("SansSerif", Font.BOLD, 12));
        eligStatus.setForeground(statusColor);
        eligStatus.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        eligPanel.add(eligStatus, BorderLayout.NORTH);
        
        // Reasons if not eligible
        if (!eligible && !alreadyApplied && currentStudent != null) {
            JTextArea eligReasons = new JTextArea();
            eligReasons.setEditable(false);
            eligReasons.setLineWrap(true);
            eligReasons.setWrapStyleWord(true);
            eligReasons.setFont(TEXTAREA_FONT);
            eligReasons.setMargin(new Insets(10, 10, 10, 10));
            
            StringBuilder reasons = new StringBuilder();
            if (currentStudent.getCgpa() < job.getMinCgpa()) {
                reasons.append("- Your CGPA (").append(currentStudent.getCgpa())
                       .append(") is below minimum required (").append(job.getMinCgpa()).append(")\n");
            }
            if ("Placed".equals(currentStudent.getPlacementStatus())) {
                reasons.append("- You have already been placed\n");
            }
            if ("Blocked".equals(currentStudent.getPlacementStatus())) {
                reasons.append("- Your account is blocked from applying\n");
            }
            
            eligReasons.setText(reasons.length() > 0 ? 
                "You do not meet the following criteria:\n" + reasons.toString() : 
                "You are not eligible for this position.");
            
            eligPanel.add(new JScrollPane(eligReasons), BorderLayout.CENTER);
        }
        
        mainPanel.add(eligPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(MAIN_BG);
        
        JButton closeBtn = new JButton("Close");
        styleButton(closeBtn, 100);
        closeBtn.addActionListener(e -> dialog.dispose());
        
        JButton applyBtn = new JButton("Apply");
        styleButton(applyBtn, 100);
        applyBtn.addActionListener(e -> {
            if (alreadyApplied) {
                JOptionPane.showMessageDialog(dialog, 
                    "You have already applied for this position.", 
                    "Already Applied", 
                    JOptionPane.WARNING_MESSAGE);
            } else if (!eligible) {
                JOptionPane.showMessageDialog(dialog, 
                    "You are not eligible for this position.", 
                    "Cannot Apply", 
                    JOptionPane.WARNING_MESSAGE);
            } else {
                submitApplication(job, dialog);
            }
        });
        
        buttonPanel.add(closeBtn);
        buttonPanel.add(applyBtn);
        mainPanel.add(buttonPanel);
        
        JScrollPane mainScroll = new JScrollPane(mainPanel);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        
        dialog.add(mainScroll, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Check if student has already applied for a job
     */
    private boolean checkIfApplied(int jobId) {
        if (currentStudent == null) return false;
        ApplicationDAO appDAO = ApplicationDAO.getInstance();
        return appDAO.hasApplied(currentStudent.getId(), jobId);
    }
    
    /**
     * Submit application for a job
     */
    private void submitApplication(Job job, JDialog dialog) {
        if (currentStudent == null) {
            JOptionPane.showMessageDialog(dialog, 
                "Please log in to apply.", 
                "Login Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ApplicationDAO appDAO = ApplicationDAO.getInstance();
        
        Application application = new Application();
        application.setJobId(job.getJobId());
        application.setStudentId(currentStudent.getId());
        application.setApplicationDate(LocalDateTime.now());
        application.setStatus("Applied");
        
        boolean success = appDAO.createApplication(application);
        
        if (success) {
            JOptionPane.showMessageDialog(dialog, 
                "Application submitted successfully!\n\n" +
                "Job: " + job.getJobTitle() + "\n" +
                "Company: " + getCompanyName(job.getCompanyId()) + "\n" +
                "Application ID: " + application.getApplicationId(),
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            
            // Refresh the table to update eligibility status
            reloadRows(allJobs);
            applyFilters();
        } else {
            JOptionPane.showMessageDialog(dialog, 
                "Failed to submit application. Please try again.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createInfoSection(String title, String[][] data) {
        JPanel panel = new JPanel(new GridLayout(data.length, 2, 10, 5));
        panel.setBackground(MAIN_BG);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT), title));
        
        for (String[] row : data) {
            JLabel label = new JLabel(row[0]);
            label.setFont(new Font("SansSerif", Font.BOLD, 12));
            
            JLabel value = new JLabel(row[1]);
            value.setFont(new Font("SansSerif", Font.PLAIN, 12));
            
            panel.add(label);
            panel.add(value);
        }
        
        return panel;
    }
    
    // ==================== STATIC UTILITY METHODS ====================
    
    private static void styleTable(JTable table) {
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(ACCENT);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        
        JTableHeader hdr = table.getTableHeader();
        hdr.setBackground(MAIN_BG);
        hdr.setForeground(Color.BLACK);
        hdr.setBorder(BorderFactory.createLineBorder(ACCENT));
        hdr.setFont(new Font("SansSerif", Font.BOLD, 12));
    }
    
    private static void styleButton(JButton b, int fixedWidth) {
        b.setBackground(BTN_BG);
        b.setForeground(Color.BLACK);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(true);
        b.setBorder(BorderFactory.createLineBorder(ACCENT));
        b.setUI(new SolidButtonUI());
        
        if (fixedWidth > 0) b.setPreferredSize(new Dimension(fixedWidth, 32));
        else b.setPreferredSize(new Dimension(160, 32));
    }
    
    // ==================== INNER CLASSES ====================
    
    static class SolidButtonUI extends BasicButtonUI {
        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(b.getBackground());
            g2.fillRect(0, 0, b.getWidth(), b.getHeight());
            g2.dispose();
            super.paint(g, c);
        }
    }
    
    static class AlternateRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus,
                                                      int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (isSelected) {
                setBackground(ACCENT);
                setForeground(Color.WHITE);
            } else {
                setBackground((row % 2 == 0) ? Color.WHITE : ROW_ALT);
                setForeground(Color.BLACK);
            }
            return this;
        }
    }
    
    static class EligibleRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus,
                                                      int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            String s = String.valueOf(value);
            setFont(getFont().deriveFont(Font.BOLD));
            
            if (isSelected) {
                setBackground(ACCENT);
                setForeground(Color.WHITE);
                return this;
            }
            
            setBackground((row % 2 == 0) ? Color.WHITE : ROW_ALT);
            if ("Yes".equalsIgnoreCase(s)) setForeground(new Color(0x0A6E2A));
            else if ("No".equalsIgnoreCase(s)) setForeground(Color.RED);
            else setForeground(Color.BLACK);
            
            return this;
        }
    }
}