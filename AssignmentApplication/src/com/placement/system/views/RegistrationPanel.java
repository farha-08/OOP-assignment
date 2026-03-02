// com.placement.system.views/RegistrationPanel.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class RegistrationPanel extends JPanel {
    private JRadioButton rbStudent = new JRadioButton("STUDENT");
    private JRadioButton rbCompany = new JRadioButton("COMPANY");
    private ButtonGroup roleGroup = new ButtonGroup();
    
    private JLabel title = new JLabel("Student Registration");
    private JLabel sub = new JLabel("Provide your details to create an account.");
    private JLabel lblError = new JLabel(" ");
    
    private CardLayout regCards = new CardLayout();
    private JPanel regRoot = new JPanel(regCards);
    
    // Student fields
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
        setBackground(new Color(245, 247, 250));
        
        JPanel page = new JPanel(new BorderLayout());
        page.setOpaque(false);
        page.setBorder(new EmptyBorder(26, 26, 26, 26));
        
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 235, 242)),
                new EmptyBorder(18, 18, 18, 18)
        ));
        
        JPanel headerLeft = new JPanel();
        headerLeft.setBackground(Color.WHITE);
        headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS));
        headerLeft.add(title);
        headerLeft.add(Box.createVerticalStrut(6));
        headerLeft.add(sub);
        
        JButton btnBack = new JButton("Back to Login");
        btnBack.setFocusPainted(false);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> {
            if (registrationListener != null) {
                registrationListener.onBackToLogin();
            }
        });
        
        header.add(headerLeft, BorderLayout.WEST);
        header.add(btnBack, BorderLayout.EAST);
        
        // Body
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 235, 242)),
                new EmptyBorder(18, 18, 18, 18)
        ));
        
        // Role row
        JPanel roleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        roleRow.setBackground(Color.WHITE);
        
        setupRoleRadio(rbStudent);
        setupRoleRadio(rbCompany);
        roleGroup.add(rbStudent);
        roleGroup.add(rbCompany);
        
        rbStudent.setSelected(true);
        rbStudent.addActionListener(e -> switchRole());
        rbCompany.addActionListener(e -> switchRole());
        
        roleRow.add(label("Role:"));
        roleRow.add(rbStudent);
        roleRow.add(rbCompany);
        
        card.add(roleRow, BorderLayout.NORTH);
        
        // Forms
        regRoot.setBackground(Color.WHITE);
        regRoot.add(buildStudentForm(), "STUDENT");
        regRoot.add(buildCompanyForm(), "COMPANY");
        
        JScrollPane sc = new JScrollPane(regRoot,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sc.setBorder(null);
        
        card.add(sc, BorderLayout.CENTER);
        
        // Bottom actions
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);
        
        lblError.setForeground(new Color(190, 40, 40));
        lblError.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblError.setBorder(new EmptyBorder(6, 0, 0, 0));
        
        JButton btnRegister = new JButton("Register");
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegister.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        btnRegister.addActionListener(e -> doRegistration());
        
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actions.setBackground(Color.WHITE);
        actions.add(btnRegister);
        
        bottom.add(lblError, BorderLayout.NORTH);
        bottom.add(actions, BorderLayout.WEST);
        
        card.add(bottom, BorderLayout.SOUTH);
        
        body.add(card, BorderLayout.CENTER);
        
        page.add(header, BorderLayout.NORTH);
        page.add(body, BorderLayout.CENTER);
        
        add(page, BorderLayout.CENTER);
        
        switchRole();
    }
    
    public void reset() {
        rbStudent.setSelected(true);
        
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
        p.setBackground(Color.WHITE);
        
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        
        int r = 0;
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
        p.setBackground(Color.WHITE);
        
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
        String first = txtFirst.getText().trim();
        String last = txtLast.getText().trim();
        String email = txtEmail.getText().trim();
        String pass = new String(txtPass.getPassword());
        String conf = new String(txtConfirm.getPassword());
        String faculty = txtFaculty.getText().trim();
        String course = txtCourse.getText().trim();
        String yearStr = txtYear.getText().trim();
        
        if (first.isEmpty() || last.isEmpty() || email.isEmpty() || pass.isEmpty() || conf.isEmpty()
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
        rb.setBackground(Color.WHITE);
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
                BorderFactory.createLineBorder(new Color(220, 225, 232)),
                new EmptyBorder(8, 10, 8, 10)
        ));
    }
}