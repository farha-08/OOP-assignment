// com.placement.system.utils/SessionManager.java
package com.placement.system.utils;

import com.placement.system.models.User;
import com.placement.system.models.Student;
import com.placement.system.models.Company;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    
    private SessionManager() {}
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public void logout() {
        this.currentUser = null;
    }
    
    // Helper method to get current user as Student (with casting)
    public Student getCurrentStudent() {
        if (currentUser != null && "STUDENT".equals(currentUser.getRole())) {
            return (Student) currentUser;
        }
        return null;
    }
    
    // Helper method to get current user as Company (with casting)
    public Company getCurrentCompany() {
        if (currentUser != null && "COMPANY".equals(currentUser.getRole())) {
            return (Company) currentUser;
        }
        return null;
    }
    
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public String getUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
}