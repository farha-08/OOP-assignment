// com.placement.system.models/Admin.java
package com.placement.system.models;

public class Admin extends User {
    
    // Admin-specific fields
    private String adminId;           // Unique admin identifier (e.g., "ADM001")
    private String department;         // e.g., "Placement Office", "HR", "Academic Affairs"
    private String subRole;            // "SUPER_ADMIN", "PLACEMENT_OFFICER", "COORDINATOR"
    
    /**
     * Full constructor
     */
    public Admin(int id, String username, String password, String email,
                 String fullName, String adminId, String department, String subRole) {
        
        // Call parent constructor
        super(id, username, password, email, "ADMIN", fullName);
        
        // Initialize admin fields
        this.adminId = adminId;
        this.department = department;
        this.subRole = subRole != null ? subRole : "PLACEMENT_OFFICER";
    }
    
    /**
     * Simplified constructor for basic admin creation
     */
    public Admin(int id, String username, String password, String email,
                 String fullName, String department) {
        this(id, username, password, email, fullName,
             "ADM" + String.format("%03d", id), // Generate admin ID
             department, "PLACEMENT_OFFICER");
    }
    
    /**
     * Copy constructor (useful for editing)
     */
    public Admin(Admin other) {
        this(other.getId(), other.getUsername(), other.getPassword(),
             other.getEmail(), other.getFullName(), other.getAdminId(),
             other.getDepartment(), other.getSubRole());
    }
    
    // ==================== Getters and Setters ====================
    
    public String getAdminId() {
        return adminId;
    }
    
    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getSubRole() {
        return subRole;
    }
    
    public void setSubRole(String subRole) {
        this.subRole = subRole;
    }
    
    // ==================== Utility Methods ====================
    
    /**
     * Check if admin has super admin privileges
     */
    public boolean isSuperAdmin() {
        return "SUPER_ADMIN".equals(subRole);
    }
    
    /**
     * Check if admin is a placement officer
     */
    public boolean isPlacementOfficer() {
        return "PLACEMENT_OFFICER".equals(subRole);
    }
    
    /**
     * Check if admin is a coordinator
     */
    public boolean isCoordinator() {
        return "COORDINATOR".equals(subRole);
    }
    
    /**
     * Get full admin info as string
     */
    public String getAdminInfo() {
        return String.format("%s - %s (%s)", adminId, getFullName(), subRole);
    }
    
    @Override
    public String toString() {
        return String.format("Admin[%s] %s - %s", adminId, getFullName(), department);
    }
}