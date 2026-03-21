// com.placement.system.views/LoginPanel.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import com.placement.system.models.User;
import com.placement.system.models.Student;
import com.placement.system.models.Company;
import com.placement.system.models.Admin;
import com.placement.system.utils.SessionManager;
import com.placement.system.dao.StudentDAO;
import com.placement.system.dao.CompanyDAO;
import com.placement.system.dao.AdminDAO;

public class LoginPanel extends JPanel {
    // Color scheme matching the dashboard
    private static final Color MAIN_BG = new Color(0xCF, 0xCF, 0xCF);      // #CFCFCF
    private static final Color ACCENT = new Color(0x54, 0x54, 0x54);       // #545454
    private static final Color BTN = new Color(0x7D, 0x7D, 0x7D);          // #7D7D7D
    private static final Color CARD_BG = new Color(0xE6, 0xE3, 0xD6);      // beige-grey
    private static final Color BORDER = new Color(0x9A, 0x9A, 0x9A);       // #9A9A9A
    
    private JRadioButton rbStudent = new JRadioButton("STUDENT");
    private JRadioButton rbCompany = new JRadioButton("COMPANY");
    private JRadioButton rbAdmin = new JRadioButton("ADMIN");
    private ButtonGroup roleGroup = new ButtonGroup();
    
    private JTextField txtIdentifier = new JTextField();
    private JPasswordField txtPass = new JPasswordField();
    private JLabel lblError = new JLabel(" ");
    
    private JLabel title = new JLabel("Student Login");
    private JLabel sub = new JLabel("Enter your email and password to continue.");
    
    private LoginListener loginListener;
    
    public interface LoginListener {
        void onLoginSuccess(User user, String role);
        void onRegisterRequest();
    }
    
    public LoginPanel(LoginListener listener) {
        this.loginListener = listener;
        
        setLayout(new BorderLayout());
        setBackground(MAIN_BG);
        
        // Top header bar matching dashboard style
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ACCENT);
        header.setBorder(new EmptyBorder(8, 16, 8, 16));
        
