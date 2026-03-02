// com.placement.system.views/StudentProfilePanel.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.placement.system.models.Student;
import com.placement.system.models.User;
import com.placement.system.utils.SessionManager;

public class StudentProfilePanel extends JPanel {
    
    // ===== UI THEME (matching the team's implementation) =====
    private static final Color MAIN_BG = new Color(0xCF, 0xCF, 0xCF);      // #CFCFCF
    private static final Color ACCENT = new Color(0x54, 0x54, 0x54);       // #545454
    private static final Color BTN = new Color(0x7D, 0x7D, 0x7D);          // #7D7D7D
    private static final Color CARD_BG = new Color(0xE6, 0xE3, 0xD6);      // beige-grey
    private static final Color BORDER = new Color(0x9A, 0x9A, 0x9A);
    
    private Student workingCopy;
    private boolean editing = false;
    
    // Personal Information Fields
    private final JTextField txtFullName = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JTextField txtUsername = new JTextField();
    private final JTextField txtPhone = new JTextField();
    
    // Academic Details Fields
    private final JComboBox<String> cmbCourse = new JComboBox<>(new String[]{
    	    "BSc (Hons) Computer Science",
    	    "BSc (Hons) Software Engineering",
    	    "BSc (Hons) Information Technology",
    	    "BSc (Hons) Business Informatics",
    	    "BSc (Hons) Data Science",
    	    "BSc (Hons) Cybersecurity",
    	    "BSc (Hons) Mathematics",
    	    "BSc (Hons) Physics",
    	    "BSc (Hons) Chemistry",
    	    "BSc (Hons) Biology",
    	    "BSc (Hons) Marine Science",
    	    "BEng (Hons) Chemical Engineering",
    	    "BEng (Hons) Civil Engineering",
    	    "BEng (Hons) Electrical & Electronic Engineering",
    	    "BEng (Hons) Mechanical Engineering",
    	    "BEng (Hons) Mechatronics",
    	    "LLB (Hons) Law",
    	    "BSc (Hons) Accounting",
    	    "BSc (Hons) Finance",
    	    "BSc (Hons) Management",
    	    "BSc (Hons) Human Resource Management",
    	    "BSc (Hons) Marketing",
    	    "BSc (Hons) Economics",
    	    "BA (Hons) English Studies",
    	    "BA (Hons) French Studies",
    	    "BA (Hons) History & Political Science",
    	    "BA (Hons) Sociology",
    	    "BA (Hons) Social Work",
    	    "MBBS Medicine",
    	    "BSc (Hons) Nursing",
    	    "BSc (Hons) Agriculture",
    	    "BSc (Hons) Food Science",
    	    "Other"
    	});

    	private final JComboBox<String> cmbBranch = new JComboBox<>(new String[]{
    	    "Faculty of Engineering",
    	    "Faculty of Information, Communication & Digital Technologies (FoICDT)",
    	    "Faculty of Science",
    	    "Faculty of Law & Management",
    	    "Faculty of Social Studies & Humanities",
    	    "Faculty of Agriculture",
    	    "Faculty of Medicine & Health Sciences",
    	    "Faculty of Ocean Studies",
    	    "School of Business",
    	    "Department of Computer Science & Engineering",
    	    "Department of Software Engineering",
    	    "Department of Electrical & Electronic Engineering",
    	    "Department of Civil Engineering",
    	    "Department of Mechanical Engineering",
    	    "Department of Finance & Accounting",
    	    "Department of Management",
    	    "Department of Law",
    	    "Department of Economics & Statistics",
    	    "Department of English Studies",
    	    "Department of French Studies",
    	    "Department of Mathematics",
    	    "Department of Physics",
    	    "Department of Chemistry",
    	    "Department of Biosciences",
    	    "Department of Health Sciences",
    	    "Centre for Information Technology & Systems (CITS)",
    	    "Other"
    	});
    private final JComboBox<String> cmbSection = new JComboBox<>(new String[]{"A1", "B2", "C3", "D4"});
    private final JTextField txtCgpa = new JTextField();
    private final JTextField txtYear = new JTextField();
    
    // Password Change Fields
    private final JPasswordField curPass = new JPasswordField();
    private final JPasswordField newPass = new JPasswordField();
    private final JPasswordField confPass = new JPasswordField();
    
    // Buttons
    private final JButton btnEdit = new JButton("Edit Profile");
    private final JButton btnSave = new JButton("Save");
    private final JButton btnCancel = new JButton("Cancel");
    private final JButton btnChangePassword = new JButton("Update Password");
    private final JLabel lblStatus = new JLabel(" ");
    
