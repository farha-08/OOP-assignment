// com.placement.system.views/LoginPanel.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import com.placement.system.models.User;
import com.placement.system.models.Student;
import com.placement.system.models.Company;
import com.placement.system.utils.SessionManager;
import com.placement.system.utils.StudentDataStore;
import com.placement.system.utils.CompanyDataStore;


public class LoginPanel extends JPanel {
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
        
        JLabel brand = new JLabel("Student Placement System");
        brand.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        JLabel hint = new JLabel("Login to access your dashboard");
        hint.setForeground(new Color(120, 120, 120));
        hint.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        header.add(brand, BorderLayout.WEST);
        header.add(hint, BorderLayout.EAST);
        
        // Body
        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        
        JPanel formCard = buildLoginFormCard();
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 235, 242)),
                new EmptyBorder(22, 22, 22, 22)
        ));
        formCard.setBackground(Color.WHITE);
        formCard.setMinimumSize(new Dimension(520, 420));
        
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 0;
        gc.weightx = 1; gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;
        body.add(formCard, gc);
        
        page.add(header, BorderLayout.NORTH);
        page.add(body, BorderLayout.CENTER);
        
        add(page, BorderLayout.CENTER);
        
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
        roleRow.setBackground(Color.WHITE);
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
        actions.setBackground(Color.WHITE);
        
        JButton btnLogin = new JButton("Login");
        stylePrimary(btnLogin);
        btnLogin.addActionListener(e -> performLogin());
        
        JButton btnRegister = new JButton("Register");
        styleSecondary(btnRegister);
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
        rb.setBackground(Color.WHITE);
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
            Student student = StudentDataStore.getInstance().getStudentByUsername(identifier);
            if (student == null) {
                student = StudentDataStore.getInstance().getStudentByEmail(identifier);
            }
            
            if (student != null && student.getPassword().equals(pass)) {
                user = student;
                role = "STUDENT";
            }
        } else if (rbCompany.isSelected()) {
            System.out.println("Attempting company login with: " + identifier);
            
            Company company = CompanyDataStore.getInstance().getCompanyByUsername(identifier);
            if (company == null) {
                company = CompanyDataStore.getInstance().getCompanyByEmail(identifier);
            }
            
            System.out.println("Company found: " + company);
            if (company != null) {
                System.out.println("Password match: " + company.getPassword().equals(pass));
            }
            
            if (company != null && company.getPassword().equals(pass)) {
                user = company;
                role = "COMPANY";
                System.out.println("Login successful for: " + company.getCompanyName());
            }
        } else {
            // Admin login - to be implemented
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
                BorderFactory.createLineBorder(new Color(220, 225, 232)),
                new EmptyBorder(8, 10, 8, 10)
        ));
    }
    
    private void stylePrimary(JButton b) {
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        b.setBackground(new Color(46, 204, 113));
        b.setForeground(Color.WHITE);
    }
    
    private void styleSecondary(JButton b) {
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        b.setBackground(new Color(240, 240, 240));
    }
}