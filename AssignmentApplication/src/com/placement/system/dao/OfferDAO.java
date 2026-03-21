// com.placement.system.dao/OfferDAO.java
package com.placement.system.dao;

import com.placement.system.models.Offer;
import com.placement.system.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OfferDAO {
    private static OfferDAO instance;
    
    private OfferDAO() {}
    
    public static OfferDAO getInstance() {
        if (instance == null) {
            instance = new OfferDAO();
        }
        return instance;
    }
    
    /**
     * Get offer by ID
     */
    public Offer getOffer(int offerId) {
        String sql = "SELECT * FROM offers WHERE offerId = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, offerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToOffer(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting offer by ID: " + offerId);
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get offers for a specific student
     */
    public List<Offer> getOffersByStudent(int studentId) {
        List<Offer> offerList = new ArrayList<>();
        String sql = "SELECT o.* FROM offers o " +
                     "WHERE o.studentId = ? " +
                     "ORDER BY o.offerDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                offerList.add(mapResultSetToOffer(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting offers for student: " + studentId);
            e.printStackTrace();
        }
        return offerList;
    }
    
    /**
     * Get offers for a specific student with job details (for display)
     */
    public List<Offer> getOffersByStudentWithDetails(int studentId) {
        List<Offer> offerList = new ArrayList<>();
        // Join through applications to get job details
        String sql = "SELECT o.*, a.jobId, j.jobTitle, j.companyId " +
                     "FROM offers o " +
                     "JOIN applications a ON o.applicationId = a.applicationId " +
                     "JOIN jobs j ON a.jobId = j.jobId " +
                     "WHERE o.studentId = ? " +
                     "ORDER BY o.offerDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Offer offer = mapResultSetToOffer(rs);
                // You can store additional job info in a Map or extend Offer model
                offerList.add(offer);
            }
        } catch (SQLException e) {
            System.err.println("Error getting offers with details for student: " + studentId);
            e.printStackTrace();
        }
        return offerList;
    }
    
    /**
     * Get offers for a specific company
     */
    public List<Offer> getOffersByCompany(int companyId) {
        List<Offer> offerList = new ArrayList<>();
        String sql = "SELECT o.*, u.fullName as studentName FROM offers o " +
                     "JOIN students s ON o.studentId = s.id " +
                     "JOIN users u ON s.id = u.id " +
                     "WHERE o.companyId = ? " +
                     "ORDER BY o.offerDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, companyId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                offerList.add(mapResultSetToOfferWithStudent(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting offers for company: " + companyId);
            e.printStackTrace();
        }
        return offerList;
    }
    
    /**
     * Create a new offer
     */
    public boolean createOffer(Offer offer) {
        String sql = "INSERT INTO offers (applicationId, companyId, studentId, " +
                     "offerDate, offerDetails, offerLetterPath, acceptDeadline, status) " +
                     "VALUES (?, ?, ?, NOW(), ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, offer.getApplicationId());
            pstmt.setInt(2, offer.getCompanyId());
            pstmt.setInt(3, offer.getStudentId());
            pstmt.setString(4, offer.getOfferDetails());
            pstmt.setString(5, offer.getOfferLetterPath());
            pstmt.setDate(6, Date.valueOf(offer.getAcceptanceDeadline()));
            pstmt.setString(7, offer.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    offer.setOfferId(rs.getInt(1));
                }
                
                // Update application status to 'Offered'
                ApplicationDAO.getInstance().updateApplicationStatus(
                    offer.getApplicationId(), "Offered"
                );
                
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating offer");
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Update offer status (Accept/Reject)
     */
    public boolean updateOfferStatus(int offerId, String newStatus) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Update offer status
            String sql = "UPDATE offers SET status = ? WHERE offerId = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, offerId);
            pstmt.executeUpdate();
            
            // If accepted, update student's placement status
            if ("Accepted".equals(newStatus)) {
                Offer offer = getOffer(offerId);
                if (offer != null) {
                    // Update student placement status
                    StudentDAO.getInstance().updatePlacementStatus(
                        offer.getStudentId(), "Placed"
                    );
                    
                    // Update application status
                    ApplicationDAO.getInstance().updateApplicationStatus(
                        offer.getApplicationId(), "Accepted"
                    );
                }
            } else if ("Rejected".equals(newStatus)) {
                Offer offer = getOffer(offerId);
                if (offer != null) {
                    ApplicationDAO.getInstance().updateApplicationStatus(
                        offer.getApplicationId(), "Rejected"
                    );
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error updating offer status: " + offerId);
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }
    
    /**
     * Check if student already has an accepted offer
     */
    public boolean hasAcceptedOffer(int studentId) {
        String sql = "SELECT COUNT(*) as count FROM offers " +
                     "WHERE studentId = ? AND status = 'Accepted'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking accepted offers for student: " + studentId);
            e.printStackTrace();
        }
        return false;
    }
    
    private Offer mapResultSetToOffer(ResultSet rs) throws SQLException {
        Offer offer = new Offer();
        offer.setOfferId(rs.getInt("offerId"));
        offer.setApplicationId(rs.getInt("applicationId"));
        offer.setCompanyId(rs.getInt("companyId"));
        offer.setStudentId(rs.getInt("studentId"));
        offer.setOfferDate(rs.getTimestamp("offerDate").toLocalDateTime());
        offer.setOfferDetails(rs.getString("offerDetails"));
        offer.setOfferLetterPath(rs.getString("offerLetterPath"));
        offer.setAcceptanceDeadline(rs.getDate("acceptDeadline").toLocalDate());
        offer.setStatus(rs.getString("status"));
        return offer;
    }
    
    private Offer mapResultSetToOfferWithDetails(ResultSet rs) throws SQLException {
        Offer offer = mapResultSetToOffer(rs);
        // Add job title and company name if needed
        return offer;
    }
    
    private Offer mapResultSetToOfferWithStudent(ResultSet rs) throws SQLException {
        Offer offer = mapResultSetToOffer(rs);
        // Add student name if needed
        return offer;
    }
}