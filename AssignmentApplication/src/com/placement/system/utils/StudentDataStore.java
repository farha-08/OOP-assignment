// com.placement.system.utils/StudentDataStore.java
package com.placement.system.utils;

import com.placement.system.models.Student;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentDataStore {
    private static StudentDataStore instance;
    private Map<Integer, Student> students = new HashMap<>();
    private int nextId = 2; // Start after mock data
    
    private StudentDataStore() {
        // Initialize with mock data
        initializeMockData();
    }
    
    public static StudentDataStore getInstance() {
        if (instance == null) {
            instance = new StudentDataStore();
        }
        return instance;
    }
    
	 private void initializeMockData() {
	     // Student(int id, String username, String password, String email, 
	     //        String fullName, String studentId, String course, String branch, 
	     //        double cgpa, String year, String phone, String placementStatus)
		 
		 Student student1 = new Student(
				 1, "tony", "1234", "tony@uom.mu", 
	             "Tony", "S001", "Computer Science","FOICDT", 
	             4.0, "2", "555-0000", "Not Placed"
	             );
	         students.put(1, student1);
	     
	     Student student2 = new Student(
		         2, "john_doe", "password", "john@uni.edu", 
		         "John Doe", "S002", "Computer Science", "B.Tech", 
		         3.5, "3", "555-0101", "Not Placed"
	     );
	     students.put(2, student2);
	     
	     Student student3 = new Student(
		         3, "jane_smith", "password", "jane@uni.edu", 
		         "Jane Smith", "S003", "CInformation Systems", "B.Tech", 
		         4.2, "3", "555-0102", "Offered"
	     );
	     students.put(3, student3);
	 }
	 
	 // ========== DATA ACCESS METHODS ==========
	 
	 public Student getStudent(int id) {
	     return students.get(id);
	 }
	 
	 public Student getStudentByUsername(String username) {
	     for (Student student : students.values()) {
	         if (student.getUsername().equals(username)) {
	             return student;
	         }
	     }
	     return null;
	 }
	 
	 public Student getStudentByEmail(String email) {
	     for (Student student : students.values()) {
	         if (student.getEmail().equals(email)) {
	             return student;
	         }
	     }
	     return null;
	 }
	 
	 public Student getStudentByStudentID(String studentId) {
	     for (Student student : students.values()) {
	         if (student.getEmail().equals(studentId)) {
	             return student;
	         }
	     }
	     return null;
	 }
	 
	 public List<Student> getAllStudents() {
	     return new ArrayList<>(students.values());
	 }
	 
	 public boolean updateStudent(Student updatedStudent) {
	     if (students.containsKey(updatedStudent.getId())) {
	         students.put(updatedStudent.getId(), updatedStudent);
	         return true;
	     }
	     return false;
	 }
	 
	 public Student addStudent(Student student) {
	     int newId = nextId++;
	     student.setId(newId);
	     students.put(newId, student);
	     return student;
	 }
	 
	 // ========== QUERY METHODS ==========
	 
	 public List<Student> getStudentsByBranch(String branch) {
	     List<Student> result = new ArrayList<>();
	     for (Student student : students.values()) {
	         if (branch.equals(student.getBranch())) {
	             result.add(student);
	         }
	     }
	     return result;
	 }
	 
	 public List<Student> getStudentsByPlacementStatus(String status) {
	     List<Student> result = new ArrayList<>();
	     for (Student student : students.values()) {
	         if (status.equals(student.getPlacementStatus())) {
	             result.add(student);
	         }
	     }
	     return result;
	 }
	 
	 public List<Student> getStudentsByMinCGPA(double minCGPA) {
	     List<Student> result = new ArrayList<>();
	     for (Student student : students.values()) {
	         if (student.getCgpa() >= minCGPA) {
	             result.add(student);
	         }
	     }
	     return result;
	 }
	 
	 // ========== BUSINESS LOGIC METHODS ==========
	 
	 public boolean changePassword(int studentId, String currentPassword, String newPassword) {
	     Student student = students.get(studentId);
	     if (student != null && student.getPassword().equals(currentPassword)) {
	         student.setPassword(newPassword);
	         return true;
	     }
	     return false;
	 }
	 
	 public boolean validateStudentForPlacement(int studentId) {
	     Student student = students.get(studentId);
	     if (student == null) return false;
	     
	     // Check business rules
	     return !"Placed".equals(student.getPlacementStatus()) 
	         && !"Blocked".equals(student.getPlacementStatus())
	         && student.getCgpa() >= 5.0; // Minimum CGPA requirement
	 }
	 
	 public boolean updatePlacementStatus(int studentId, String newStatus) {
	     Student student = students.get(studentId);
	     if (student != null) {
	         student.setPlacementStatus(newStatus);
	         return true;
	     }
	     return false;
	 }
}