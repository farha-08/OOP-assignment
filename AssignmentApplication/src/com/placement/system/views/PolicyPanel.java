// com.placement.system.views/PolicyPanel.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PolicyPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    // Color scheme matching other panels
    private static final Color MAIN_BG = new Color(0xCF, 0xCF, 0xCF);      // #CFCFCF
    private static final Color ACCENT_DARK = new Color(0x54, 0x54, 0x54);  // #545454
    private static final Color ACCENT_BUTTON = new Color(0x7D, 0x7D, 0x7D); // #7D7D7D
    
    public PolicyPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // ================= HEADER =================
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(MAIN_BG);
        
        JLabel title = new JLabel("Placement Policies");
        title.setForeground(ACCENT_DARK);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        
        JLabel sub = new JLabel("Guidelines and regulations for campus placement process");
        sub.setForeground(new Color(100, 100, 100));
        
        header.add(title);
        header.add(sub);
        
        add(header, BorderLayout.NORTH);
        
        // ================= MAIN CONTENT CARD =================
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Create tabbed pane for different policy categories
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabbedPane.setBackground(new Color(245, 245, 245));
        
        // Add policy tabs
        tabbedPane.addTab("General Rules", createGeneralRulesPanel());
        tabbedPane.addTab("Eligibility Criteria", createEligibilityPanel());
        tabbedPane.addTab("Application Process", createApplicationProcessPanel());
        tabbedPane.addTab("Offer & Acceptance", createOfferAcceptancePanel());
        tabbedPane.addTab("Code of Conduct", createCodeOfConductPanel());
        
        card.add(tabbedPane, BorderLayout.CENTER);
        
        // ================= BOTTOM ACKNOWLEDGMENT =================
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JCheckBox chkAcknowledge = new JCheckBox("I have read and understood all placement policies");
        chkAcknowledge.setFont(new Font("SansSerif", Font.PLAIN, 12));
        chkAcknowledge.setBackground(Color.WHITE);
        
        JButton btnAcknowledge = new JButton("Acknowledge");
        styleButton(btnAcknowledge);
        btnAcknowledge.setEnabled(false);
        
        chkAcknowledge.addActionListener(e -> {
            btnAcknowledge.setEnabled(chkAcknowledge.isSelected());
        });
        
        btnAcknowledge.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Thank you for acknowledging the placement policies.\nYou may now proceed with the placement process.",
                "Acknowledgement Received",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        JPanel leftBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftBottom.setBackground(Color.WHITE);
        leftBottom.add(chkAcknowledge);
        
        JPanel rightBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightBottom.setBackground(Color.WHITE);
        rightBottom.add(btnAcknowledge);
        
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(leftBottom, BorderLayout.WEST);
        bottomPanel.add(rightBottom, BorderLayout.EAST);
        
        card.add(bottomPanel, BorderLayout.SOUTH);
        
        add(card, BorderLayout.CENTER);
        
        // ================= LAST UPDATED FOOTER =================
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(MAIN_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        JLabel lastUpdated = new JLabel("Last updated: 15 February 2025 | Version 3.2");
        lastUpdated.setForeground(new Color(120, 120, 120));
        lastUpdated.setFont(new Font("SansSerif", Font.ITALIC, 11));
        
        footer.add(lastUpdated);
        
        add(footer, BorderLayout.SOUTH);
    }
    
    private JPanel createGeneralRulesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        textArea.setBackground(Color.WHITE);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        textArea.setText(
            "GENERAL PLACEMENT RULES AND REGULATIONS\n" +
            "========================================\n\n" +
            
            "1. ELIGIBILITY\n" +
            "   • All final year students are eligible to participate in the placement process.\n" +
            "   • Students must have a minimum CGPA of 6.0 (or as specified by companies).\n" +
            "   • Students with active backlogs may be restricted from certain companies.\n\n" +
            
            "2. REGISTRATION\n" +
            "   • Students must complete their profile with accurate academic and personal details.\n" +
            "   • Profile must be approved by the Placement Office before applying to companies.\n" +
            "   • Incomplete profiles will not be considered for placement opportunities.\n\n" +
            
            "3. COMPANY ELIGIBILITY\n" +
            "   • Companies must be registered with the Placement Office.\n" +
            "   • All job postings must include clear eligibility criteria.\n" +
            "   • Companies must adhere to the placement schedule.\n\n" +
            
            "4. ONE STUDENT ONE JOB POLICY\n" +
            "   • A student can hold only ONE job offer at any given time.\n" +
            "   • Once an offer is accepted, the student is withdrawn from further placement.\n" +
            "   • Students cannot apply to new companies after accepting an offer.\n\n" +
            
            "5. DISCIPLINE\n" +
            "   • Students must maintain professional conduct during interviews.\n" +
            "   • Any malpractice will result in immediate disqualification.\n" +
            "   • The Placement Office reserves the right to take disciplinary action."
        );
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createEligibilityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Use HTML for better formatting
        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        editorPane.setBackground(Color.WHITE);
        editorPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String html = "<html><body style='font-family: SansSerif; font-size: 12pt; margin: 10px;'>" +
            "<h2 style='color: #545454;'>Eligibility Criteria</h2>" +
            
            "<h3 style='color: #545454;'>Academic Requirements</h3>" +
            "<ul>" +
            "<li><b>Minimum CGPA:</b> 6.0 on a 10-point scale (may vary by company)</li>" +
            "<li><b>Backlogs:</b> Maximum 2 active backlogs allowed (some companies require 0)</li>" +
            "<li><b>Attendance:</b> Minimum 75% attendance in current academic year</li>" +
            "</ul>" +
            
            "<h3 style='color: #545454;'>Program-Specific Criteria</h3>" +
            "<ul>" +
            "<li><b>B.Tech/B.E.:</b> All branches eligible</li>" +
            "<li><b>M.Tech/M.E.:</b> Minimum 6.5 CGPA in graduation</li>" +
            "<li><b>MBA/M.Com:</b> Minimum 60% in graduation</li>" +
            "<li><b>MCA:</b> Minimum 6.5 CGPA or 65% in graduation</li>" +
            "</ul>" +
            
            "<h3 style='color: #545454;'>Company-Specific Criteria</h3>" +
            "<p>Companies may set additional criteria including:</p>" +
            "<ul>" +
            "<li>Higher CGPA requirements (e.g., 7.5, 8.0, etc.)</li>" +
            "<li>Specific branch eligibility</li>" +
            "<li>Technical skills and certifications</li>" +
            "<li>Internship experience</li>" +
            "<li>Communication skills assessment</li>" +
            "</ul>" +
            
            "<p style='color: #666; margin-top: 20px;'><i>Note: Always check individual job postings for specific eligibility requirements.</i></p>" +
            "</body></html>";
        
        editorPane.setText(html);
        
        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createApplicationProcessPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel stepsPanel = new JPanel();
        stepsPanel.setLayout(new BoxLayout(stepsPanel, BoxLayout.Y_AXIS));
        stepsPanel.setBackground(Color.WHITE);
        stepsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] steps = {
            "Step 1: Profile Registration",
            "  • Complete your profile with accurate academic details",
            "  • Upload resume and other required documents",
            "  • Get profile verified by Placement Office",
            "",
            "Step 2: Job Search",
            "  • Browse available job postings from registered companies",
            "  • Use filters to find relevant opportunities",
            "  • Check eligibility criteria before applying",
            "",
            "Step 3: Application Submission",
            "  • Submit applications to desired companies",
            "  • System checks eligibility automatically",
            "  • Applications can be submitted before deadline",
            "",
            "Step 4: Screening Process",
            "  • Companies review applications",
            "  • Shortlisted candidates receive notification",
            "  • Status updates visible in 'My Applications'",
            "",
            "Step 5: Interview Process",
            "  • Attend interviews as per company schedule",
            "  • May include written tests, technical rounds, and HR interviews",
            "  • Results communicated within stipulated timeframe",
            "",
            "Step 6: Offer and Acceptance",
            "  • Selected candidates receive offer letters",
            "  • Students must accept/reject offers within given timeframe",
            "  • Once accepted, student cannot apply elsewhere"
        };
        
        for (String step : steps) {
            JLabel label = new JLabel(step);
            label.setFont(new Font("SansSerif", 
                step.startsWith("Step") ? Font.BOLD : Font.PLAIN, 
                step.startsWith("Step") ? 14 : 12));
            if (step.startsWith("Step")) {
                label.setForeground(ACCENT_DARK);
            }
            stepsPanel.add(label);
            stepsPanel.add(Box.createVerticalStrut(5));
        }
        
        JScrollPane scrollPane = new JScrollPane(stepsPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createOfferAcceptancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        editorPane.setBackground(Color.WHITE);
        
        String html = "<html><body style='font-family: SansSerif; font-size: 12pt; margin: 15px;'>" +
            "<h2 style='color: #545454;'>Offer and Acceptance Policy</h2>" +
            
            "<h3 style='color: #545454;'>One Student One Job Policy</h3>" +
            "<p>To ensure fair distribution of opportunities, the institute follows a strict <b>\"One Student One Job\"</b> policy. " +
            "Once a student accepts an offer, they are immediately withdrawn from the active placement process.</p>" +
            
            "<h3 style='color: #545454;'>Offer Timeline</h3>" +
            "<ul>" +
            "<li><b>Offer Validity:</b> Typically 3-7 days from date of offer</li>" +
            "<li><b>Acceptance Deadline:</b> Clearly mentioned in offer letter</li>" +
            "<li><b>Extension:</b> Can be requested through Placement Office</li>" +
            "</ul>" +
            
            "<h3 style='color: #545454;'>Acceptance Process</h3>" +
            "<ol>" +
            "<li>Review offer details carefully</li>" +
            "<li>Accept offer through the system</li>" +
            "<li>Upload signed acceptance copy</li>" +
            "<li>Provide joining confirmation details</li>" +
            "</ol>" +
            
            "<h3 style='color: #545454;'>Important Rules</h3>" +
            "<ul>" +
            "<li><b>No Rejection after Acceptance:</b> Once accepted, you cannot reject the offer</li>" +
            "<li><b>No Dual Offers:</b> Cannot hold two offers simultaneously</li>" +
            "<li><b>No Backing Out:</b> Backing out after acceptance may lead to disciplinary action</li>" +
            "<li><b>Dream Company Clause:</b> Special considerations for dream companies (see Placement Office)</li>" +
            "</ul>" +
            
            "<div style='background-color: #FFF3CD; padding: 10px; margin-top: 20px; border-left: 4px solid #FFC107;'>" +
            "<b>⚠️ Important:</b> Students who reject an offer after acceptance will be barred from future placement drives.</div>" +
            "</body></html>";
        
        editorPane.setText(html);
        
        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCodeOfConductPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel conductPanel = new JPanel();
        conductPanel.setLayout(new BoxLayout(conductPanel, BoxLayout.Y_AXIS));
        conductPanel.setBackground(Color.WHITE);
        conductPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        String[] rules = {
            "CODE OF CONDUCT FOR STUDENTS",
            "══════════════════════════════",
            "",
            "1. PROFESSIONALISM",
            "   • Dress appropriately for interviews and placement activities",
            "   • Arrive on time for all scheduled events",
            "   • Maintain a respectful attitude toward company representatives",
            "",
            "2. INTEGRITY",
            "   • Provide accurate information in your profile",
            "   • Do not misrepresent academic achievements",
            "   • Report any issues to Placement Office immediately",
            "",
            "3. COMMUNICATION",
            "   • Respond to company communications promptly",
            "   • Keep Placement Office informed of any changes",
            "   • Use official email for all placement correspondence",
            "",
            "4. COMMITMENT",
            "   • Attend all interviews you apply for",
            "   • Notify at least 24 hours in advance if unable to attend",
            "   • Uphold commitments made to companies",
            "",
            "5. PENALTIES FOR VIOLATION",
            "   • First offense: Written warning",
            "   • Second offense: Suspension from placement for one semester",
            "   • Third offense: Permanent debarment from placement",
            "",
            "6. FOR COMPANIES",
            "   • Provide accurate job descriptions and eligibility criteria",
            "   • Respect student's time and commitments",
            "   • Communicate results within promised timeframe",
            "   • Adhere to the placement schedule"
        };
        
        for (String rule : rules) {
            JLabel label = new JLabel(rule);
            if (rule.contains("══") || rule.isEmpty()) {
                label.setFont(new Font("SansSerif", Font.PLAIN, 12));
            } else if (rule.matches("\\d\\..*") || rule.equals("CODE OF CONDUCT FOR STUDENTS")) {
                label.setFont(new Font("SansSerif", Font.BOLD, 14));
                label.setForeground(ACCENT_DARK);
            } else {
                label.setFont(new Font("SansSerif", Font.PLAIN, 12));
            }
            conductPanel.add(label);
            conductPanel.add(Box.createVerticalStrut(rule.isEmpty() ? 5 : 2));
        }
        
        JScrollPane scrollPane = new JScrollPane(conductPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void styleButton(JButton b) {
        b.setBackground(ACCENT_BUTTON);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_DARK, 1),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}