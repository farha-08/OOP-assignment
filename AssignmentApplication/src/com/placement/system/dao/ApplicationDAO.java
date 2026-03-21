// com.placement.system.dao/ApplicationDAO.java
package com.placement.system.dao;

import com.placement.system.models.Application;
import com.placement.system.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {
    private static ApplicationDAO instance;
    
    private ApplicationDAO() {}
    
    public static ApplicationDAO getInstance() {
        if (instance == null) {
            instance = new ApplicationDAO();
        }
        return instance;
    }
    
    /**
     * Get application by ID
     */
    public Application getApplication(int applicationId) {
        String sql = "SELECT * FROM applications WHERE applicationId = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, applicationId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToApplication(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting application by ID: " + applicationId);
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get applications for a specific student
     */
    public List<Application> getApplicationsByStudent(int studentId) {
        List<Application> appList = new ArrayList<>();
        String sql = "SELECT a.*, j.jobTitle, c.companyName FROM applications a " +
                     "JOIN jobs j ON a.jobId = j.jobId " +
                     "JOIN companies c ON j.companyId = c.id " +
                     "WHERE a.studentId = ? " +
                     "ORDER BY a.applicationDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appList.add(mapResultSetToApplicationWithDetails(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting applications for student: " + studentId);
            e.printStackTrace();
        }
        return appList;
    }
    
    /**
     * Get applications for a specific job
     */
    public List<Application> getApplicationsByJob(int jobId) {
        List<Application> appList = new ArrayList<>();
        String sql = "SELECT a.*, u.fullName FROM applications a " +
                     "JOIN students s ON a.studentId = s.id " +
                     "JOIN users u ON s.id = u.id " +
                     "WHERE a.jobId = ? " +
                     "ORDER BY a.applicationDate";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, jobId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                appList.add(mapResultSetToApplicationWithStudent(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting applications for job: " + jobId);
            e.printStackTrace();
        }
        return appList;
    }
    
    /**
     * Create a new application
     */
    public boolean createApplication(Application application) {
        String sql = "INSERT INTO applications (jobId, studentId, applicationDate, status) " +
                     "VALUES (?, ?, NOW(), ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, application.getJobId());
            pstmt.setInt(2, application.getStudentId());
            pstmt.setString(3, application.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    application.setApplicationId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating application");
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Update application status
     */
    public boolean updateApplicationStatus(int applicationId, String newStatus) {
        String sql = "UPDATE applications SET status = ? WHERE applicationId = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, applicationId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating application status for: " + applicationId);
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Check if student already applied to a job
     */
    public boolean hasApplied(int studentId, int jobId) {
        String sql = "SELECT COUNT(*) as count FROM applications " +
                     "WHERE studentId = ? AND jobId = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, jobId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if student applied");
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get application count for a job
     */
    public int getApplicationCountForJob(int jobId) {
        String sql = "SELECT COUNT(*) as count FROM applications WHERE jobId = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, jobId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting application count for job: " + jobId);
            e.printStackTrace();
        }
        return 0;
    }
    
    private Application mapResultSetToApplication(ResultSet rs) throws SQLException {
        Application app = new Application();
        app.setApplicationId(rs.getInt("applicationId"));
        app.setJobId(rs.getInt("jobId"));
        app.setStudentId(rs.getInt("studentId"));
        app.setApplicationDate(rs.getTimestamp("applicationDate").toLocalDateTime());
        app.setStatus(rs.getString("status"));
        return app;
    }
    
    private Application mapResultSetToApplicationWithDetails(ResultSet rs) throws SQLException {
        Application app = mapResultSetToApplication(rs);
        // Add extra fields if your Application model has them
        return app;
    }
    
    private Application mapResultSetToApplicationWithStudent(ResultSet rs) throws SQLException {
        Application app = mapResultSetToApplication(rs);
        // Add student name if needed
        return app;
    }
}