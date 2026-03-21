// com.placement.system.dao/CompanyDAO.java
package com.placement.system.dao;

import com.placement.system.models.Company;
import com.placement.system.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO {
    private static CompanyDAO instance;
    
    private CompanyDAO() {}
    
    public static CompanyDAO getInstance() {
        if (instance == null) {
            instance = new CompanyDAO();
        }
        return instance;
    }
    
    // ========== DATA ACCESS METHODS ==========
    
    /**
     * Get a company by their user ID
     */
    public Company getCompany(int id) {
        String sql = "SELECT c.*, u.email, u.username, u.password, u.fullName, u.isActive " +
                     "FROM companies c " +
                     "JOIN users u ON c.id = u.id " +
                     "WHERE c.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCompany(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting company by ID: " + id);
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get a company by their email
     */
    public Company getCompanyByEmail(String email) {
        String sql = "SELECT c.*, u.email, u.username, u.password, u.fullName, u.isActive " +
                     "FROM companies c " +
                     "JOIN users u ON c.id = u.id " +
                     "WHERE u.email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCompany(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting company by email: " + email);
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get a company by their username
     */
    public Company getCompanyByUsername(String username) {
        String sql = "SELECT c.*, u.email, u.username, u.password, u.fullName, u.isActive " +
                     "FROM companies c " +
                     "JOIN users u ON c.id = u.id " +
                     "WHERE u.username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCompany(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting company by username: " + username);
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get a company by their company name
     */
    public Company getCompanyByName(String companyName) {
        String sql = "SELECT c.*, u.email, u.username, u.password, u.fullName, u.isActive " +
                     "FROM companies c " +
                     "JOIN users u ON c.id = u.id " +
                     "WHERE c.companyName = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, companyName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCompany(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting company by name: " + companyName);
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all companies
     */
    public List<Company> getAllCompanies() {
        List<Company> companyList = new ArrayList<>();
        String sql = "SELECT c.*, u.email, u.username, u.password, u.fullName, u.isActive " +
                     "FROM companies c " +
                     "JOIN users u ON c.id = u.id " +
                     "ORDER BY c.companyName";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                companyList.add(mapResultSetToCompany(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all companies");
            e.printStackTrace();
        }
        return companyList;
    }
    
    /**
     * Get only verified companies
     */
    public List<Company> getVerifiedCompanies() {
        List<Company> companyList = new ArrayList<>();
        String sql = "SELECT c.*, u.email, u.username, u.password, u.fullName, u.isActive " +
                     "FROM companies c " +
                     "JOIN users u ON c.id = u.id " +
                     "WHERE c.isVerified = TRUE " +
                     "ORDER BY c.companyName";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                companyList.add(mapResultSetToCompany(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting verified companies");
            e.printStackTrace();
        }
        return companyList;
    }
    
    /**
     * Update an existing company
     */
    public boolean updateCompany(Company updatedCompany) {
        Connection conn = null;
        PreparedStatement pstmtUser = null;
        PreparedStatement pstmtCompany = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Update users table
            String userSql = "UPDATE users SET email = ?, fullName = ?, password = ? WHERE id = ?";
            pstmtUser = conn.prepareStatement(userSql);
            pstmtUser.setString(1, updatedCompany.getEmail());
            pstmtUser.setString(2, updatedCompany.getFullName());
            pstmtUser.setString(3, updatedCompany.getPassword());
            pstmtUser.setInt(4, updatedCompany.getId());
            pstmtUser.executeUpdate();
            
            // Update companies table
            String companySql = "UPDATE companies SET companyName = ?, contactPerson = ?, " +
                                "phone = ?, website = ?, address = ?, companyDescription = ?, " +
                                "isVerified = ?, totalJobsPosted = ? WHERE id = ?";
            pstmtCompany = conn.prepareStatement(companySql);
            pstmtCompany.setString(1, updatedCompany.getCompanyName());
            pstmtCompany.setString(2, updatedCompany.getContactPerson());
            pstmtCompany.setString(3, updatedCompany.getPhone());
            pstmtCompany.setString(4, updatedCompany.getWebsite());
            pstmtCompany.setString(5, updatedCompany.getAddress());
            pstmtCompany.setString(6, updatedCompany.getCompanyDescription());
            pstmtCompany.setBoolean(7, updatedCompany.isVerified());
            pstmtCompany.setInt(8, updatedCompany.getTotalJobsPosted());
            pstmtCompany.setInt(9, updatedCompany.getId());
            
            int rowsAffected = pstmtCompany.executeUpdate();
            
            conn.commit(); // Commit transaction
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating company: " + updatedCompany.getId());
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (pstmtUser != null) pstmtUser.close();
                if (pstmtCompany != null) pstmtCompany.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    /**
     * Add a new company (registers both users and companies tables)
     */
    public Company addCompany(Company company) {
        Connection conn = null;
        PreparedStatement pstmtUser = null;
        PreparedStatement pstmtCompany = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert into users table
            String userSql = "INSERT INTO users (email, username, password, fullName, role, isActive) " +
                             "VALUES (?, ?, ?, ?, 'COMPANY', 1)";
            pstmtUser = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            pstmtUser.setString(1, company.getEmail());
            pstmtUser.setString(2, company.getUsername());
            pstmtUser.setString(3, company.getPassword());
            pstmtUser.setString(4, company.getFullName());
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
            
            // Generate companyId (e.g., "C0005")
            String companyId = "C" + String.format("%04d", userId);
            company.setCompanyId(companyId);
            
            // Insert into companies table
            String companySql = "INSERT INTO companies (id, companyId, companyName, contactPerson, " +
                                "phone, website, address, companyDescription, isVerified, totalJobsPosted) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmtCompany = conn.prepareStatement(companySql);
            pstmtCompany.setInt(1, userId);
            pstmtCompany.setString(2, companyId);
            pstmtCompany.setString(3, company.getCompanyName());
            pstmtCompany.setString(4, company.getContactPerson());
            pstmtCompany.setString(5, company.getPhone());
            pstmtCompany.setString(6, company.getWebsite());
            pstmtCompany.setString(7, company.getAddress());
            pstmtCompany.setString(8, company.getCompanyDescription());
            pstmtCompany.setBoolean(9, company.isVerified());
            pstmtCompany.setInt(10, company.getTotalJobsPosted());
            pstmtCompany.executeUpdate();
            
            conn.commit();
            
            company.setId(userId);
            return company;
            
        } catch (SQLException e) {
            System.err.println("Error adding new company");
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmtUser != null) pstmtUser.close();
                if (pstmtCompany != null) pstmtCompany.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * Delete a company (cascades to users table due to FK constraint)
     */
    public boolean deleteCompany(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting company: " + id);
            e.printStackTrace();
        }
        return false;
    }
    
    // ========== BUSINESS LOGIC METHODS ==========
    
    /**
     * Change company password
     */
    public boolean changePassword(int companyId, String currentPassword, String newPassword) {
        // First verify current password
        Company company = getCompany(companyId);
        if (company == null || !company.getPassword().equals(currentPassword)) {
            return false;
        }
        
        // Update password in users table
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, companyId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error changing password for company: " + companyId);
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Verify a company (admin function)
     */
    public boolean verifyCompany(int companyId) {
        String sql = "UPDATE companies SET isVerified = TRUE WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, companyId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error verifying company: " + companyId);
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Increment job count when company posts a new job
     */
    public boolean incrementJobCount(int companyId) {
        String sql = "UPDATE companies SET totalJobsPosted = totalJobsPosted + 1 WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, companyId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error incrementing job count for company: " + companyId);
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Search companies by name or contact person
     */
    public List<Company> searchCompanies(String searchTerm) {
        List<Company> companyList = new ArrayList<>();
        String sql = "SELECT c.*, u.email, u.username, u.password, u.fullName, u.isActive " +
                     "FROM companies c " +
                     "JOIN users u ON c.id = u.id " +
                     "WHERE LOWER(c.companyName) LIKE ? OR LOWER(c.contactPerson) LIKE ? " +
                     "ORDER BY c.companyName";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String pattern = "%" + searchTerm.toLowerCase() + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                companyList.add(mapResultSetToCompany(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching companies with term: " + searchTerm);
            e.printStackTrace();
        }
        return companyList;
    }
    
    // ========== STATISTICS METHODS ==========
    
    /**
     * Get total number of companies
     */
    public int getTotalCompanies() {
        String sql = "SELECT COUNT(*) as count FROM companies";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total companies count");
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get number of verified companies
     */
    public int getVerifiedCompaniesCount() {
        String sql = "SELECT COUNT(*) as count FROM companies WHERE isVerified = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting verified companies count");
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get number of pending companies (not verified)
     */
    public int getPendingCompaniesCount() {
        String sql = "SELECT COUNT(*) as count FROM companies WHERE isVerified = FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting pending companies count");
            e.printStackTrace();
        }
        return 0;
    }
    
    // ========== HELPER METHODS ==========
    
    /**
     * Convert a ResultSet row to a Company object
     */
    private Company mapResultSetToCompany(ResultSet rs) throws SQLException {
        return new Company(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("email"),
            rs.getString("fullName"),
            rs.getString("companyId"),
            rs.getString("companyName"),
            rs.getString("contactPerson"),
            rs.getString("phone"),
            rs.getString("website"),
            rs.getString("address"),
            rs.getString("companyDescription"),
            rs.getBoolean("isVerified"),
            rs.getInt("totalJobsPosted")
        );
    }
}