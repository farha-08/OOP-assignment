// com.placement.system.models/Company.java
package com.placement.system.models;

public class Company extends User {
    
    // Company-specific fields
    private String companyId;           // Unique company identifier
    private String companyName;          // Full company name
    private String industry;             // e.g., "Technology", "Finance", "Consulting"
    private String contactPerson;        // HR/Recruiter name
    private String phone;                 // Contact phone
    private String website;               // Company website
    private String companyDescription;    // About the company
    private String address;               // Physical address
    private boolean isVerified;           // Admin verification status
    private int totalJobsPosted;           // Number of jobs posted
    
    // Full constructor
    public Company(int id, String username, String password, String email,
                   String fullName, String companyId, String companyName, 
                   String industry, String contactPerson, String phone, 
                   String website, String companyDescription, String address,
                   boolean isVerified, int totalJobsPosted) {
        
        // Call parent constructor
        super(id, username, password, email, "COMPANY", fullName);
        
        // Initialize company fields
        this.companyId = companyId;
        this.companyName = companyName;
        this.industry = industry;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.website = website;
        this.companyDescription = companyDescription;
        this.address = address;
        this.isVerified = isVerified;
        this.totalJobsPosted = totalJobsPosted;
    }
    
    // Simplified constructor for basic company creation
    public Company(int id, String username, String password, String email,
                   String companyName, String industry, String contactPerson) {
        this(id, username, password, email, companyName,
             "C" + String.format("%04d", id), // Generate company ID
             companyName, industry, contactPerson, "", "", 
             "", "", false, 0);
    }
    
    // Copy constructor (useful for editing)
    public Company(Company other) {
        this(other.getId(), other.getUsername(), other.getPassword(), 
             other.getEmail(), other.getFullName(), other.getCompanyId(),
             other.getCompanyName(), other.getIndustry(), other.getContactPerson(),
             other.getPhone(), other.getWebsite(), other.getCompanyDescription(),
             other.getAddress(), other.isVerified(), other.getTotalJobsPosted());
    }
    
    // ==================== Getters and Setters ====================
    
    public String getCompanyId() {
        return companyId;
    }
    
    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
        // Also update the inherited fullName for consistency
        setFullName(companyName);
    }
    
    public String getIndustry() {
        return industry;
    }
    
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getCompanyDescription() {
        return companyDescription;
    }
    
    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public boolean isVerified() {
        return isVerified;
    }
    
    public void setVerified(boolean verified) {
        isVerified = verified;
    }
    
    public int getTotalJobsPosted() {
        return totalJobsPosted;
    }
    
    public void setTotalJobsPosted(int totalJobsPosted) {
        this.totalJobsPosted = totalJobsPosted;
    }
    
    // Increment job count when posting new job
    public void incrementJobCount() {
        this.totalJobsPosted++;
    }
    
    // ==================== Utility Methods ====================
    
    /**
     * Get company contact info as formatted string
     */
    public String getContactInfo() {
        return String.format("Contact: %s | Phone: %s | Email: %s", 
            contactPerson, phone, getEmail());
    }
    
    /**
     * Check if company can post jobs (must be verified)
     */
    public boolean canPostJobs() {
        return isVerified;
    }
    
    /**
     * Get company summary
     */
    public String getSummary() {
        return String.format("%s (%s) - %s | Jobs Posted: %d", 
            companyName, industry, isVerified ? "Verified" : "Pending", totalJobsPosted);
    }
    
    @Override
    public String toString() {
        return String.format("Company[%s] %s - %s", companyId, companyName, industry);
    }
}