// com.placement.system.models/Job.java
package com.placement.system.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Job {
    private int jobId;
    private int companyId;          // Foreign key to Company
    private String jobTitle;
    private String department;
    private String location;
    private String employmentType;   // "Full-time", "Internship", "Contract"
    private String salaryRange;      // e.g., "Rs 45,000 - 60,000/month"
    private int vacancies;
    private LocalDate applicationDeadline;
    private double minCgpa;          // Minimum CGPA requirement
    private String description;
    private LocalDateTime postedDate;
    private boolean isActive;
    
    // Constructors
    public Job() {}
    
    public Job(int jobId, int companyId, String jobTitle, String department, 
               String location, String employmentType, String salaryRange, 
               int vacancies, LocalDate applicationDeadline, double minCgpa, 
               String description, LocalDateTime postedDate, boolean isActive) {
        this.jobId = jobId;
        this.companyId = companyId;
        this.jobTitle = jobTitle;
        this.department = department;
        this.location = location;
        this.employmentType = employmentType;
        this.salaryRange = salaryRange;
        this.vacancies = vacancies;
        this.applicationDeadline = applicationDeadline;
        this.minCgpa = minCgpa;
        this.description = description;
        this.postedDate = postedDate;
        this.isActive = isActive;
    }
    
    // Getters and Setters
    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }
    
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
    
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }
    
    public String getSalaryRange() { return salaryRange; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }
    
    public int getVacancies() { return vacancies; }
    public void setVacancies(int vacancies) { this.vacancies = vacancies; }
    
    public LocalDate getApplicationDeadline() { return applicationDeadline; }
    public void setApplicationDeadline(LocalDate applicationDeadline) { this.applicationDeadline = applicationDeadline; }
    
    public double getMinCgpa() { return minCgpa; }
    public void setMinCgpa(double minCgpa) { this.minCgpa = minCgpa; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getPostedDate() { return postedDate; }
    public void setPostedDate(LocalDateTime postedDate) { this.postedDate = postedDate; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    // Utility Methods
    public boolean isDeadlinePassed() {
        return LocalDate.now().isAfter(applicationDeadline);
    }
    
    public boolean isEligible(double studentCgpa) {
        return studentCgpa >= minCgpa;
    }
    
    @Override
    public String toString() {
        return String.format("Job[%d] %s at %s", jobId, jobTitle, location);
    }
}