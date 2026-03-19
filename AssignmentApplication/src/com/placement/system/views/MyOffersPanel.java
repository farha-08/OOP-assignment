// com.placement.system.views/MyOffersPanel.java
package com.placement.system.views;

import com.placement.system.models.Company;
import com.placement.system.models.Offer;
import com.placement.system.utils.CompanyDataStore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
    private final JButton btnStall;

    private int studentId = -1;

    public MyOffersPanel() {
        setLayout(new BorderLayout());
        setBackground(MAIN_BG);

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

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        table = new JTable(model);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(CARD_BG);
        card.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(CARD_BG);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        lblCount = new JLabel("0 offer(s) total.");
        lblCount.setFont(new Font("Dialog", Font.PLAIN, 12));
        lblCount.setBorder(new EmptyBorder(8, 8, 8, 8));
        bottomPanel.add(lblCount, BorderLayout.WEST);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        actionsPanel.setBackground(CARD_BG);

        btnViewDetails = createActionButton("View Details");
        btnAccept = createActionButton("Accept");
        btnReject = createActionButton("Reject");
        btnStall = createActionButton("Stall");

        actionsPanel.add(btnViewDetails);
        actionsPanel.add(btnAccept);
        actionsPanel.add(btnReject);
        actionsPanel.add(btnStall);

        bottomPanel.add(actionsPanel, BorderLayout.EAST);
        card.add(bottomPanel, BorderLayout.SOUTH);

        mainContent.add(card, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        btnViewDetails.addActionListener(e -> viewSelectedOfferDetails());
        btnAccept.addActionListener(e -> updateSelectedOfferStatus("Accepted"));
        btnReject.addActionListener(e -> updateSelectedOfferStatus("Rejected"));
        btnStall.addActionListener(e -> updateSelectedOfferStatus("Stalled"));
    }

    /**
     * Call this when the student logs in / dashboard loads.
     */
    public void loadOffersForStudent(int studentId) {
        this.studentId = studentId;
        refreshOffers();
    }

    /**
     * Refresh offers table.
     */
    public void refreshOffers() {
        model.setRowCount(0);

        if (studentId == -1) {
            lblCount.setText("0 offer(s) total.");
            return;
        }

        List<Offer> offers = MockOfferService.getOffersForStudent(studentId);

        for (Offer offer : offers) {
            model.addRow(new Object[]{
                    getCompanyName(offer.getCompanyId()),
                    getRoleText(offer),
                    formatDateTime(offer.getOfferDate()),
                    formatDate(offer.getAcceptanceDeadline()),
                    getDisplayStatus(offer)
            });
        }

        lblCount.setText(offers.size() + " offer(s) total.");
        resizeTableColumns();
    }

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

        Offer offer = MockOfferService.getOffersForStudent(studentId).get(selectedRow);

        JTextArea textArea = new JTextArea(buildOfferDetailsText(offer));
        textArea.setEditable(false);
        textArea.setFont(new Font("Dialog", Font.PLAIN, 13));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(Color.WHITE);
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(470, 280));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Offer Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

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

        Offer offer = MockOfferService.getOffersForStudent(studentId).get(selectedRow);

        if (offer.isExpired()) {
            offer.setStatus("Expired");
            refreshOffers();

            JOptionPane.showMessageDialog(
                    this,
                    "This offer has already expired.",
                    "Offer Expired",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if ("Accepted".equalsIgnoreCase(offer.getStatus())) {
            JOptionPane.showMessageDialog(
                    this,
                    "This offer has already been accepted.",
                    "Action Not Allowed",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if ("Rejected".equalsIgnoreCase(offer.getStatus())) {
            JOptionPane.showMessageDialog(
                    this,
                    "This offer has already been rejected.",
                    "Action Not Allowed",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to mark this offer as " + newStatus + "?",
                "Confirm Action",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            offer.setStatus(newStatus);
            refreshOffers();

            JOptionPane.showMessageDialog(
                    this,
                    "Offer updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private String buildOfferDetailsText(Offer offer) {
        StringBuilder sb = new StringBuilder();

        sb.append("Company: ").append(getCompanyName(offer.getCompanyId())).append("\n");
        sb.append("Role: ").append(getRoleText(offer)).append("\n");
        sb.append("Offer ID: ").append(offer.getOfferId()).append("\n");
        sb.append("Application ID: ").append(offer.getApplicationId()).append("\n");
        sb.append("Offer Date: ").append(formatDateTime(offer.getOfferDate())).append("\n");
        sb.append("Acceptance Deadline: ").append(formatDate(offer.getAcceptanceDeadline())).append("\n");
        sb.append("Status: ").append(getDisplayStatus(offer)).append("\n");
        sb.append("Offer Letter Path: ")
                .append(offer.getOfferLetterPath() == null || offer.getOfferLetterPath().isBlank()
                        ? "N/A"
                        : offer.getOfferLetterPath())
                .append("\n\n");
        sb.append("Offer Details:\n");
        sb.append(offer.getOfferDetails() == null || offer.getOfferDetails().isBlank()
                ? "No details available."
                : offer.getOfferDetails());

        return sb.toString();
    }

    private String getCompanyName(int companyId) {
        Company company = CompanyDataStore.getInstance().getCompany(companyId);
        return company != null ? company.getCompanyName() : "Unknown Company";
    }

    /**
     * Temporary role text until you connect Job/Application mock data.
     */
    private String getRoleText(Offer offer) {
        return "Application #" + offer.getApplicationId();
    }

    private String getDisplayStatus(Offer offer) {
        if (offer.isExpired() && !"Expired".equalsIgnoreCase(offer.getStatus())) {
            return "Expired";
        }
        return offer.getStatus() == null ? "" : offer.getStatus();
    }

    private String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
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
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));

        bar.add(titleLabel, BorderLayout.WEST);
        return bar;
    }

    // ==================== TEMP MOCK SERVICE ====================

    public static class MockOfferService {
        private static final List<Offer> mockOffers = new ArrayList<>();

        static {
            mockOffers.add(new Offer(
                    1,
                    101,
                    1,
                    1,
                    LocalDateTime.now().minusDays(1),
                    "We are pleased to offer you a placement opportunity at TechNova Ltd. "
                            + "Please review the terms and conditions and respond before the deadline.",
                    "offers/offer_1.pdf",
                    LocalDate.now().plusDays(5),
                    "Pending"
            ));

            mockOffers.add(new Offer(
                    2,
                    102,
                    2,
                    1,
                    LocalDateTime.now().minusDays(3),
                    "MCB Ltd is happy to extend you an offer for placement. "
                            + "Kindly confirm your decision before the deadline.",
                    "offers/offer_2.pdf",
                    LocalDate.now().plusDays(2),
                    "Pending"
            ));

            mockOffers.add(new Offer(
                    3,
                    103,
                    3,
                    2,
                    LocalDateTime.now().minusDays(4),
                    "Startup Inc offers you a placement opportunity subject to the attached conditions.",
                    "offers/offer_3.pdf",
                    LocalDate.now().plusDays(4),
                    "Pending"
            ));
        }

        public static List<Offer> getOffersForStudent(int studentId) {
            List<Offer> result = new ArrayList<>();
            for (Offer offer : mockOffers) {
                if (offer.getStudentId() == studentId) {
                    result.add(offer);
                }
            }
            return result;
        }
    }
}