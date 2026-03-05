// com.placement.system.views/RegistrationPanel.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.placement.system.models.Company;
import com.placement.system.utils.CompanyDataStore;
import com.placement.system.models.Student;
import com.placement.system.utils.StudentDataStore;

public class RegistrationPanel extends JPanel {
    // Color scheme matching the dashboard
    private static final Color MAIN_BG = new Color(0xCF, 0xCF, 0xCF);      // #CFCFCF
    private static final Color ACCENT = new Color(0x54, 0x54, 0x54);       // #545454
    private static final Color BTN = new Color(0x7D, 0x7D, 0x7D);          // #7D7D7D
    private static final Color CARD_BG = new Color(0xE6, 0xE3, 0xD6);      // beige-grey
    private static final Color BORDER = new Color(0x9A, 0x9A, 0x9A);       // #9A9A9A
    
    private JRadioButton rbStudent = new JRadioButton("STUDENT");
    private JRadioButton rbCompany = new JRadioButton("COMPANY");
    private ButtonGroup roleGroup = new ButtonGroup();
    
    private JLabel title = new JLabel("Student Registration");
    private JLabel sub = new JLabel("Provide your details to create an account.");
    private JLabel lblError = new JLabel(" ");
    
    private CardLayout regCards = new CardLayout();
    private JPanel regRoot = new JPanel(regCards);
    
    // Student fields
    private JTextField txtStudentId = new JTextField();
    private JTextField txtFullName = new JTextField();
    private JTextField txtEmail = new JTextField();
    private JTextField txtUsername = new JTextField();
    private JPasswordField txtPass = new JPasswordField();
    private JPasswordField txtConfirm = new JPasswordField();
    private JTextField txtFaculty = new JTextField();
    private JTextField txtCourse = new JTextField();
    private JTextField txtYear = new JTextField();
    private JTextField txtPhone = new JTextField();
    
    // Company fields
    private JTextField txtCompanyName = new JTextField();
    private JTextField txtCompanyEmail = new JTextField();
    private JTextField txtContactPerson = new JTextField();
    private JTextField txtCompanyPhone = new JTextField();
    private JTextField txtCompanyWebsite = new JTextField();
    private JTextField txtCompanyAddress = new JTextField();
    private JTextArea txtCompanyDescription = new JTextArea(3, 20);
    private JPasswordField txtCompanyPass = new JPasswordField();
    private JPasswordField txtCompanyConfirm = new JPasswordField();
    
    private RegistrationListener registrationListener;
    
    public interface RegistrationListener {
        void onRegistrationComplete();
        void onBackToLogin();
    }
    
    public RegistrationPanel(RegistrationListener listener) {
        this.registrationListener = listener;
        
        setLayout(new BorderLayout());
        setBackground(MAIN_BG);
        
        // Top header bar matching dashboard style
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ACCENT);
        header.setBorder(new EmptyBorder(8, 16, 8, 16));
        
