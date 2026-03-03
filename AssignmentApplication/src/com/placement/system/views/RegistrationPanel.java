// com.placement.system.views/RegistrationPanel.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

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
    private JTextField txtFirst = new JTextField();
    private JTextField txtLast = new JTextField();
    private JTextField txtEmail = new JTextField();
    private JPasswordField txtPass = new JPasswordField();
    private JPasswordField txtConfirm = new JPasswordField();
    private JTextField txtFaculty = new JTextField();
    private JTextField txtCourse = new JTextField();
    private JTextField txtYear = new JTextField();
    
    // Company fields
    private JTextField txtCompanyName = new JTextField();
    private JTextField txtCompanyEmail = new JTextField();
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
        txtFirst.setText("");
        txtLast.setText("");
        txtEmail.setText("");
        txtPass.setText("");
        txtConfirm.setText("");
        txtFaculty.setText("");
        txtCourse.setText("");
        txtYear.setText("");
        
        txtCompanyName.setText("");
        txtCompanyEmail.setText("");
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
        addField(p, gc, r++, "Student ID", txtStudentId);
        addField(p, gc, r++, "First Name", txtFirst);
        addField(p, gc, r++, "Last Name", txtLast);
        addField(p, gc, r++, "Email", txtEmail);
        addField(p, gc, r++, "Password", txtPass);
        addField(p, gc, r++, "Confirm Password", txtConfirm);
        addField(p, gc, r++, "Faculty", txtFaculty);
        addField(p, gc, r++, "Course", txtCourse);
        addField(p, gc, r++, "Year of Study", txtYear);
        
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
        addField(p, gc, r++, "Company Name", txtCompanyName);
        addField(p, gc, r++, "Company Email", txtCompanyEmail);
        addField(p, gc, r++, "Password", txtCompanyPass);
        addField(p, gc, r++, "Confirm Password", txtCompanyConfirm);
        
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
        String studentId = txtStudentId.getText().trim();
        String first = txtFirst.getText().trim();
        String last = txtLast.getText().trim();
        String email = txtEmail.getText().trim();
        String pass = new String(txtPass.getPassword());
        String conf = new String(txtConfirm.getPassword());
        String faculty = txtFaculty.getText().trim();
        String course = txtCourse.getText().trim();
        String yearStr = txtYear.getText().trim();
        
        if (studentId.isEmpty() || first.isEmpty() || last.isEmpty() || email.isEmpty() || pass.isEmpty() || conf.isEmpty()
                || faculty.isEmpty() || course.isEmpty() || yearStr.isEmpty()) {
            lblError.setText("All fields are required.");
            return;
        }
        if (!pass.equals(conf)) {
            lblError.setText("Passwords do not match.");
            return;
        }
        
        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (Exception ex) {
            lblError.setText("Year must be a number.");
            return;
        }
        
        // Here you would save to database
        JOptionPane.showMessageDialog(this,
                "Student registration successful. You can now login.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        
        if (registrationListener != null) {
            registrationListener.onRegistrationComplete();
        }
    }
    
    private void doCompanyRegistration() {
        String name = txtCompanyName.getText().trim();
        String email = txtCompanyEmail.getText().trim();
        String pass = new String(txtCompanyPass.getPassword());
        String conf = new String(txtCompanyConfirm.getPassword());
        
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || conf.isEmpty()) {
            lblError.setText("All fields are required.");
            return;
        }
        if (!pass.equals(conf)) {
            lblError.setText("Passwords do not match.");
            return;
        }
        
        // Here you would save to database
        JOptionPane.showMessageDialog(this,
                "Company registration successful. You can now login.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        
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