    public StudentProfilePanel() {
        initializePanel();
        loadCurrentStudent();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(MAIN_BG);
        
        // Main container with BoxLayout for vertical stacking (like team's implementation)
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(MAIN_BG);
        
        // === Personal Information Section ===
        page.add(createSectionBar("Personal Information"));
        page.add(Box.createVerticalStrut(5));
        
        JPanel personalCard = createTitledBlock("Personal Details");
        personalCard.setLayout(new GridBagLayout());
        addFormRow(personalCard, "Full Name:", txtFullName, 0);
        addFormRow(personalCard, "Email:", txtEmail, 1);
        addFormRow(personalCard, "Username:", txtUsername, 2);
        addFormRow(personalCard, "Phone:", txtPhone, 3);
        page.add(wrapInPadding(personalCard));
        page.add(Box.createVerticalStrut(10));
        
        // === Academic Details Section ===
        page.add(createSectionBar("Academic Details"));
        page.add(Box.createVerticalStrut(5));
        
        JPanel academicCard = createTitledBlock("Academic Information");
        academicCard.setLayout(new GridBagLayout());
        addFormRow(academicCard, "Course:", cmbCourse, 0);
        addFormRow(academicCard, "Branch:", cmbBranch, 1);
        addFormRow(academicCard, "Section:", cmbSection, 2);
        addFormRow(academicCard, "CGPA:", txtCgpa, 3);
        addFormRow(academicCard, "Year of Study:", txtYear, 4);
        page.add(wrapInPadding(academicCard));
        page.add(Box.createVerticalStrut(10));
        
        // === Edit/Save Controls ===
        JPanel editRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        editRow.setBackground(CARD_BG);
        
        styleNavButton(btnEdit);
        styleNavButton(btnSave);
        styleNavButton(btnCancel);
        
        btnEdit.addActionListener(e -> toggleEdit());
        btnSave.addActionListener(e -> saveChanges());
        btnCancel.addActionListener(e -> cancelEdit());
        
        editRow.add(btnEdit);
        editRow.add(btnSave);
        editRow.add(btnCancel);
        
        JPanel editWrap = new JPanel(new BorderLayout());
        editWrap.setBackground(MAIN_BG);
        editWrap.setBorder(new EmptyBorder(8, 8, 8, 8));
        editWrap.add(editRow, BorderLayout.CENTER);
        editWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        page.add(editWrap);
        
        // Status label
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(0, 128, 0));
        lblStatus.setBorder(new EmptyBorder(0, 8, 8, 8));
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        page.add(lblStatus);
        
        page.add(Box.createVerticalStrut(15));
        
        // === Change Password Section ===
        page.add(createSectionBar("Change Password"));
        page.add(Box.createVerticalStrut(5));
        
        JPanel passwordCard = createTitledBlock("Password");
        passwordCard.setLayout(new GridBagLayout());
        addFormRow(passwordCard, "Current Password:", curPass, 0);
        addFormRow(passwordCard, "New Password:", newPass, 1);
        addFormRow(passwordCard, "Confirm Password:", confPass, 2);
        page.add(wrapInPadding(passwordCard));
        page.add(Box.createVerticalStrut(10));
        
        // Password action button
        JPanel passwordActionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        passwordActionRow.setBackground(CARD_BG);
        styleButton(btnChangePassword, BTN);
        passwordActionRow.add(btnChangePassword);
        
        JPanel passwordWrap = new JPanel(new BorderLayout());
        passwordWrap.setBackground(MAIN_BG);
        passwordWrap.setBorder(new EmptyBorder(0, 8, 8, 8));
        passwordWrap.add(passwordActionRow, BorderLayout.CENTER);
        passwordWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        page.add(passwordWrap);
        
        btnChangePassword.addActionListener(e -> changePassword());
        
        // Wrap everything in a scroll pane (like team's implementation)
        JScrollPane scrollPane = new JScrollPane(page);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(MAIN_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
        
        setEditing(false);
    }
    
