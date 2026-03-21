// com.placement.system.dao/StudentDAO.java
package com.placement.system.dao;

import com.placement.system.models.Student;
import com.placement.system.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private static StudentDAO instance;
    
    private StudentDAO() {}
    
    public static StudentDAO getInstance() {
        if (instance == null) {
            instance = new StudentDAO();
        }
        return instance;
    }
    
    // ========== DATA ACCESS METHODS ==========
    
    /**
     * Get a student by their user ID (which is also the student ID in students table)
     */
    public Student getStudent(int id) {
        String sql = "SELECT s.*, u.email, u.username, u.password, u.fullName, u.isActive " +
                     "FROM students s " +
                     "JOIN users u ON s.id = u.id " +
                     "WHERE s.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting student by ID: " + id);
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get a student by their username
     */
    public Student getStudentByUsername(String username) {
        String sql = "SELECT s.*, u.email, u.username, u.password, u.fullName, u.isActive " +
                     "FROM students s " +
                     "JOIN users u ON s.id = u.id " +
                     "WHERE u.username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting student by username: " + username);
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get a student by their email
     */
    public Student getStudentByEmail(String email) {
        String sql = "SELECT s.*, u.email, u.username, u.password, u.fullName, u.isActive " +
                     "FROM students s " +
                     "JOIN users u ON s.id = u.id " +
                     "WHERE u.email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting student by email: " + email);
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get a student by their student ID (e.g., "STU001")
     */
    public Student getStudentByStudentID(String studentId) {
        String sql = "SELECT s.*, u.email, u.username, u.password, u.fullName, u.isActive " +
                     "FROM students s " +
                     "JOIN users u ON s.id = u.id " +
                     "WHERE s.studentId = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting student by studentId: " + studentId);
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all students
     */
    public List<Student> getAllStudents() {
        List<Student> studentList = new ArrayList<>();
        String sql = "SELECT s.*, u.email, u.username, u.password, u.fullName, u.isActive " +
                     "FROM students s " +
                     "JOIN users u ON s.id = u.id " +
                     "ORDER BY s.studentId";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                studentList.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all students");
            e.printStackTrace();
        }
        return studentList;
    }
    
    /**
     * Update an existing student
     */
    public boolean updateStudent(Student updatedStudent) {
        Connection conn = null;
        PreparedStatement pstmtUser = null;
        PreparedStatement pstmtStudent = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Update users table
            String userSql = "UPDATE users SET email = ?, fullName = ?, password = ? WHERE id = ?";
            pstmtUser = conn.prepareStatement(userSql);
            pstmtUser.setString(1, updatedStudent.getEmail());
            pstmtUser.setString(2, updatedStudent.getFullName());
            pstmtUser.setString(3, updatedStudent.getPassword());
            pstmtUser.setInt(4, updatedStudent.getId());
            pstmtUser.executeUpdate();
            
            // Update students table
            String studentSql = "UPDATE students SET course = ?, branch = ?, cgpa = ?, " +
                                "year = ?, phone = ?, placementStatus = ?, resumePath = ?, " +
                                "studentBio = ? WHERE id = ?";
            pstmtStudent = conn.prepareStatement(studentSql);
            pstmtStudent.setString(1, updatedStudent.getCourse());
            pstmtStudent.setString(2, updatedStudent.getBranch());
            pstmtStudent.setDouble(3, updatedStudent.getCgpa());
            pstmtStudent.setString(4, updatedStudent.getYear());
            pstmtStudent.setString(5, updatedStudent.getPhone());
            pstmtStudent.setString(6, updatedStudent.getPlacementStatus());
            pstmtStudent.setString(7, updatedStudent.getResumePath());
            pstmtStudent.setString(8, updatedStudent.getStudentBio());
            pstmtStudent.setInt(9, updatedStudent.getId());
            
            int rowsAffected = pstmtStudent.executeUpdate();
            
            conn.commit(); // Commit transaction
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating student: " + updatedStudent.getId());
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); // Rollback on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (pstmtUser != null) pstmtUser.close();
                if (pstmtStudent != null) pstmtStudent.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    // Don't close conn here - let DatabaseConnection manage it
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    /**
     * Add a new student (registers both users and students tables)
     */
    public Student addStudent(Student student) {
        Connection conn = null;
        PreparedStatement pstmtUser = null;
        PreparedStatement pstmtStudent = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Insert into users table
            String userSql = "INSERT INTO users (email, username, password, fullName, role, isActive) " +
                             "VALUES (?, ?, ?, ?, 'STUDENT', 1)";
            pstmtUser = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            pstmtUser.setString(1, student.getEmail());
            pstmtUser.setString(2, student.getUsername());
            pstmtUser.setString(3, student.getPassword());
            pstmtUser.setString(4, student.getFullName());
            pstmtUser.executeUpdate();
            
            // Get the generated user ID
            rs = pstmtUser.getGeneratedKeys();
            int userId = -1;
            if (rs.next()) {
                userId = rs.getInt(1);
            }
            
            if (userId == -1) {
                throw new SQLException("Failed to get generated user ID");
            }
            
            // Insert into students table
            String studentSql = "INSERT INTO students (id, studentId, course, branch, cgpa, year, " +
                                "phone, placementStatus, resumePath, studentBio) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmtStudent = conn.prepareStatement(studentSql);
            pstmtStudent.setInt(1, userId);
            pstmtStudent.setString(2, student.getStudentId());
            pstmtStudent.setString(3, student.getCourse());
            pstmtStudent.setString(4, student.getBranch());
            pstmtStudent.setDouble(5, student.getCgpa());
            pstmtStudent.setString(6, student.getYear());
            pstmtStudent.setString(7, student.getPhone());
            pstmtStudent.setString(8, student.getPlacementStatus());
            pstmtStudent.setString(9, student.getResumePath());
            pstmtStudent.setString(10, student.getStudentBio());
            pstmtStudent.executeUpdate();
            
            conn.commit(); // Commit transaction
            
            // Set the new ID and return
            student.setId(userId);
            return student;
            
        } catch (SQLException e) {
            System.err.println("Error adding new student");
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); // Rollback on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmtUser != null) pstmtUser.close();
                if (pstmtStudent != null) pstmtStudent.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    // ========== QUERY METHODS ==========
    
    /**
     * Get students by branch
     */
    public List<Student> getStudentsByBranch(String branch) {
        List<Student> studentList = new ArrayList<>();
        String sql = "SELECT s.*, u.email, u.username, u.password, u.fullName, u.isActive " +
                     "FROM students s " +
                     "JOIN users u ON s.id = u.id " +
                     "WHERE s.branch = ? " +
                     "ORDER BY s.studentId";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, branch);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                studentList.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting students by branch: " + branch);
            e.printStackTrace();
        }
        return studentList;
    }
    
    /**
     * Get students by placement status
     */
    public List<Student> getStudentsByPlacementStatus(String status) {
        List<Student> studentList = new ArrayList<>();
        String sql = "SELECT s.*, u.email, u.username, u.password, u.fullName, u.isActive " +
                     "FROM students s " +
                     "JOIN users u ON s.id = u.id " +
                     "WHERE s.placementStatus = ? " +
                     "ORDER BY s.studentId";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                studentList.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting students by status: " + status);
            e.printStackTrace();
        }
        return studentList;
    }
    
    /**
     * Get students with CGPA greater than or equal to minimum
     */
    public List<Student> getStudentsByMinCGPA(double minCGPA) {
        List<Student> studentList = new ArrayList<>();
        String sql = "SELECT s.*, u.email, u.username, u.password, u.fullName, u.isActive " +
                     "FROM students s " +
                     "JOIN users u ON s.id = u.id " +
                     "WHERE s.cgpa >= ? " +
                     "ORDER BY s.cgpa DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, minCGPA);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                studentList.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting students by min CGPA: " + minCGPA);
            e.printStackTrace();
        }
        return studentList;
    }
    
    // ========== BUSINESS LOGIC METHODS ==========
    
    /**
     * Change student password
     */
    public boolean changePassword(int studentId, String currentPassword, String newPassword) {
        // First verify current password
        Student student = getStudent(studentId);
        if (student == null || !student.getPassword().equals(currentPassword)) {
            return false;
        }
        
        // Update password in users table
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, studentId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error changing password for student: " + studentId);
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Validate if student is eligible for placement
     */
    public boolean validateStudentForPlacement(int studentId) {
        Student student = getStudent(studentId);
        if (student == null) return false;
        
        // Check business rules
        return !"Placed".equals(student.getPlacementStatus()) 
            && !"Blocked".equals(student.getPlacementStatus())
            && student.getCgpa() >= 5.0; // Minimum CGPA requirement
    }
    
    /**
     * Update just the placement status
     */
    public boolean updatePlacementStatus(int studentId, String newStatus) {
        String sql = "UPDATE students SET placementStatus = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, studentId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating placement status for student: " + studentId);
            e.printStackTrace();
        }
        return false;
    }
    
    // ========== HELPER METHODS ==========
    
    /**
     * Convert a ResultSet row to a Student object
     */
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("email"),
            rs.getString("fullName"),
            rs.getString("studentId"),
            rs.getString("course"),
            rs.getString("branch"),
            rs.getDouble("cgpa"),
            rs.getString("year"),
            rs.getString("phone"),
            rs.getString("placementStatus"),
            rs.getString("resumePath"),
            rs.getString("studentBio")
        );
    }
}