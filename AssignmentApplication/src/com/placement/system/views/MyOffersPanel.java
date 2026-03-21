// com.placement.system.views/MyOffersPanel.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

import com.placement.system.models.Offer;
import com.placement.system.models.Job;
import com.placement.system.models.Company;
import com.placement.system.models.Student;
import com.placement.system.models.User;
import com.placement.system.utils.SessionManager;
import com.placement.system.dao.OfferDAO;
import com.placement.system.dao.JobDAO;
import com.placement.system.dao.CompanyDAO;
import com.placement.system.dao.StudentDAO;

public class MyOffersPanel extends JPanel {

    // Same style as MyApplicationsPanel
    private static final Color MAIN_BG = new Color(240, 240, 240);
    private static final Color ACCENT = new Color(0x54, 0x54, 0x54);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(200, 200, 200);

    private final DefaultTableModel model;
    private final JTable table;
    private final JLabel lblCount;

    private final JButton btnViewDetails;
    private final JButton btnAccept;
    private final JButton btnReject;

    private Student currentStudent;
    private List<Offer> offers;
    private Map<Integer, String> companyNameCache = new HashMap<>();
    private Map<Integer, String> jobTitleCache = new HashMap<>();

    public MyOffersPanel() {
        setLayout(new BorderLayout());
        setBackground(MAIN_BG);

        // Load current student
        loadCurrentStudent();

        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(MAIN_BG);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = createSectionHeader("My Offers");
        mainContent.add(headerPanel, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));

