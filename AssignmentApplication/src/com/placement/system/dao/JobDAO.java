// com.placement.system.dao/JobDAO.java
package com.placement.system.dao;

import com.placement.system.models.Job;
import com.placement.system.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JobDAO {
    private static JobDAO instance;
    
    private JobDAO() {}
    
    public static JobDAO getInstance() {
        if (instance == null) {
            instance = new JobDAO();
        }
        return instance;
    }
    
    /**
     * Get job by ID
     */
    public Job getJob(int jobId) {
        String sql = "SELECT j.*, c.companyName FROM jobs j " +
                     "JOIN companies c ON j.companyId = c.id " +
                     "WHERE j.jobId = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, jobId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToJob(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting job by ID: " + jobId);
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all jobs (active only by default)
     */
    public List<Job> getAllJobs(boolean onlyActive) {
        List<Job> jobList = new ArrayList<>();
        String sql = "SELECT j.*, c.companyName FROM jobs j " +
                     "JOIN companies c ON j.companyId = c.id ";
        
        if (onlyActive) {
            sql += "WHERE j.isActive = TRUE ";
        }
        
        sql += "ORDER BY j.postedDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                jobList.add(mapResultSetToJob(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all jobs");
            e.printStackTrace();
        }
        return jobList;
    }
    
    /**
     * Get jobs by company
     */
    public List<Job> getJobsByCompany(int companyId) {
        List<Job> jobList = new ArrayList<>();
        String sql = "SELECT j.*, c.companyName FROM jobs j " +
                     "JOIN companies c ON j.companyId = c.id " +
                     "WHERE j.companyId = ? " +
                     "ORDER BY j.postedDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, companyId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                jobList.add(mapResultSetToJob(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting jobs for company: " + companyId);
            e.printStackTrace();
        }
        return jobList;
    }
    
    /**
     * Get jobs eligible for a student based on CGPA
     */
    public List<Job> getEligibleJobsForStudent(double studentCgpa) {
        List<Job> jobList = new ArrayList<>();
        String sql = "SELECT j.*, c.companyName FROM jobs j " +
                     "JOIN companies c ON j.companyId = c.id " +
                     "WHERE j.isActive = TRUE " +
                     "AND j.applicationDeadline >= CURDATE() " +
                     "AND j.minCgpa <= ? " +
                     "ORDER BY j.postedDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, studentCgpa);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                jobList.add(mapResultSetToJob(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting eligible jobs");
            e.printStackTrace();
        }
        return jobList;
    }
    
    /**
     * Create a new job posting
     */
    public boolean createJob(Job job) {
        String sql = "INSERT INTO jobs (companyId, jobTitle, department, location, " +
                     "employmentType, salaryRange, vacancies, applicationDeadline, " +
                     "minCgpa, description, postedDate, isActive) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), 1)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, job.getCompanyId());
            pstmt.setString(2, job.getJobTitle());
            pstmt.setString(3, job.getDepartment());
            pstmt.setString(4, job.getLocation());
            pstmt.setString(5, job.getEmploymentType());
            pstmt.setString(6, job.getSalaryRange());
            pstmt.setInt(7, job.getVacancies());
            pstmt.setDate(8, Date.valueOf(job.getApplicationDeadline()));
            pstmt.setDouble(9, job.getMinCgpa());
            pstmt.setString(10, job.getDescription());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Increment company's job count
                CompanyDAO.getInstance().incrementJobCount(job.getCompanyId());
                
                // Get generated job ID
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    job.setJobId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating job");
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Update job status (active/inactive)
     */
    public boolean updateJobStatus(int jobId, boolean isActive) {
        String sql = "UPDATE jobs SET isActive = ? WHERE jobId = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, isActive);
            pstmt.setInt(2, jobId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating job status for job: " + jobId);
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Search jobs by title or company
     */
    public List<Job> searchJobs(String searchTerm) {
        List<Job> jobList = new ArrayList<>();
        String sql = "SELECT j.*, c.companyName FROM jobs j " +
                     "JOIN companies c ON j.companyId = c.id " +
                     "WHERE j.isActive = TRUE " +
                     "AND (LOWER(j.jobTitle) LIKE ? OR LOWER(c.companyName) LIKE ?) " +
                     "ORDER BY j.postedDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String pattern = "%" + searchTerm.toLowerCase() + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                jobList.add(mapResultSetToJob(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching jobs with term: " + searchTerm);
            e.printStackTrace();
        }
        return jobList;
    }
    
    private Job mapResultSetToJob(ResultSet rs) throws SQLException {
        Job job = new Job();
        job.setJobId(rs.getInt("jobId"));
        job.setCompanyId(rs.getInt("companyId"));
        job.setJobTitle(rs.getString("jobTitle"));
        job.setDepartment(rs.getString("department"));
        job.setLocation(rs.getString("location"));
        job.setEmploymentType(rs.getString("employmentType"));
        job.setSalaryRange(rs.getString("salaryRange"));
        job.setVacancies(rs.getInt("vacancies"));
        job.setApplicationDeadline(rs.getDate("applicationDeadline").toLocalDate());
        job.setMinCgpa(rs.getDouble("minCgpa"));
        job.setDescription(rs.getString("description"));
        job.setPostedDate(rs.getTimestamp("postedDate").toLocalDateTime());
        job.setActive(rs.getBoolean("isActive"));
        return job;
    }
}