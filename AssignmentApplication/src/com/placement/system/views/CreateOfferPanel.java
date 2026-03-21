// com.placement.system.views/CreateOfferPanel.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.placement.system.models.Job;
import com.placement.system.models.Company;
import com.placement.system.models.User;
import com.placement.system.utils.SessionManager;
import com.placement.system.dao.JobDAO;
import com.placement.system.dao.CompanyDAO;

public class CreateOfferPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    // Color scheme matching their design
    private static final Color MAIN_BG = new Color(0xCF, 0xCF, 0xCF);      // #CFCFCF
    private static final Color ACCENT_DARK = new Color(0x54, 0x54, 0x54);  // #545454
    private static final Color ACCENT_BTN = new Color(0x7D, 0x7D, 0x7D);   // #7D7D7D
    
    // Date formatters
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DB_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Current company
    private Company currentCompany;
    
    // Top fields
    private JTextField jt_title = new JTextField(20);
    private JTextField jt_dept = new JTextField(20);
    private JTextField jt_location = new JTextField(20);
    private JComboBox<String> jc_type = new JComboBox<>(new String[]{"Full-time", "Internship", "Contract"});
    private JTextField jt_salary = new JTextField(20);
    private JSpinner js_positions = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private JTextField jt_deadline = new JTextField(20); // dd/mm/yyyy
    private JTextField jt_cgpa = new JTextField(20);
    
    // Text areas
    private JTextArea ta_desc = new JTextArea(3, 30);
    private JTextArea ta_qual = new JTextArea(3, 30);
    
    // Skills
    private JTextField jt_skill = new JTextField(20);
    private JButton btnAddSkill = new JButton("Add");
    private final List<String> skillsList = new ArrayList<>();
    
    private Runnable onOfferCreated;
    
    public CreateOfferPanel(Runnable onOfferCreated) {
        this.onOfferCreated = onOfferCreated;
        loadCurrentCompany();
        setupPanel();
    }
    
    /**
     * Load current company from session/database
     */
    private void loadCurrentCompany() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            if (user instanceof Company) {
                this.currentCompany = (Company) user;
            } else {
                // Load from DAO if not Company instance
                this.currentCompany = CompanyDAO.getInstance().getCompany(user.getId());
                if (this.currentCompany != null) {
                    SessionManager.getInstance().setCurrentUser(this.currentCompany);
                }
            }
        }
    }
    
    private void setupPanel() {
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(MAIN_BG);
        
        // ---------- HEADER ----------
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(MAIN_BG);
        
        JLabel h1 = new JLabel("Create Job Offer");
        h1.setForeground(ACCENT_DARK);
        h1.setFont(h1.getFont().deriveFont(Font.BOLD, 22f));
        
        JLabel h2 = new JLabel("Fill in the details to post a new job vacancy");
        h2.setForeground(new Color(100, 100, 100));
        
        header.add(h1);
        header.add(h2);
        
        add(header, BorderLayout.NORTH);
        
        // ---------- MAIN CARD ----------
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Card Header
        JLabel jobDetails = new JLabel("Job Details");
        jobDetails.setForeground(ACCENT_DARK);
        jobDetails.setFont(jobDetails.getFont().deriveFont(Font.BOLD, 14f));
        card.add(jobDetails, BorderLayout.NORTH);
        
        // ---------- FORM GRID (2 columns) ----------
        JPanel formGrid = new JPanel(new GridLayout(0, 2, 15, 10));
        formGrid.setBackground(Color.WHITE);
        
        // Row 1
        formGrid.add(new JLabel("Job Title *"));
        formGrid.add(new JLabel("Department"));
        formGrid.add(jt_title);
        formGrid.add(jt_dept);
        
        // Row 2
        formGrid.add(new JLabel("Location *"));
        formGrid.add(new JLabel("Employment Type *"));
        formGrid.add(jt_location);
        formGrid.add(jc_type);
        
        // Row 3
        formGrid.add(new JLabel("Salary Range"));
        formGrid.add(new JLabel("Number of Positions *"));
        formGrid.add(jt_salary);
        formGrid.add(js_positions);
        
        // Row 4
        formGrid.add(new JLabel("Application Deadline (dd/mm/yyyy) *"));
        formGrid.add(new JLabel("Minimum CGPA *"));
        formGrid.add(jt_deadline);
        formGrid.add(jt_cgpa);
        
        // ---------- DESCRIPTION PANEL ----------
        ta_desc.setLineWrap(true);
        ta_desc.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(ta_desc);
        descScroll.setPreferredSize(new Dimension(400, 70));
        
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBackground(Color.WHITE);
        descPanel.add(new JLabel("Job Description *"), BorderLayout.NORTH);
        descPanel.add(descScroll, BorderLayout.CENTER);
        
        // ---------- QUALIFICATIONS PANEL ----------
        ta_qual.setLineWrap(true);
        ta_qual.setWrapStyleWord(true);
        JScrollPane qualScroll = new JScrollPane(ta_qual);
        qualScroll.setPreferredSize(new Dimension(400, 70));
        
        JPanel qualPanel = new JPanel(new BorderLayout());
        qualPanel.setBackground(Color.WHITE);
        qualPanel.add(new JLabel("Qualifications Needed"), BorderLayout.NORTH);
        qualPanel.add(qualScroll, BorderLayout.CENTER);
        
        // ---------- SKILLS PANEL ----------
        JPanel skillsRow = new JPanel(new BorderLayout(10, 0));
        skillsRow.setBackground(Color.WHITE);
        styleButton(btnAddSkill);
        skillsRow.add(jt_skill, BorderLayout.CENTER);
        skillsRow.add(btnAddSkill, BorderLayout.EAST);
        
        JPanel skillsPanel = new JPanel(new BorderLayout());
        skillsPanel.setBackground(Color.WHITE);
        skillsPanel.add(new JLabel("Required Skills"), BorderLayout.NORTH);
        skillsPanel.add(skillsRow, BorderLayout.CENTER);
        
        // Add a small panel to display added skills
        JPanel skillsDisplayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        skillsDisplayPanel.setBackground(Color.WHITE);
        skillsDisplayPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        skillsPanel.add(skillsDisplayPanel, BorderLayout.SOUTH);
        
        // Note about required fields
        JLabel noteLabel = new JLabel("* Required fields");
        noteLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        noteLabel.setForeground(new Color(100, 100, 100));
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // ---------- BODY LAYOUT ----------
        JPanel body = new JPanel();
        body.setBackground(Color.WHITE);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        formGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        descPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        qualPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        skillsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        body.add(formGrid);
        body.add(Box.createVerticalStrut(15));
        body.add(descPanel);
        body.add(Box.createVerticalStrut(15));
        body.add(qualPanel);
        body.add(Box.createVerticalStrut(15));
        body.add(skillsPanel);
        body.add(Box.createVerticalStrut(10));
        body.add(noteLabel);

        JScrollPane bodyScroll = new JScrollPane(body);
        bodyScroll.setBorder(BorderFactory.createEmptyBorder());
        bodyScroll.getVerticalScrollBar().setUnitIncrement(16);
        bodyScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        card.add(bodyScroll, BorderLayout.CENTER);
        
        // ---------- BOTTOM BUTTONS ----------
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomPanel.setBackground(Color.WHITE);
        
        JButton btnClear = new JButton("Clear Form");
        styleSecondaryButton(btnClear);
        btnClear.addActionListener(e -> clearForm());
        
        JButton btnCreate = new JButton("Create Job Offer");
        styleButton(btnCreate);
        
        bottomPanel.add(btnClear);
        bottomPanel.add(btnCreate);
        
        card.add(bottomPanel, BorderLayout.SOUTH);
        
        add(card, BorderLayout.CENTER);
        
        // ---------- ACTION LISTENERS ----------
        btnAddSkill.addActionListener(e -> {
            String skill = jt_skill.getText().trim();
            if (skill.isEmpty()) return;
            
            // Check for duplicates
            for (String s : skillsList) {
                if (s.equalsIgnoreCase(skill)) {
                    JOptionPane.showMessageDialog(this, 
                        "Skill already added!", 
                        "Duplicate Skill", 
                        JOptionPane.WARNING_MESSAGE);
                    jt_skill.setText("");
                    return;
                }
            }
            
            skillsList.add(skill);
            jt_skill.setText("");
            
            // Update the skills display
            updateSkillsDisplay(skillsDisplayPanel);
        });
        
        btnCreate.addActionListener(e -> createJobOffer());
    }
    
    /**
     * Create a new job offer and save to database
     */
    private void createJobOffer() {
        // Validate current company
        if (currentCompany == null) {
            loadCurrentCompany();
            if (currentCompany == null) {
                JOptionPane.showMessageDialog(this, 
                    "Company not found. Please log in again.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Get form values
        String title = jt_title.getText().trim();
        String dept = jt_dept.getText().trim();
        String location = jt_location.getText().trim();
        String type = jc_type.getSelectedItem().toString();
        String salary = jt_salary.getText().trim();
        int positions = (Integer) js_positions.getValue();
        String deadlineStr = jt_deadline.getText().trim();
        String cgpaStr = jt_cgpa.getText().trim();
        String description = ta_desc.getText().trim();
        String qualifications = ta_qual.getText().trim();
        
        // Validate required fields
        if (title.isEmpty()) {
            showError("Job Title is required.");
            return;
        }
        
        if (location.isEmpty()) {
            showError("Location is required.");
            return;
        }
        
        if (deadlineStr.isEmpty()) {
            showError("Application Deadline is required.");
            return;
        }
        
        if (cgpaStr.isEmpty()) {
            showError("Minimum CGPA is required.");
            return;
        }
        
        if (description.isEmpty()) {
            showError("Job Description is required.");
            return;
        }
        
        // Parse and validate deadline
        LocalDate deadline;
        try {
            deadline = LocalDate.parse(deadlineStr, INPUT_DATE_FORMAT);
            if (deadline.isBefore(LocalDate.now())) {
                showError("Deadline cannot be in the past.");
                return;
            }
        } catch (DateTimeParseException e) {
            showError("Invalid date format. Please use dd/mm/yyyy");
            return;
        }
        
        // Parse and validate CGPA
        double minCgpa;
        try {
            minCgpa = Double.parseDouble(cgpaStr);
            if (minCgpa < 0 || minCgpa > 10) {
                showError("CGPA must be between 0 and 10.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Invalid CGPA. Please enter a number.");
            return;
        }
        
        // Create Job object
        Job job = new Job();
        job.setCompanyId(currentCompany.getId());
        job.setJobTitle(title);
        job.setDepartment(dept.isEmpty() ? null : dept);
        job.setLocation(location);
        job.setEmploymentType(type);
        job.setSalaryRange(salary.isEmpty() ? null : salary);
        job.setVacancies(positions);
        job.setApplicationDeadline(deadline);
        job.setMinCgpa(minCgpa);
        job.setDescription(description);
        // Note: Skills and qualifications are stored but not in the Job table yet
        // You might want to add a skills column to jobs table or create a separate table
        
        // Save to database
        JobDAO jobDAO = JobDAO.getInstance();
        boolean success = jobDAO.createJob(job);
        
        if (success) {
            // Build success message
            StringBuilder message = new StringBuilder();
            message.append("Job Offer Created Successfully!\n\n");
            message.append("Job ID: ").append(job.getJobId()).append("\n");
            message.append("Title: ").append(title).append("\n");
            message.append("Company: ").append(currentCompany.getCompanyName()).append("\n");
            message.append("Location: ").append(location).append("\n");
            message.append("Type: ").append(type).append("\n");
            if (!salary.isEmpty()) {
                message.append("Salary: ").append(salary).append("\n");
            }
            message.append("Positions: ").append(positions).append("\n");
            message.append("Deadline: ").append(deadline.format(INPUT_DATE_FORMAT)).append("\n");
            message.append("Minimum CGPA: ").append(minCgpa).append("\n");
            if (!skillsList.isEmpty()) {
                message.append("Skills: ").append(String.join(", ", skillsList)).append("\n");
            }
            
            JOptionPane.showMessageDialog(this, 
                message.toString(), 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Clear form after successful creation
            clearForm();
            
            // Navigate back to My Offers panel
            if (onOfferCreated != null) {
                onOfferCreated.run();
            }
        } else {
            showError("Failed to create job offer. Please try again.");
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "Validation Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    private void updateSkillsDisplay(JPanel displayPanel) {
        displayPanel.removeAll();
        if (skillsList.isEmpty()) {
            displayPanel.add(new JLabel("No skills added yet."));
        } else {
            for (String skill : skillsList) {
                JLabel skillLabel = new JLabel(skill);
                skillLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_DARK, 1),
                    BorderFactory.createEmptyBorder(2, 8, 2, 8)
                ));
                skillLabel.setBackground(new Color(240, 240, 240));
                skillLabel.setOpaque(true);
                displayPanel.add(skillLabel);
                displayPanel.add(Box.createHorizontalStrut(5));
            }
        }
        displayPanel.revalidate();
        displayPanel.repaint();
    }
    
    /**
     * Clear all form fields
     */
    public void clearForm() {
        jt_title.setText("");
        jt_dept.setText("");
        jt_location.setText("");
        jc_type.setSelectedIndex(0);
        jt_salary.setText("");
        js_positions.setValue(1);
        jt_deadline.setText("");
        jt_cgpa.setText("");
        ta_desc.setText("");
        ta_qual.setText("");
        jt_skill.setText("");
        skillsList.clear();
        
        // Refresh the component to show cleared fields
        revalidate();
        repaint();
    }
    
    /**
     * Style for primary buttons (Create, Add)
     */
    private void styleButton(JButton b) {
        b.setBackground(ACCENT_BTN);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_DARK, 1),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    
    /**
     * Style for secondary buttons (Clear)
     */
    private void styleSecondaryButton(JButton b) {
        b.setBackground(Color.WHITE);
        b.setForeground(ACCENT_DARK);
        b.setFocusPainted(false);
        b.setBorderPainted(true);
        b.setOpaque(true);
        b.setFont(new Font("SansSerif", Font.PLAIN, 12));
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_BTN, 1),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}