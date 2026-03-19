// com.placement.system.views/StudentProfilePanel.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

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
    private final JTextArea txtBio = new JTextArea(4, 20);
    
    // Academic Information Fields
    private final JTextField txtCourse = new JTextField();
    private final JTextField txtBranch = new JTextField();
    private final JTextField txtCgpa = new JTextField();
    private final JTextField txtYear = new JTextField();
    private final JTextField txtCvPath = new JTextField();
    
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
        addTextAreaRow(personalCard, "Student Bio:", txtBio, 4);
        page.add(wrapInPadding(personalCard));
        page.add(Box.createVerticalStrut(10));
        
        // === Academic Details Section ===
        page.add(createSectionBar("Academic Details"));
        page.add(Box.createVerticalStrut(5));
        
        JPanel academicCard = createTitledBlock("Academic Information");
        academicCard.setLayout(new GridBagLayout());
        addFormRow(academicCard, "Course:", txtCourse, 0);
        addFormRow(academicCard, "Branch:", txtBranch, 1);
        addFormRow(academicCard, "CGPA:", txtCgpa, 2);
        addFormRow(academicCard, "Year of Study:", txtYear, 3);
        addFormRow(academicCard, "Curriculum Vitae:", txtCvPath, 4);
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
        
        JScrollPane scrollPane = new JScrollPane(page);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(MAIN_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
        
        setEditing(false);
    }
    
    private JPanel createSectionBar(String text) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(ACCENT);
        bar.setBorder(new EmptyBorder(4, 8, 4, 8));
        bar.add(createHeaderLabel(text), BorderLayout.WEST);
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        return bar;
    }
    
    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        return label;
    }
    
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
    
    private JPanel wrapInPadding(JComponent component) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(MAIN_BG);
        wrap.setBorder(new EmptyBorder(8, 8, 8, 8));
        wrap.add(component, BorderLayout.CENTER);
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        return wrap;
    }
    
    private void addFormRow(JPanel panel, String labelText, JComponent field, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(new Color(60, 60, 60));
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        panel.add(field, gbc);
        
        styleField(field);
    }
    
    private void addTextAreaRow(JPanel panel, String labelText, JTextArea textArea, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.BOTH;
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(new Color(60, 60, 60));
        panel.add(label, gbc);
        
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(6, 6, 6, 6)
        ));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 90));
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        gbc.weighty = 1.0;
        panel.add(scrollPane, gbc);
    }
    
    private void styleField(JComponent field) {
        field.setPreferredSize(new Dimension(300, 28));
        field.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        if (field instanceof JTextField) {
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(4, 6, 4, 6)
            ));
        } else if (field instanceof JPasswordField) {
            ((JPasswordField) field).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(4, 6, 4, 6)
            ));
        }
    }
    
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
        this.workingCopy = student;
        
        // Personal Information
        txtFullName.setText(student.getFullName());
        txtEmail.setText(student.getEmail());
        txtUsername.setText(student.getUsername());
        txtPhone.setText(student.getPhone() != null ? student.getPhone() : "");
        
        // These two lines require matching fields in Student model
        txtBio.setText(student.getBio() != null ? student.getBio() : "");
        
        // Academic Details
        txtCourse.setText(student.getCourse());
        txtBranch.setText(student.getBranch());
        txtCgpa.setText(String.valueOf(student.getCgpa()));
        txtYear.setText(student.getYear());
        txtCvPath.setText(student.getCvPath() != null ? student.getCvPath() : "");
        
        setEditing(false);
        lblStatus.setText(" ");
        
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
        if (txtEmail.getText().trim().isEmpty()) {
            showError("Email cannot be empty");
            return;
        }
        
        workingCopy.setEmail(txtEmail.getText().trim());
        workingCopy.setPhone(txtPhone.getText().trim());
        
        // These two lines require matching fields in Student model
        workingCopy.setBio(txtBio.getText().trim());
        workingCopy.setCvPath(txtCvPath.getText().trim());
        
        // Here you would save to database
        
        showSuccess("Profile updated successfully!");
        setEditing(false);
        btnEdit.setVisible(true);
        btnSave.setVisible(false);
        btnCancel.setVisible(false);
    }
    
    private void cancelEdit() {
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
        
        if (!workingCopy.getPassword().equals(current)) {
            showError("Current password is incorrect");
            return;
        }
        
        workingCopy.setPassword(newPwd);
        
        curPass.setText("");
        newPass.setText("");
        confPass.setText("");
        
        showSuccess("Password changed successfully!");
    }
    
    private void showError(String message) {
        lblStatus.setForeground(new Color(190, 40, 40));
        lblStatus.setText(message);
    }
    
    private void showSuccess(String message) {
        lblStatus.setForeground(new Color(0, 128, 0));
        lblStatus.setText(message);
    }
    
    private void setEditing(boolean enabled) {
        editing = enabled;
        
        txtFullName.setEditable(false);
        txtEmail.setEditable(enabled);
        txtPhone.setEditable(enabled);
        txtBio.setEditable(enabled);
        
        txtCourse.setEditable(false);
        txtBranch.setEditable(false);
        txtCgpa.setEditable(false);
        txtYear.setEditable(false);
        txtCvPath.setEditable(enabled);
        
        txtUsername.setEditable(false);
    }
}