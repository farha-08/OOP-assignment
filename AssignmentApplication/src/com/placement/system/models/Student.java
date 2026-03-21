// com.placement.system.models/Student.java
package com.placement.system.models;

public class Student extends User {
    
    // Student-specific fields
    private String studentId;           // Roll number / Registration number
    private String course;               // e.g., "B.Tech", "BSc"
    private String branch;               // e.g., "Computer Science", "IT"
    private double cgpa;                  // Current CGPA
    private String year;                   // Year of study (e.g., "3", "Final Year")
    private String phone;                  // Contact number
    private String placementStatus;        // "Not Placed", "Offered", "Placed", "Blocked"
    
    // NEW FIELDS
    private String resumePath;             // Path to uploaded resume file
    private String studentBio;              // Brief description about the student
    
    // Full constructor with all fields
    public Student(int id, String username, String password, String email, 
                   String fullName, String studentId, String course, 
                   String branch, double cgpa, String year, String phone, 
                   String placementStatus, String resumePath, String studentBio) {
        
        // Call parent constructor
        super(id, username, password, email, "STUDENT", fullName);
        
        // Initialize student fields
        this.studentId = studentId;
        this.course = course;
        this.branch = branch;
        this.cgpa = cgpa;
        this.year = year;
        this.phone = phone;
        this.placementStatus = placementStatus != null ? placementStatus : "Not Placed";
        
        // Initialize new fields
        this.resumePath = resumePath;
        this.studentBio = studentBio;
    }
    
    // Backward compatibility constructor (for existing code)
    public Student(int id, String username, String password, String email, 
                   String fullName, String studentId, String course, 
                   String branch, double cgpa, String year, String phone, 
                   String placementStatus) {
        this(id, username, password, email, fullName, studentId, course, 
             branch, cgpa, year, phone, placementStatus, null, null);
    }
    
    // Simplified constructor for basic student creation
    public Student(int id, String username, String password, String email, 
                   String fullName, String course, String branch, 
                   double cgpa, String year) {
        this(id, username, password, email, fullName, 
             "S" + String.format("%04d", id), // Generate student ID
             course, branch, cgpa, year, "", "Not Placed", null, null);
    }
    
    // Copy constructor (useful for editing)
    public Student(Student other) {
        this(other.getId(), other.getUsername(), other.getPassword(), 
             other.getEmail(), other.getFullName(), other.getStudentId(),
             other.getCourse(), other.getBranch(), other.getCgpa(), 
             other.getYear(), other.getPhone(), other.getPlacementStatus(),
             other.getResumePath(), other.getStudentBio());
    }
    
    // ==================== Getters and Setters for New Fields ====================
    
    public String getResumePath() {
        return resumePath;
    }
    
    public void setResumePath(String resumePath) {
        this.resumePath = resumePath;
    }
    
    public String getStudentBio() {
        return studentBio;
    }
    
    public void setStudentBio(String studentBio) {
        this.studentBio = studentBio;
    }
 
    // ==================== Existing Getters and Setters ====================
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getCourse() {
        return course;
    }
    
    public void setCourse(String course) {
        this.course = course;
    }
    
    public String getBranch() {
        return branch;
    }
    
    public void setBranch(String branch) {
        this.branch = branch;
    }
    
    public double getCgpa() {
        return cgpa;
    }
    
    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }
    
    public String getYear() {
        return year;
    }
    
    public void setYear(String year) {
        this.year = year;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getPlacementStatus() {
        return placementStatus;
    }
    
    public void setPlacementStatus(String placementStatus) {
        this.placementStatus = placementStatus;
    }
    
    // ==================== New Utility Methods ====================
    
    /**
     * Check if student has uploaded a resume
     */
    public boolean hasResume() {
        return resumePath != null && !resumePath.isEmpty();
    }
    

    
    /**
     * Get profile completion percentage (for UI feedback)
     */
    public int getProfileCompletionPercentage() {
        int totalFields = 11; // Count of important fields
        int completed = 0;
        
        if (studentId != null && !studentId.isEmpty()) completed++;
        if (getFullName() != null && !getFullName().isEmpty()) completed++;
        if (getEmail() != null && !getEmail().isEmpty()) completed++;
        if (phone != null && !phone.isEmpty()) completed++;
        if (course != null && !course.isEmpty()) completed++;
        if (branch != null && !branch.isEmpty()) completed++;
        if (cgpa > 0) completed++;
        if (year != null && !year.isEmpty()) completed++;
        if (studentBio != null && !studentBio.isEmpty()) completed++;
        if (resumePath != null && !resumePath.isEmpty()) completed++;
        
        return (completed * 100) / totalFields;
    }
    
    // ==================== Existing Utility Methods ====================
    
    /**
     * Check if student is eligible based on CGPA criteria
     */
    public boolean isEligible(double minimumCGPA) {
        return this.cgpa >= minimumCGPA;
    }
    
    /**
     * Check if student can apply for more jobs (not placed/blocked)
     */
    public boolean canApply() {
        return !"Placed".equals(placementStatus) && !"Blocked".equals(placementStatus);
    }
    
    /**
     * Get full academic info as string
     */
    public String getAcademicInfo() {
        return String.format("%s - %s | CGPA: %.2f | Year: %s", 
            course, branch, cgpa, year);
    }
    
    @Override
    public String toString() {
        return String.format("Student[%s] %s - %s (CGPA: %.2f)", 
            studentId, getFullName(), course, cgpa);
    }
}