// com.placement.system.utils/CompanyDataStore.java
package com.placement.system.utils;

import com.placement.system.models.Company;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyDataStore {
    private static CompanyDataStore instance;
    private Map<Integer, Company> companies = new HashMap<>();
    private Map<String, Company> companiesByEmail = new HashMap<>();
    private Map<String, Company> companiesByName = new HashMap<>();
    private int nextId = 3; // Start after mock data
    
    private CompanyDataStore() {
        initializeMockData();
    }
    
    public static CompanyDataStore getInstance() {
        if (instance == null) {
            instance = new CompanyDataStore();
        }
        return instance;
    }
    
    private void initializeMockData() {
        // Company(int id, String username, String password, String email,
        //         String fullName, String companyId, String companyName, 
        //         String industry, String contactPerson, String phone, 
        //         String website, String companyDescription, String address,
        //         boolean isVerified, int totalJobsPosted)
        
        Company company1 = new Company(
            1, "tech_nova", "1234", "hr@technova.com",
            "TechNova Ltd", "C0001", "TechNova Ltd",
            "Jane Harper", "555-0201", 
            "www.technova.com", "Ebène, Mauritius",
            "Leading technology company specializing in software development and IT services.",
             true, 5
        );
        companies.put(1, company1);
        companiesByEmail.put("hr@technova.com", company1);
        companiesByName.put("TechNova Ltd", company1);
        
        Company company2 = new Company(
            2, "mcb_ltd", "password", "careers@mcb.mu",
            "MCB Ltd", "C0002", "MCB Ltd",
            "John Smith", "555-0301",
            "www.mcb.mu", "Port Louis, Mauritius",
            "Mauritius Commercial Bank - Leading financial institution.",
            true, 3
        );
        companies.put(2, company2);
        companiesByEmail.put("careers@mcb.mu", company2);
        companiesByName.put("MCB Ltd", company2);
        
        // Add a pending company for testing
        Company company3 = new Company(
            3, "startup_inc", "password", "hello@startup.mu",
            "Startup Inc", "C0003", "Startup Inc",
            "Alice Brown", "555-0401",
            "www.startup.mu", "Curepipe, Mauritius", 
            "A new startup looking for talent.",
            false, 0
        );
        companies.put(3, company3);
        companiesByEmail.put("hello@startup.mu", company3);
        companiesByName.put("Startup Inc", company3);
        
        nextId = 4;
    }
    
    // ========== DATA ACCESS METHODS ==========
    
    public Company getCompany(int id) {
        return companies.get(id);
    }
    
    public Company getCompanyByEmail(String email) {
        return companiesByEmail.get(email);
    }
    
    public Company getCompanyByUsername(String username) {
        for (Company company : companies.values()) {
            if (company.getUsername().equals(username)) {
                return company;
            }
        }
        return null;
    }
    
    public Company getCompanyByName(String companyName) {
        return companiesByName.get(companyName);
    }
    
    public List<Company> getAllCompanies() {
        return new ArrayList<>(companies.values());
    }
    
    public List<Company> getVerifiedCompanies() {
        List<Company> result = new ArrayList<>();
        for (Company company : companies.values()) {
            if (company.isVerified()) {
                result.add(company);
            }
        }
        return result;
    }
    
    public boolean updateCompany(Company updatedCompany) {
        if (companies.containsKey(updatedCompany.getId())) {
            // Update all maps
            Company oldCompany = companies.get(updatedCompany.getId());
            
            // Remove old entries if email/name changed
            if (!oldCompany.getEmail().equals(updatedCompany.getEmail())) {
                companiesByEmail.remove(oldCompany.getEmail());
            }
            if (!oldCompany.getCompanyName().equals(updatedCompany.getCompanyName())) {
                companiesByName.remove(oldCompany.getCompanyName());
            }
            
            // Update with new values
            companies.put(updatedCompany.getId(), updatedCompany);
            companiesByEmail.put(updatedCompany.getEmail(), updatedCompany);
            companiesByName.put(updatedCompany.getCompanyName(), updatedCompany);
            
            return true;
        }
        return false;
    }
    
    public Company addCompany(Company company) {
        int newId = nextId++;
        company.setId(newId);
        company.setCompanyId("C" + String.format("%04d", newId));
        
        companies.put(newId, company);
        companiesByEmail.put(company.getEmail(), company);
        companiesByName.put(company.getCompanyName(), company);
        
        return company;
    }
    
    public boolean deleteCompany(int id) {
        Company company = companies.get(id);
        if (company != null) {
            companies.remove(id);
            companiesByEmail.remove(company.getEmail());
            companiesByName.remove(company.getCompanyName());
            return true;
        }
        return false;
    }
    
    // ========== BUSINESS LOGIC METHODS ==========
    
    public boolean changePassword(int companyId, String currentPassword, String newPassword) {
        Company company = companies.get(companyId);
        if (company != null && company.getPassword().equals(currentPassword)) {
            company.setPassword(newPassword);
            return true;
        }
        return false;
    }
    
    public boolean verifyCompany(int companyId) {
        Company company = companies.get(companyId);
        if (company != null) {
            company.setVerified(true);
            return true;
        }
        return false;
    }
    
    public boolean incrementJobCount(int companyId) {
        Company company = companies.get(companyId);
        if (company != null) {
            company.incrementJobCount();
            return true;
        }
        return false;
    }
    
    public List<Company> searchCompanies(String searchTerm) {
        List<Company> result = new ArrayList<>();
        String term = searchTerm.toLowerCase();
        
        for (Company company : companies.values()) {
            if (company.getCompanyName().toLowerCase().contains(term) ||
                company.getContactPerson().toLowerCase().contains(term)) {
                result.add(company);
            }
        }
        return result;
    }
    
    // ========== STATISTICS METHODS ==========
    
    public int getTotalCompanies() {
        return companies.size();
    }
    
    public int getVerifiedCompaniesCount() {
        int count = 0;
        for (Company company : companies.values()) {
            if (company.isVerified()) count++;
        }
        return count;
    }
    
    public int getPendingCompaniesCount() {
        int count = 0;
        for (Company company : companies.values()) {
            if (!company.isVerified()) count++;
        }
        return count;
    }
    
}