        JLabel titleLabel = new JLabel("Student Placement System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Login to Your Account");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.WHITE);
        
        JPanel leftHeader = new JPanel(new BorderLayout());
        leftHeader.setBackground(ACCENT);
        leftHeader.add(titleLabel, BorderLayout.WEST);
        
        header.add(leftHeader, BorderLayout.WEST);
        header.add(subtitleLabel, BorderLayout.EAST);
        
        // Center content area
        JPanel centerContent = new JPanel(new GridBagLayout());
        centerContent.setBackground(MAIN_BG);
        centerContent.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JPanel formCard = buildLoginFormCard();
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(30, 40, 30, 40)
        ));
        formCard.setBackground(CARD_BG);
        // allow a larger box so controls aren't cramped
        formCard.setMaximumSize(new Dimension(650, 550));
        
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 0;
        gc.weightx = 0.5; gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;
        centerContent.add(formCard, gc);
        
        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(MAIN_BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        JLabel footerLabel = new JLabel("Student Placement System © 2026 - All Rights Reserved");
        footerLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        footerLabel.setForeground(new Color(100, 100, 100));
        footerLabel.setBorder(new EmptyBorder(8, 16, 8, 16));
        footer.add(footerLabel, BorderLayout.WEST);
        
        add(header, BorderLayout.NORTH);
        add(centerContent, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
        
        setupRoleRadios();
        updateLoginTitleByRole();
    }
    
    private JPanel buildLoginFormCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setOpaque(true);
        
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(new Color(90, 90, 90));
        
        // row 0: title
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        card.add(title, gc);
        
        // row 1: sub
        gc.gridy++;
        card.add(sub, gc);
        
        // row 2: role label
        gc.gridy++;
        gc.gridwidth = 2;
        card.add(label("Role"), gc);
        
        // row 3: role radios
        gc.gridy++;
        JPanel roleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        roleRow.setBackground(CARD_BG);
        roleRow.add(rbStudent);
        roleRow.add(rbCompany);
        roleRow.add(rbAdmin);
        card.add(roleRow, gc);
        
        // row 4: identifier label
        gc.gridy++;
        card.add(label("Username or Email"), gc);
        
        // row 5: identifier field
        gc.gridy++;
        styleField(txtIdentifier);
        card.add(txtIdentifier, gc);
        
        // row 6: password label
        gc.gridy++;
        card.add(label("Password"), gc);
        
        // row 7: password field
        gc.gridy++;
        styleField(txtPass);
        card.add(txtPass, gc);
        
        // row 8: error
        gc.gridy++;
        lblError.setForeground(new Color(190, 40, 40));
        lblError.setFont(new Font("SansSerif", Font.PLAIN, 12));
        card.add(lblError, gc);
        
        // row 9: buttons
        gc.gridy++;
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setBackground(CARD_BG);
        
        JButton btnLogin = new JButton("Login");
        stylePrimary(btnLogin);
        btnLogin.addActionListener(e -> performLogin());
        
        JButton btnRegister = new JButton("Register");
        stylePrimary(btnRegister);
        btnRegister.addActionListener(e -> {
            if (loginListener != null) {
                loginListener.onRegisterRequest();
            }
        });
        
        actions.add(btnLogin);
        actions.add(btnRegister);
        card.add(actions, gc);
        
        // Spacer
        gc.gridy++;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;
        card.add(Box.createVerticalGlue(), gc);
        
        return card;
    }
    
    private void setupRoleRadios() {
        roleGroup.add(rbStudent);
        roleGroup.add(rbCompany);
        roleGroup.add(rbAdmin);
        
        rbStudent.setSelected(true);
        
        ActionListener roleListener = e -> updateLoginTitleByRole();
        rbStudent.addActionListener(roleListener);
        rbCompany.addActionListener(roleListener);
        rbAdmin.addActionListener(roleListener);
        
        setupRoleRadio(rbStudent);
        setupRoleRadio(rbCompany);
        setupRoleRadio(rbAdmin);
    }
    
    private void setupRoleRadio(JRadioButton rb) {
        rb.setBackground(CARD_BG);
        rb.setFocusPainted(false);
        rb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rb.setFont(new Font("SansSerif", Font.PLAIN, 12));
    }
    
    private void updateLoginTitleByRole() {
        if (rbStudent.isSelected()) title.setText("Student Login");
        else if (rbCompany.isSelected()) title.setText("Company Login");
        else title.setText("Admin Login");
    }
    
    private void performLogin() {
        String identifier = txtIdentifier.getText().trim();
        String pass = new String(txtPass.getPassword());
        
        if (identifier.isEmpty() || pass.isEmpty()) {
            lblError.setText("Please enter username/email and password.");
            return;
        }
        
        User user = null;
        String role = null;
        
        if (rbStudent.isSelected()) {
            // Student login
            Student student = StudentDAO.getInstance().getStudentByUsername(identifier);
            if (student == null) {
                student = StudentDAO.getInstance().getStudentByEmail(identifier);
            }
            
            if (student != null && student.getPassword().equals(pass)) {
                user = student;
                role = "STUDENT";
                System.out.println("Student login successful: " + student.getFullName());
            }
            
        } else if (rbCompany.isSelected()) {
            // Company login
            System.out.println("Attempting company login with: " + identifier);
            
            Company company = CompanyDAO.getInstance().getCompanyByUsername(identifier);
            if (company == null) {
                company = CompanyDAO.getInstance().getCompanyByEmail(identifier);
            }
            
            if (company != null) {
                System.out.println("Company found: " + company.getCompanyName());
                System.out.println("Password match: " + company.getPassword().equals(pass));
            }
            
            if (company != null && company.getPassword().equals(pass)) {
                user = company;
                role = "COMPANY";
                System.out.println("Login successful for: " + company.getCompanyName());
            }
            
        } else {
            // Admin login
            System.out.println("Attempting admin login with: " + identifier);
            
            Admin admin = AdminDAO.getInstance().getAdminByUsername(identifier);
            
            if (admin != null) {
                System.out.println("Admin found: " + admin.getFullName());
                System.out.println("Password match: " + admin.getPassword().equals(pass));
            }
            
            if (admin != null && admin.getPassword().equals(pass)) {
                user = admin;
                role = "ADMIN";
                System.out.println("Login successful for: " + admin.getFullName());
            }
        }
        
        if (user == null) {
            lblError.setText("Invalid credentials.");
            System.out.println("Login failed for: " + identifier);
        } else {
            lblError.setText(" ");
            SessionManager.getInstance().setCurrentUser(user);
            System.out.println("User stored in session: " + user.getClass().getName());
            
            // Navigate to appropriate dashboard
            if (loginListener != null) {
                loginListener.onLoginSuccess(user, role);
            }
        }
    }
    
    public void reset() {
        txtIdentifier.setText("");
        txtPass.setText("");
        lblError.setText(" ");
        rbStudent.setSelected(true);
        updateLoginTitleByRole();
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
    
    private void stylePrimary(JButton b) {
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // border same as fill colour for solid look
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT),
                BorderFactory.createEmptyBorder(9, 16, 9, 16)
        ));
        b.setBackground(ACCENT);
        b.setForeground(Color.WHITE);
    }
    
    private void styleSecondary(JButton b) {
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        b.setBackground(MAIN_BG);
        b.setForeground(Color.BLACK);
        b.setBorder(BorderFactory.createLineBorder(BORDER));
    }
}