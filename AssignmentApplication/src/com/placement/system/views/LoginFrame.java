// com.placement.system.views/LoginFrame.java
package com.placement.system.views;

import javax.swing.*;
import java.awt.*;
import com.placement.system.models.User;
import com.placement.system.utils.SessionManager;

public class LoginFrame extends JFrame implements 
        LoginPanel.LoginListener, 
        RegistrationPanel.RegistrationListener {
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private RegistrationPanel registrationPanel;
    
    public LoginFrame() {
        setTitle("Student Placement System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        loginPanel = new LoginPanel(this);
        registrationPanel = new RegistrationPanel(this);
        
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registrationPanel, "REGISTER");
        
        add(mainPanel);
        
        // Show login panel by default
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    // LoginListener implementation
    @Override
    public void onLoginSuccess(User user, String role) {
        SessionManager.getInstance().setCurrentUser(user);
        
        dispose(); // Close login window
        
        // Open appropriate dashboard
        switch(role) {
            case "STUDENT":
                new StudentDashboard().setVisible(true);
                break;
            case "COMPANY":
                new CompanyDashboard().setVisible(true);
                break;
            case "ADMIN":
                new AdminDashboard().setVisible(true);
                break;
        }
    }
    
    @Override
    public void onRegisterRequest() {
        cardLayout.show(mainPanel, "REGISTER");
        registrationPanel.reset();
    }
    
    // RegistrationListener implementation
    @Override
    public void onRegistrationComplete() {
        JOptionPane.showMessageDialog(this, 
            "Registration successful! Please login.", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
        cardLayout.show(mainPanel, "LOGIN");
        loginPanel.reset();
    }
    
    @Override
    public void onBackToLogin() {
        cardLayout.show(mainPanel, "LOGIN");
        loginPanel.reset();
    }
}