    /**
     * Creates a section bar with accent color (exactly like team's implementation)
     */
    private JPanel createSectionBar(String text) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(ACCENT);
        bar.setBorder(new EmptyBorder(4, 8, 4, 8));
        bar.add(createHeaderLabel(text), BorderLayout.WEST);
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        return bar;
    }
    
    /**
     * Creates a header label with white text (exactly like team's implementation)
     */
    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        return label;
    }
    
    /**
     * Creates a titled block with border (like team's titledBlock method)
     */
    private JPanel createTitledBlock(String title) {
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
     * Wraps a component with padding (like team's wrapCard method)
     */
    private JPanel wrapInPadding(JComponent component) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(MAIN_BG);
        wrap.setBorder(new EmptyBorder(8, 8, 8, 8));
        wrap.add(component, BorderLayout.CENTER);
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        return wrap;
    }
    
    /**
     * Adds a form row with label and field (matching team's addFormRows pattern)
     */
    private void addFormRow(JPanel panel, String labelText, JComponent field, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(new Color(60, 60, 60));
        panel.add(label, gbc);
        
        // Field
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        styleField(field);
        panel.add(field, gbc);
    }
    
    /**
     * Style form fields (matching team's styleField method)
     */
    private void styleField(JComponent field) {
        field.setPreferredSize(new Dimension(300, 28));
        field.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        if (field instanceof JTextField) {
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(4, 6, 4, 6)
            ));
        } else if (field instanceof JComboBox) {
            ((JComboBox<?>) field).setBackground(Color.WHITE);
            ((JComboBox<?>) field).setBorder(BorderFactory.createLineBorder(BORDER));
            ((JComboBox<?>) field).setMaximumSize(new Dimension(300, 28));
        } else if (field instanceof JPasswordField) {
            ((JPasswordField) field).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(4, 6, 4, 6)
            ));
        }
    }
    
    /**
     * Style for navigation buttons (like team's navButton)
     */
    private void styleNavButton(JButton button) {
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBackground(MAIN_BG);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(6, 12, 6, 12)
        ));
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
    }
    
    /**
     * Style for primary action buttons (like team's greyButton)
     */
    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(7, 14, 7, 14)
        ));
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
    }
    
    private void loadCurrentStudent() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser instanceof Student) {
            loadStudent((Student) currentUser);
        } else {
            JOptionPane.showMessageDialog(this,
                "Error: Current user is not a student",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void loadStudent(Student student) {
        this.workingCopy = student; // Keep reference to actual student
        
        // Personal Information
        txtFullName.setText(student.getFullName());
        txtEmail.setText(student.getEmail());
        txtUsername.setText(student.getUsername());
        txtPhone.setText(student.getPhone() != null ? student.getPhone() : "");
        
        // Academic Details
        cmbCourse.setSelectedItem(student.getCourse());
        cmbBranch.setSelectedItem(student.getBranch());
        cmbSection.setSelectedItem(student.getSection());
        txtCgpa.setText(String.valueOf(student.getCgpa()));
        txtYear.setText(student.getYear());
        
        setEditing(false);
        lblStatus.setText(" ");
        
        // Clear password fields
        curPass.setText("");
        newPass.setText("");
        confPass.setText("");
    }
    
    private void toggleEdit() {
        setEditing(true);
        btnEdit.setVisible(false);
        btnSave.setVisible(true);
        btnCancel.setVisible(true);
    }
    
    private void saveChanges() {
        // Validate inputs
        if (txtFullName.getText().trim().isEmpty()) {
            showError("Full name cannot be empty");
            return;
        }
        
        if (txtEmail.getText().trim().isEmpty()) {
            showError("Email cannot be empty");
            return;
        }
        
        double cgpa;
        try {
            cgpa = Double.parseDouble(txtCgpa.getText().trim());
            if (cgpa < 0 || cgpa > 10) {
                showError("CGPA must be between 0 and 10");
                return;
            }
        } catch (NumberFormatException ex) {
            showError("CGPA must be a valid number");
            return;
        }
        
        // Update student object
        workingCopy.setFullName(txtFullName.getText().trim());
        workingCopy.setEmail(txtEmail.getText().trim());
        workingCopy.setCourse((String) cmbCourse.getSelectedItem());
        workingCopy.setBranch((String) cmbBranch.getSelectedItem());
        workingCopy.setSection((String) cmbSection.getSelectedItem());
        workingCopy.setCgpa(cgpa);
        workingCopy.setYear(txtYear.getText().trim());
        workingCopy.setPhone(txtPhone.getText().trim());
        
        // Here you would save to database
        // MockStudentService.updateStudent(workingCopy);
        
        showSuccess("Profile updated successfully!");
        setEditing(false);
        btnEdit.setVisible(true);
        btnSave.setVisible(false);
        btnCancel.setVisible(false);
    }
    
    private void cancelEdit() {
        // Reload original data
        loadStudent(workingCopy);
        setEditing(false);
        btnEdit.setVisible(true);
        btnSave.setVisible(false);
        btnCancel.setVisible(false);
        lblStatus.setText(" ");
    }
    
    private void changePassword() {
        String current = new String(curPass.getPassword());
        String newPwd = new String(newPass.getPassword());
        String confirm = new String(confPass.getPassword());
        
        if (current.isEmpty() || newPwd.isEmpty() || confirm.isEmpty()) {
            showError("All password fields are required");
            return;
        }
        
        if (!newPwd.equals(confirm)) {
            showError("New passwords do not match");
            return;
        }
        
        if (newPwd.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }
        
        // Verify current password
        if (!workingCopy.getPassword().equals(current)) {
            showError("Current password is incorrect");
            return;
        }
        
        // Update password
        workingCopy.setPassword(newPwd);
        
        // Clear password fields
        curPass.setText("");
        newPass.setText("");
        confPass.setText("");
        
        showSuccess("Password changed successfully!");
    }
    
    private void showError(String message) {
        lblStatus.setForeground(new Color(190, 40, 40)); // Red
        lblStatus.setText(message);
    }
    
    private void showSuccess(String message) {
        lblStatus.setForeground(new Color(0, 128, 0)); // Green
        lblStatus.setText(message);
    }
    
    private void setEditing(boolean enabled) {
        editing = enabled;
        
        // Enable/disable fields
        txtFullName.setEditable(enabled);
        txtEmail.setEditable(enabled);
        txtPhone.setEditable(enabled);
        
        cmbCourse.setEnabled(enabled);
        cmbBranch.setEnabled(enabled);
        cmbSection.setEnabled(enabled);
        txtCgpa.setEditable(enabled);
        txtYear.setEditable(enabled);
        
        // Username should never be editable
        txtUsername.setEditable(false);
    }
}