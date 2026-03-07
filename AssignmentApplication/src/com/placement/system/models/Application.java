// com.placement.system.models/Application.java
package com.placement.system.models;

import java.time.LocalDateTime;

public class Application {
    private int applicationId;
    private int jobId;           // Foreign key to Job
    private int studentId;        // Foreign key to Student
    private LocalDateTime applicationDate;
    private String status;        // "Applied", "Under Review", "Shortlisted", "Rejected", "Offered", "Accepted", "Withdrawn"
    
    // Constructors
    public Application() {}
    
    public Application(int applicationId, int jobId, int studentId, 
                       LocalDateTime applicationDate, String status) {
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.studentId = studentId;
        this.applicationDate = applicationDate;
        this.status = status;
    }
    
    // Getters and Setters
    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }
    
    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }
    
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    
    public LocalDateTime getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDateTime applicationDate) { this.applicationDate = applicationDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // Utility Methods
    public boolean isPending() {
        return "Applied".equals(status) || "Under Review".equals(status);
    }
    
    public boolean isShortlisted() {
        return "Shortlisted".equals(status);
    }
    
    public boolean isRejected() {
        return "Rejected".equals(status);
    }
    
    public boolean isOffered() {
        return "Offered".equals(status);
    }
    
    public boolean isAccepted() {
        return "Accepted".equals(status);
    }
    
    public boolean canWithdraw() {
        return !"Accepted".equals(status) && !"Rejected".equals(status);
    }
    
    @Override
    public String toString() {
        return String.format("Application[%d] Student:%d for Job:%d - %s", 
            applicationId, studentId, jobId, status);
    }
}