        String[] columns = {"Company", "Role", "Offer Date", "Deadline", "Status"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        styleTable(table);

        // Add double-click listener
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    viewSelectedOfferDetails();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(CARD_BG);
        card.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(CARD_BG);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        lblCount = new JLabel("Loading offers...");
        lblCount.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblCount.setBorder(new EmptyBorder(8, 8, 8, 8));
        bottomPanel.add(lblCount, BorderLayout.WEST);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        actionsPanel.setBackground(CARD_BG);

        btnViewDetails = createActionButton("View Details");
        btnAccept = createActionButton("Accept");
        btnReject = createActionButton("Reject");

        actionsPanel.add(btnViewDetails);
        actionsPanel.add(btnAccept);
        actionsPanel.add(btnReject);

        bottomPanel.add(actionsPanel, BorderLayout.EAST);
        card.add(bottomPanel, BorderLayout.SOUTH);

        mainContent.add(card, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // Action listeners
        btnViewDetails.addActionListener(e -> viewSelectedOfferDetails());
        btnAccept.addActionListener(e -> updateSelectedOfferStatus("Accepted"));
        btnReject.addActionListener(e -> updateSelectedOfferStatus("Rejected"));

        // Load offers
        refreshOffers();
    }

    /**
     * Load current student from session/database
     */
    private void loadCurrentStudent() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            if (user instanceof Student) {
                this.currentStudent = (Student) user;
            } else {
                // Load from DAO if not Student instance
                this.currentStudent = StudentDAO.getInstance().getStudent(user.getId());
                if (this.currentStudent != null) {
                    SessionManager.getInstance().setCurrentUser(this.currentStudent);
                }
            }
        }
    }

    /**
     * Refresh offers table (can be called from parent dashboard)
     */
    public void refreshOffers() {
        if (currentStudent == null) {
            loadCurrentStudent();
            if (currentStudent == null) {
                lblCount.setText("Please log in to view offers.");
                return;
            }
        }

        // Fetch offers from database
        OfferDAO offerDAO = OfferDAO.getInstance();
        offers = offerDAO.getOffersByStudent(currentStudent.getId());

        // Pre-cache company names and job titles
        cacheCompanyAndJobNames();

        // Refresh table display
        refreshTable();
    }

    /**
     * Cache company names and job titles for all offers
     */
    private void cacheCompanyAndJobNames() {
        CompanyDAO companyDAO = CompanyDAO.getInstance();
        JobDAO jobDAO = JobDAO.getInstance();

        for (Offer offer : offers) {
            // Cache company name
            if (!companyNameCache.containsKey(offer.getCompanyId())) {
                Company company = companyDAO.getCompany(offer.getCompanyId());
                if (company != null) {
                    companyNameCache.put(offer.getCompanyId(), company.getCompanyName());
                } else {
                    companyNameCache.put(offer.getCompanyId(), "Unknown Company");
                }
            }

            // Cache job title - need to get from application then job
            // For now, we'll fetch when needed in getRoleText()
        }
    }

    /**
     * Get company name by company ID
     */
    private String getCompanyName(int companyId) {
        return companyNameCache.getOrDefault(companyId, "Unknown Company");
    }

    /**
     * Get role/job title for an offer (from the associated job)
     */
    private String getRoleText(Offer offer) {
        // Get the job ID from the application
        // For now, we need to get job via application
        // Since we don't have direct jobId in Offer, we need to query through Application
        // Using a cached approach or direct lookup
        
        JobDAO jobDAO = JobDAO.getInstance();
        
        // Get the application to find jobId
        com.placement.system.dao.ApplicationDAO appDAO = 
            com.placement.system.dao.ApplicationDAO.getInstance();
        com.placement.system.models.Application app = appDAO.getApplication(offer.getApplicationId());
        
        if (app != null) {
            if (jobTitleCache.containsKey(app.getJobId())) {
                return jobTitleCache.get(app.getJobId());
            }
            Job job = jobDAO.getJob(app.getJobId());
            if (job != null) {
                jobTitleCache.put(app.getJobId(), job.getJobTitle());
                return job.getJobTitle();
            }
        }
        return "Position #" + offer.getApplicationId();
    }

    private void refreshTable() {
        model.setRowCount(0);

        if (offers == null || offers.isEmpty()) {
            lblCount.setText("0 offer(s) total. Offers will appear here when companies respond.");
            return;
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Add rows to table
        for (Offer offer : offers) {
            String displayStatus = getDisplayStatus(offer);
            model.addRow(new Object[]{
                    getCompanyName(offer.getCompanyId()),
                    getRoleText(offer),
                    offer.getOfferDate() != null ? offer.getOfferDate().format(dateTimeFormatter) : "N/A",
                    offer.getAcceptanceDeadline() != null ? offer.getAcceptanceDeadline().format(dateFormatter) : "N/A",
                    displayStatus
            });
        }

        // Update count label
        lblCount.setText(offers.size() + " offer(s) total.");
        resizeTableColumns();
    }

    /**
     * Get display status with expiration check
     */
    private String getDisplayStatus(Offer offer) {
        if (offer.isExpired() && !"Accepted".equalsIgnoreCase(offer.getStatus())) {
            return "Expired";
        }
        return offer.getStatus() == null ? "Pending" : offer.getStatus();
    }

    /**
     * View detailed information about a selected offer
     */
    private void viewSelectedOfferDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an offer first.",
                    "No Offer Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Offer offer = offers.get(selectedRow);
        showOfferDetailsDialog(offer);
    }

    /**
     * Show detailed offer dialog
     */
    private void showOfferDetailsDialog(Offer offer) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Offer Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(550, 550);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(MAIN_BG);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ACCENT);
        JLabel titleLabel = new JLabel("  Offer Details");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Offer Info Section
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        infoPanel.setBackground(CARD_BG);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(10, 10, 10, 10)
        ));

        infoPanel.add(createInfoLabel("Company:"));
        infoPanel.add(createInfoValue(getCompanyName(offer.getCompanyId())));

        infoPanel.add(createInfoLabel("Position:"));
        infoPanel.add(createInfoValue(getRoleText(offer)));

        infoPanel.add(createInfoLabel("Offer ID:"));
        infoPanel.add(createInfoValue(String.valueOf(offer.getOfferId())));

        infoPanel.add(createInfoLabel("Application ID:"));
        infoPanel.add(createInfoValue(String.valueOf(offer.getApplicationId())));

        infoPanel.add(createInfoLabel("Offer Date:"));
        infoPanel.add(createInfoValue(offer.getOfferDate() != null ? 
            offer.getOfferDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A"));

        infoPanel.add(createInfoLabel("Acceptance Deadline:"));
        infoPanel.add(createInfoValue(offer.getAcceptanceDeadline() != null ? 
            offer.getAcceptanceDeadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "N/A"));

        infoPanel.add(createInfoLabel("Status:"));
        infoPanel.add(createStatusValue(offer));

        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Offer Letter Path (if exists)
        if (offer.getOfferLetterPath() != null && !offer.getOfferLetterPath().isEmpty()) {
            JPanel letterPanel = new JPanel(new BorderLayout());
            letterPanel.setBackground(CARD_BG);
            letterPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(BORDER),
                    "Offer Letter",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new Font("Dialog", Font.BOLD, 12)
            ));

            JLabel pathLabel = new JLabel(offer.getOfferLetterPath());
            pathLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
            pathLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
            letterPanel.add(pathLabel, BorderLayout.CENTER);
            mainPanel.add(letterPanel);
            mainPanel.add(Box.createVerticalStrut(10));
        }

        // Offer Details
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(CARD_BG);
        detailsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER),
                "Offer Details",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Dialog", Font.BOLD, 12)
        ));

        JTextArea detailsArea = new JTextArea(offer.getOfferDetails() != null && !offer.getOfferDetails().isEmpty() 
                ? offer.getOfferDetails() 
                : "No additional details provided.");
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("Dialog", Font.PLAIN, 12));
        detailsArea.setMargin(new Insets(8, 8, 8, 8));
        detailsArea.setBackground(CARD_BG);

        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        detailsScroll.setPreferredSize(new Dimension(500, 120));
        detailsScroll.setBorder(BorderFactory.createLineBorder(BORDER));

        detailsPanel.add(detailsScroll, BorderLayout.CENTER);
        mainPanel.add(detailsPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Action buttons for the dialog
        boolean canAct = canTakeAction(offer);
        if (canAct) {
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            actionPanel.setBackground(MAIN_BG);

            JButton acceptBtn = new JButton("Accept Offer");
            styleActionButton(acceptBtn, new Color(0, 128, 0));
            acceptBtn.addActionListener(e -> {
                updateOfferStatus(offer, "Accepted", dialog);
            });

            JButton rejectBtn = new JButton("Reject Offer");
            styleActionButton(rejectBtn, new Color(178, 34, 34));
            rejectBtn.addActionListener(e -> {
                updateOfferStatus(offer, "Rejected", dialog);
            });

            actionPanel.add(acceptBtn);
            actionPanel.add(rejectBtn);
            mainPanel.add(actionPanel);
        }

        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        closePanel.setBackground(MAIN_BG);
        JButton closeBtn = new JButton("Close");
        styleButton(closeBtn);
        closeBtn.addActionListener(e -> dialog.dispose());
        closePanel.add(closeBtn);
        mainPanel.add(closePanel);

        JScrollPane mainScroll = new JScrollPane(mainPanel);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);

        dialog.add(mainScroll, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    /**
     * Check if student can take action on this offer
     */
    private boolean canTakeAction(Offer offer) {
        return "Pending".equalsIgnoreCase(offer.getStatus()) && !offer.isExpired();
    }

    /**
     * Update offer status and refresh
     */
    private void updateOfferStatus(Offer offer, String newStatus, JDialog dialog) {
        if (!canTakeAction(offer)) {
            JOptionPane.showMessageDialog(this,
                    "This offer cannot be " + newStatus.toLowerCase() + ".",
                    "Action Not Allowed",
                    JOptionPane.WARNING_MESSAGE);
            dialog.dispose();
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to " + newStatus.toLowerCase() + " this offer?\n\n" +
                "Company: " + getCompanyName(offer.getCompanyId()) + "\n" +
                "Position: " + getRoleText(offer),
                "Confirm " + newStatus,
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            OfferDAO offerDAO = OfferDAO.getInstance();
            boolean success = offerDAO.updateOfferStatus(offer.getOfferId(), newStatus);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Offer " + newStatus.toLowerCase() + " successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshOffers();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update offer. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Update selected offer status from the main panel
     */
    private void updateSelectedOfferStatus(String newStatus) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an offer first.",
                    "No Offer Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Offer offer = offers.get(selectedRow);
        updateOfferStatus(offer, newStatus, null);
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Dialog", Font.BOLD, 12));
        label.setForeground(new Color(70, 70, 70));
        return label;
    }

    private JLabel createInfoValue(String value) {
        JLabel label = new JLabel(value);
        label.setFont(new Font("Dialog", Font.PLAIN, 12));
        return label;
    }

    private JLabel createStatusValue(Offer offer) {
        String status = getDisplayStatus(offer);
        JLabel label = new JLabel(status);
        label.setFont(new Font("Dialog", Font.BOLD, 12));

        // Color code based on status
        switch (status) {
            case "Accepted":
                label.setForeground(new Color(0, 128, 0));
                break;
            case "Rejected":
                label.setForeground(new Color(178, 34, 34));
                break;
            case "Expired":
                label.setForeground(new Color(128, 128, 128));
                break;
            default:
                label.setForeground(new Color(255, 140, 0));
                break;
        }
        return label;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(ACCENT);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Dialog", Font.PLAIN, 12));
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        return button;
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(100, 100, 100));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Dialog", Font.PLAIN, 11));
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleActionButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Dialog", Font.BOLD, 12));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker()),
                new EmptyBorder(6, 16, 6, 16)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Dialog", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));
        table.getTableHeader().setBackground(ACCENT);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowGrid(true);
        table.setGridColor(BORDER);
        table.setSelectionBackground(new Color(220, 240, 255));
        table.setIntercellSpacing(new Dimension(5, 5));
        table.setRowMargin(2);
    }

    private void resizeTableColumns() {
        table.getColumnModel().getColumn(0).setPreferredWidth(150); // Company
        table.getColumnModel().getColumn(1).setPreferredWidth(180); // Role
        table.getColumnModel().getColumn(2).setPreferredWidth(130); // Offer Date
        table.getColumnModel().getColumn(3).setPreferredWidth(110); // Deadline
        table.getColumnModel().getColumn(4).setPreferredWidth(90);  // Status
    }

    private JPanel createSectionHeader(String title) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(ACCENT);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(40, 40, 40)),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));

        bar.add(titleLabel, BorderLayout.WEST);
        return bar;
    }

    /**
     * Get the current student ID (for external use)
     */
    public int getStudentId() {
        return currentStudent != null ? currentStudent.getId() : -1;
    }

    /**
     * Load offers for a specific student (called from dashboard)
     */
    public void loadOffersForStudent(int studentId) {
        if (currentStudent == null || currentStudent.getId() != studentId) {
            currentStudent = StudentDAO.getInstance().getStudent(studentId);
            if (currentStudent != null) {
                SessionManager.getInstance().setCurrentUser(currentStudent);
            }
        }
        refreshOffers();
    }
}