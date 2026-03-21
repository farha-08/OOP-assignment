// com.placement.system.utils/DatabaseConnection.java
package com.placement.system.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/placement_system";
    private static final String USERNAME = "root"; // Change to your MySQL username
    private static final String PASSWORD = "$MySQLPw1234"; // Change to your MySQL password
    
    // Don't store a single static connection - get a new one each time
    // This prevents connection closure issues
    
    private DatabaseConnection() {}
    
    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create new connection each time
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("✅ Database connected successfully!");
            return conn;
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found!");
            e.printStackTrace();
            throw new SQLException("JDBC Driver not found", e);
        }
    }
    
    // Helper method to close connection safely
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("📪 Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Test connection method
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        Connection conn = null;
        try {
            conn = getConnection();
            System.out.println("✅ Connection test successful!");
        } catch (SQLException e) {
            System.err.println("❌ Connection test failed!");
            e.printStackTrace();
        } finally {
            closeConnection(conn);
        }
    }
}