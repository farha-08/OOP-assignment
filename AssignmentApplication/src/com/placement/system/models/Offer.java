// com.placement.system.models/Offer.java
package com.placement.system.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Offer {
    private int offerId;
    private int applicationId;     // Foreign key to Application
    private int companyId;          // Foreign key to Company (denormalized)
    private int studentId;          // Foreign key to Student (denormalized)
    private LocalDateTime offerDate;
    private String offerDetails;     // Terms and conditions
    private String offerLetterPath;  // Path to PDF offer letter
    private LocalDate acceptanceDeadline;
    private String status;           // "Pending", "Accepted", "Rejected", "Expired"
    
    //Note: Some foreign keys are denormalised to prevent the loss of critical information in the records if the entity records data is changed
    
    // Constructors
    public Offer() {}
    
    public Offer(int offerId, int applicationId, int companyId, int studentId,
                 LocalDateTime offerDate, String offerDetails, String offerLetterPath,
                 LocalDate acceptanceDeadline, String status) {
        this.offerId = offerId;
        this.applicationId = applicationId;
        this.companyId = companyId;
        this.studentId = studentId;
        this.offerDate = offerDate;
        this.offerDetails = offerDetails;
        this.offerLetterPath = offerLetterPath;
        this.acceptanceDeadline = acceptanceDeadline;
        this.status = status;
    }
    
    // Getters and Setters
    public int getOfferId() { return offerId; }
    public void setOfferId(int offerId) { this.offerId = offerId; }
    
    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }
    
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
    
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    
    public LocalDateTime getOfferDate() { return offerDate; }
    public void setOfferDate(LocalDateTime offerDate) { this.offerDate = offerDate; }
    
    public String getOfferDetails() { return offerDetails; }
    public void setOfferDetails(String offerDetails) { this.offerDetails = offerDetails; }
    
    public String getOfferLetterPath() { return offerLetterPath; }
    public void setOfferLetterPath(String offerLetterPath) { this.offerLetterPath = offerLetterPath; }
    
    public LocalDate getAcceptanceDeadline() { return acceptanceDeadline; }
    public void setAcceptanceDeadline(LocalDate acceptanceDeadline) { this.acceptanceDeadline = acceptanceDeadline; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // Utility Methods
    public boolean isPending() {
        return "Pending".equals(status);
    }
    
    public boolean isAccepted() {
        return "Accepted".equals(status);
    }
    
    public boolean isRejected() {
        return "Rejected".equals(status);
    }
    
    public boolean isExpired() {
        return "Expired".equals(status) || 
               (acceptanceDeadline != null && LocalDate.now().isAfter(acceptanceDeadline));
    }
    
    public boolean canAccept() {
        return "Pending".equals(status) && !isExpired();
    }
    
    @Override
    public String toString() {
        return String.format("Offer[%d] Student:%d from Company:%d - %s", 
            offerId, studentId, companyId, status);
    }
}