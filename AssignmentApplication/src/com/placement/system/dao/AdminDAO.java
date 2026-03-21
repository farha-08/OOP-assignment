// com.placement.system.dao/AdminDAO.java
package com.placement.system.dao;

import com.placement.system.models.Admin;
import com.placement.system.utils.DatabaseConnection;

import java.sql.*;

public class AdminDAO {
    private static AdminDAO instance;
    
    private AdminDAO() {}
    
    public static AdminDAO getInstance() {
        if (instance == null) {
            instance = new AdminDAO();
        }
        return instance;
    }
    
    /**
     * Get admin by username (for login)
     */
    public Admin getAdminByUsername(String username) {
        String sql = "SELECT a.*, u.email, u.username, u.password, u.fullName, u.isActive " +
                     "FROM admins a " +
                     "JOIN users u ON a.id = u.id " +
                     "WHERE u.username = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Admin admin = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                admin = new Admin(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("fullName"),
                    rs.getString("adminId"),
                    rs.getString("department"),
                    rs.getString("subRole")
                );
                System.out.println("✅ Admin found: " + admin.getFullName());
            } else {
                System.out.println("❌ No admin found with username: " + username);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting admin by username: " + username);
            e.printStackTrace();
        } finally {
            // Close resources in reverse order
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) DatabaseConnection.closeConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return admin;
    }
    
    /**
     * Verify admin login credentials
     */
    public boolean authenticate(String username, String password) {
        Admin admin = getAdminByUsername(username);
        boolean authenticated = admin != null && admin.getPassword().equals(password);
        if (authenticated) {
            System.out.println("✅ Admin authentication successful");
        } else {
            System.out.println("❌ Admin authentication failed");
        }
        return authenticated;
    }
}