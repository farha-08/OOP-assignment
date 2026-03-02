// com.placement.system.models/Student.java
package com.placement.system.models;

public class Student extends User {
    
    // Student-specific fields
    private String studentId;      // Roll number / Registration number
    private String course;          // e.g., "B.Tech", "BSc"
    private String branch;          // e.g., "Computer Science", "IT"
    private String section;         // e.g., "A", "B"
    private double cgpa;            // Current CGPA
    private String year;            // Year of study (e.g., "3", "Final Year")
    private String phone;           // Contact number
    private String placementStatus; // "Not Placed", "Offered", "Placed", "Blocked"
    
    // Full constructor
    public Student(int id, String username, String password, String email, 
                   String fullName, String studentId, String course, 
                   String branch, String section, double cgpa, 
                   String year, String phone, String placementStatus) {
        
        // Call parent constructor
        super(id, username, password, email, "STUDENT", fullName);
        
        // Initialize student fields
        this.studentId = studentId;
        this.course = course;
        this.branch = branch;
        this.section = section;
        this.cgpa = cgpa;
        this.year = year;
        this.phone = phone;
        this.placementStatus = placementStatus != null ? placementStatus : "Not Placed";
    }
    
    // Simplified constructor for basic student creation
    public Student(int id, String username, String password, String email, 
                   String fullName, String course, String branch, 
                   double cgpa, String year) {
        this(id, username, password, email, fullName, 
             "S" + String.format("%04d", id), // Generate student ID
             course, branch, "A", cgpa, year, "", "Not Placed");
    }
    
    // Copy constructor (useful for editing)
    public Student(Student other) {
        this(other.getId(), other.getUsername(), other.getPassword(), 
             other.getEmail(), other.getFullName(), other.getStudentId(),
             other.getCourse(), other.getBranch(), other.getSection(),
             other.getCgpa(), other.getYear(), other.getPhone(),
             other.getPlacementStatus());
    }
    
    // ==================== Getters and Setters ====================
    
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
    
    public String getSection() {
        return section;
    }
    
    public void setSection(String section) {
        this.section = section;
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
    
    // ==================== Utility Methods ====================
    
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
        return String.format("%s - %s (%s) | CGPA: %.2f | Year: %s", 
            course, branch, section, cgpa, year);
    }
    
    @Override
    public String toString() {
        return String.format("Student[%s] %s - %s", studentId, getFullName(), course);
    }
}