        JLabel titleLabel = new JLabel("Student Placement System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        
        // subtitle removed per user request
        
        JPanel leftHeader = new JPanel(new BorderLayout());
        leftHeader.setBackground(ACCENT);
        leftHeader.add(titleLabel, BorderLayout.WEST);
        
        JButton btnBackToLogin = new JButton("Back to Login");
        btnBackToLogin.setFocusPainted(false);
        btnBackToLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBackToLogin.setBackground(BTN);
        btnBackToLogin.setForeground(Color.WHITE);
        btnBackToLogin.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnBackToLogin.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btnBackToLogin.addActionListener(e -> {
            if (registrationListener != null) {
                registrationListener.onBackToLogin();
            }
        });
        
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightHeader.setBackground(ACCENT);
        rightHeader.add(btnBackToLogin);
        
        header.add(leftHeader, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);
        
        // Center content area
        JPanel centerContent = new JPanel(new GridBagLayout());
        centerContent.setBackground(MAIN_BG);
        centerContent.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formCard = new JPanel(new BorderLayout(0, 12));
        formCard.setBackground(CARD_BG);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(20, 20, 20, 20)
        ));
        // enlarge card space per request
        formCard.setMaximumSize(new Dimension(700, 600));
        
        // Role row
        JPanel roleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        roleRow.setBackground(CARD_BG);
        
        setupRoleRadio(rbStudent);
        setupRoleRadio(rbCompany);
        roleGroup.add(rbStudent);
        roleGroup.add(rbCompany);
        
        rbStudent.setSelected(true);
        rbStudent.addActionListener(e -> switchRole());
        rbCompany.addActionListener(e -> switchRole());
        
        roleRow.add(label("Select Role:"));
        roleRow.add(rbStudent);
        roleRow.add(rbCompany);
        
        formCard.add(roleRow, BorderLayout.NORTH);
        
        // Forms
        regRoot.setBackground(CARD_BG);
        regRoot.add(buildStudentForm(), "STUDENT");
        regRoot.add(buildCompanyForm(), "COMPANY");
        
        JScrollPane sc = new JScrollPane(regRoot,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sc.setBorder(null);
        sc.getViewport().setBackground(CARD_BG);
        
        formCard.add(sc, BorderLayout.CENTER);
        
        // Bottom actions
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(CARD_BG);
        
        lblError.setForeground(new Color(190, 40, 40));
        lblError.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblError.setBorder(new EmptyBorder(6, 0, 0, 0));
        
        // make button identical to dashboard grey style and rename for clarity
        JButton btnRegister = new JButton("Register");
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // match login panel's primary style
        btnRegister.setBackground(ACCENT);
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));
        btnRegister.addActionListener(e -> doRegistration());
        
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actions.setBackground(CARD_BG);
        actions.add(btnRegister);
        
        bottom.add(lblError, BorderLayout.NORTH);
        bottom.add(actions, BorderLayout.WEST);
        
        formCard.add(bottom, BorderLayout.SOUTH);
        
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 0;
        gc.weightx = 0.6; gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;
        centerContent.add(formCard, gc);
        
        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(MAIN_BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        JLabel footerLabel = new JLabel("Student Placement System © 2025 - All Rights Reserved");
        footerLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        footerLabel.setForeground(new Color(100, 100, 100));
        footerLabel.setBorder(new EmptyBorder(8, 16, 8, 16));
        footer.add(footerLabel, BorderLayout.WEST);
        
        add(header, BorderLayout.NORTH);
        add(centerContent, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
        
        switchRole();
    }
    
    public void reset() {
        rbStudent.setSelected(true);
        
        txtStudentId.setText("");
        txtFullName.setText("");
        txtEmail.setText("");
        txtUsername.setText("");
        txtPass.setText("");
        txtConfirm.setText("");
        txtFaculty.setText("");
        txtCourse.setText("");
        txtYear.setText("");
        txtPhone.setText("");
        
        txtCompanyName.setText("");
        txtCompanyEmail.setText("");
        txtContactPerson.setText("");
        txtCompanyPhone.setText("");
        txtCompanyWebsite.setText("");
        txtCompanyAddress.setText("");
        txtCompanyDescription.setText("");
        txtCompanyPass.setText("");
        txtCompanyConfirm.setText("");
        
        lblError.setText(" ");
        switchRole();
    }
    
    private void switchRole() {
        lblError.setText(" ");
        if (rbStudent.isSelected()) {
            title.setText("Student Registration");
            regCards.show(regRoot, "STUDENT");
        } else {
            title.setText("Company Registration");
            regCards.show(regRoot, "COMPANY");
        }
    }
    
    private JPanel buildStudentForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(CARD_BG);
        
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        
        int r = 0;
        
        addField(p, gc, r++, "Student ID*", txtStudentId);
        addField(p, gc, r++, "Full Name*", txtFullName);
        addField(p, gc, r++, "Email*", txtEmail);
        addField(p, gc, r++, "Username*", txtUsername);
        addField(p, gc, r++, "Password*", txtPass);
        addField(p, gc, r++, "Confirm Password*", txtConfirm);
        addField(p, gc, r++, "Faculty*", txtFaculty);
        addField(p, gc, r++, "Course*", txtCourse);
        addField(p, gc, r++, "Year of Study*", txtYear);
        addField(p, gc, r++, "Phone", txtPhone);
        
        // Note about required fields
        gc.gridy = r;
        gc.gridx = 0;
        gc.gridwidth = 2;
        gc.weightx = 1;
        JLabel noteLabel = new JLabel("* Required fields");
        noteLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        noteLabel.setForeground(new Color(100, 100, 100));
        p.add(noteLabel, gc);
        r++;
        
        // spacer
        gc.gridx = 0; gc.gridy = r; gc.gridwidth = 2;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;
        p.add(Box.createVerticalGlue(), gc);
        
        return p;
    }
    
    private JPanel buildCompanyForm() {
    	JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(CARD_BG);
        
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        
        int r = 0;
        
        // Basic Account Info
        addField(p, gc, r++, "Company Name*", txtCompanyName);
        addField(p, gc, r++, "Contact Person*", txtContactPerson);
        addField(p, gc, r++, "Email*", txtCompanyEmail);
        addField(p, gc, r++, "Password*", txtCompanyPass);
        addField(p, gc, r++, "Confirm Password*", txtCompanyConfirm);
        
        // Contact Details
        addField(p, gc, r++, "Phone Number", txtCompanyPhone);
        addField(p, gc, r++, "Website", txtCompanyWebsite);
        addField(p, gc, r++, "Address*", txtCompanyAddress);
        
        
        // Description - using JTextArea with scroll
        gc.gridy = r;
        gc.gridx = 0;
        gc.gridwidth = 1;
        gc.weightx = 0.25;
        p.add(label("Company Description"), gc);
        
        gc.gridx = 1;
        gc.weightx = 0.75;
        JScrollPane descScroll = new JScrollPane(txtCompanyDescription);
        descScroll.setPreferredSize(new Dimension(250, 60));
        descScroll.setBorder(BorderFactory.createLineBorder(BORDER));
        p.add(descScroll, gc);
        r++;
        
        // Note about required fields
        gc.gridy = r;
        gc.gridx = 0;
        gc.gridwidth = 2;
        gc.weightx = 1;
        JLabel noteLabel = new JLabel("* Required fields");
        noteLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        noteLabel.setForeground(new Color(100, 100, 100));
        p.add(noteLabel, gc);
        r++;
        
        // spacer
        gc.gridx = 0; gc.gridy = r; gc.gridwidth = 2;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;
        p.add(Box.createVerticalGlue(), gc);
        
        return p;
    }
    
    private void addField(JPanel p, GridBagConstraints gc, int row, String lab, JComponent field) {
        gc.gridy = row;
        
        gc.gridx = 0;
        gc.gridwidth = 1;
        gc.weightx = 0.25;
        p.add(label(lab), gc);
        
        gc.gridx = 1;
        gc.weightx = 0.75;
        styleField(field);
        p.add(field, gc);
    }
    
    private void doRegistration() {
        if (rbStudent.isSelected()) {
            doStudentRegistration();
        } else {
            doCompanyRegistration();
        }
    }
    
    private void doStudentRegistration() {
        // Get values from form
        String studentId = txtStudentId.getText().trim();
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String username = txtUsername.getText().trim();
        String pass = new String(txtPass.getPassword());
        String conf = new String(txtConfirm.getPassword());
        String faculty = txtFaculty.getText().trim();
        String course = txtCourse.getText().trim();
        String yearStr = txtYear.getText().trim();
        String phoneStr = txtPhone.getText().trim();
        
        // Validate required fields
        if (studentId.isEmpty() || fullName.isEmpty() || email.isEmpty() || username.isEmpty() || pass.isEmpty() || conf.isEmpty()
                || faculty.isEmpty() || course.isEmpty() || yearStr.isEmpty()) {
            lblError.setText("Please fill all required fields.");
            return;
        }
        
        if (!pass.equals(conf)) {
            lblError.setText("Passwords do not match.");
            return;
        }
        
        if (pass.length() < 6) {
            lblError.setText("Password must be at least 6 characters long.");
            return;
        }
        
        // Basic email validation
        if (!email.contains("@") || !email.contains(".")) {
            lblError.setText("Please enter a valid email address.");
            return;
        }
        
        // Validate year is a number
        int year;
        try {
            year = Integer.parseInt(yearStr);
            if (year < 1 || year > 6) {
                lblError.setText("Year must be between 1 and 6.");
                return;
            }
        } catch (NumberFormatException ex) {
                lblError.setText("Year must be a valid number.");
                return;
        }
        
        // Check if student already exists in repository
        Student existingStudent = StudentDataStore.getInstance().getStudentByEmail(email);
        if (existingStudent != null) {
            lblError.setText("A student with this email already exists.");
            return;
        }
        
        // Check if student ID already exists
        existingStudent = StudentDataStore.getInstance().getStudentByStudentID(studentId);
        if (existingStudent != null) {
            lblError.setText("A student with this ID already exists.");
            return;
        }
        
     // Check if student username already exists
        existingStudent = StudentDataStore.getInstance().getStudentByUsername(username);
        if (existingStudent != null) {
            lblError.setText("A student with this username already exists.");
            return;
        }
        
        // Get next available ID from repository
        int nextId = StudentDataStore.getInstance().getAllStudents().size() + 1;
        
        // Create new Student object
        // Using constructor: Student(int id, String username, String password, String email,
        //                    String fullName, String studentId, String course, String branch,
        //                    String section, double cgpa, String year, String phone, String placementStatus)
        
        // Note: branch maps to faculty, section defaults to "A", cgpa starts at 0.0, phone empty
        Student newStudent = new Student(
            nextId,                          // id
            username,                        // username
            pass,                            // password
            email,                           // email
            fullName,                        // fullName
            studentId,                       // studentId (from form)
            course,                          // course
            faculty,                         // branch (maps to faculty)
            0.0,                             // cgpa (default 0.0 until updated)
            String.valueOf(year),             // year
            "",                              // phone (empty until updated)
            "Not Placed"                      // placementStatus (default)
        );
        
        // Save to repository
        StudentDataStore.getInstance().addStudent(newStudent);
        
        // Show success message
        String message = String.format(
            "Student registration successful!\n\n" +
            "Name: %s\n" +
            "Student ID: %s\n" +
            "Email: %s\n" +
            "Course: %s\n" +
            "Faculty: %s\n" +
            "Year: %d\n\n" +
            "You can now login with your credentials.",
            fullName, studentId, email, course, faculty, year
        );
        
        JOptionPane.showMessageDialog(this,
            message,
            "Registration Successful",
            JOptionPane.INFORMATION_MESSAGE);
        
        if (registrationListener != null) {
            registrationListener.onRegistrationComplete();
        }
    }
    
    private void doCompanyRegistration() {
        // Get values
        String name = txtCompanyName.getText().trim();
        String contactPerson = txtContactPerson.getText().trim();
        String email = txtCompanyEmail.getText().trim();
        String pass = new String(txtCompanyPass.getPassword());
        String conf = new String(txtCompanyConfirm.getPassword());
        String phone = txtCompanyPhone.getText().trim();
        String website = txtCompanyWebsite.getText().trim();
        String address = txtCompanyAddress.getText().trim();
        String description = txtCompanyDescription.getText().trim();
        
        // Validate required fields
        if (name.isEmpty() || contactPerson.isEmpty() || email.isEmpty() || pass.isEmpty() || conf.isEmpty()) {
            lblError.setText("Please fill all required fields.");
            return;
        }
        
        if (!pass.equals(conf)) {
            lblError.setText("Passwords do not match.");
            return;
        }
        
        if (pass.length() < 6) {
            lblError.setText("Password must be at least 6 characters long.");
            return;
        }
        
        // Basic email validation
        if (!email.contains("@") || !email.contains(".")) {
            lblError.setText("Please enter a valid email address.");
            return;
        }
        
        // Check if company already exists in repository
        Company existingCompany = CompanyDataStore.getInstance().getCompanyByEmail(email);
        if (existingCompany != null) {
            lblError.setText("A company with this email already exists.");
            return;
        }
        
        // Generate a username from company name (lowercase, no spaces)
        String username = name.toLowerCase().replaceAll("[^a-z0-9]", "_");
        
        // Create new Company object
        // Using constructor: Company(id, username, password, email, fullName, companyId, companyName, 
        //                        contactPerson, phone, website, address, companyDescription, isVerified, totalJobsPosted)
        
        // Get next available ID (you'd typically get this from your data store)
        int nextId = CompanyDataStore.getInstance().getAllCompanies().size() + 1;
        
        Company newCompany = new Company(
            nextId,                          // id
            username,                        // username
            pass,                            // password
            email,                           // email
            name,                            // fullName (inherited)
            "C" + String.format("%04d", nextId), // companyId (e.g., C0005)
            name,                            // companyName
            contactPerson,                   // contactPerson
            phone,                           // phone
            website,                         // website
            address,                         // address
            description,                     // companyDescription
            false,                           // isVerified (default false)
            0                                 // totalJobsPosted
        );
        
        // Save to repository
        CompanyDataStore.getInstance().addCompany(newCompany);
        
        // Show success message
        String message = String.format(
            "Company registration successful!\n\n" +
            "Company: %s\n" +
            "Contact: %s\n" +
            "Email: %s\n" +
            "Phone: %s\n\n" +
            "Your account is pending verification by an administrator.\n" +
            "You will be able to login once verified.",
            name, contactPerson, email, phone.isEmpty() ? "Not provided" : phone
        );
        
        JOptionPane.showMessageDialog(this,
            message,
            "Registration Successful",
            JOptionPane.INFORMATION_MESSAGE);
        
        if (registrationListener != null) {
            registrationListener.onRegistrationComplete();
        }
    }
    
    private void setupRoleRadio(JRadioButton rb) {
        rb.setBackground(CARD_BG);
        rb.setFocusPainted(false);
        rb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rb.setFont(new Font("SansSerif", Font.PLAIN, 12));
    }
    
    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setForeground(new Color(60, 60, 60));
        return l;
    }
    
    private void styleField(JComponent c) {
        c.setFont(new Font("SansSerif", Font.PLAIN, 13));
        c.setPreferredSize(new Dimension(10, 36));
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 10, 8, 10)
        ));
    }
}