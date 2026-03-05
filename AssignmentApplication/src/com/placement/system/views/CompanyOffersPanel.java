// com.placement.system.views/CompanyOffersPanel.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

public class CompanyOffersPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    // Color scheme matching their design
    private static final Color MAIN_BG = new Color(0xCF, 0xCF, 0xCF);      // #CFCFCF
    private static final Color ACCENT_DARK = new Color(0x54, 0x54, 0x54);  // #545454
    private static final Color ACCENT_BUTTON = new Color(0x7D, 0x7D, 0x7D); // #7D7D7D

    private static final Color STATUS_OPEN = new Color(0, 180, 80);
    private static final Color STATUS_CLOSED = new Color(120, 120, 120); // grey
    private static final DateTimeFormatter DEADLINE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private CompanyDataStore store;
    private JPanel cardsGrid;
    private JPanel wrapper;
    private Runnable onCreateOfferCallback;
    
    public CompanyOffersPanel(Runnable onCreateOfferCallback) {
        this.onCreateOfferCallback = onCreateOfferCallback;
        this.store = CompanyDataStore.getInstance(); // Assuming singleton pattern
        
        setLayout(new BorderLayout(0, 12));
        setBackground(MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header with Create Offer button
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(MAIN_BG);
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(MAIN_BG);
        
        JLabel title = new JLabel("My Job Offers");
        title.setForeground(ACCENT_DARK);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        
        JLabel sub = new JLabel("Manage and review your posted job vacancies");
        sub.setForeground(new Color(100, 100, 100));
        
        titlePanel.add(title);
        titlePanel.add(sub);
        
        JButton btnCreateOffer = new JButton("+ Create New Offer");
        styleButton(btnCreateOffer);
        btnCreateOffer.addActionListener(e -> {
            if (onCreateOfferCallback != null) {
                onCreateOfferCallback.run();
            }
        });
        
        header.add(titlePanel, BorderLayout.WEST);
        header.add(btnCreateOffer, BorderLayout.EAST);
        
        add(header, BorderLayout.NORTH);
        
        // Grid area inside scroll pane
        cardsGrid = new JPanel(new GridLayout(0, 3, 15, 15));
        cardsGrid.setBackground(MAIN_BG);
        
        wrapper = new JPanel(new BorderLayout());
        wrapper.add(cardsGrid, BorderLayout.NORTH);
        wrapper.setBackground(MAIN_BG);
        
        JScrollPane sp = new JScrollPane(wrapper);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setBorder(BorderFactory.createEmptyBorder());
        add(sp, BorderLayout.CENTER);
        
        // Initial refresh
        refresh();
    }
    
    public void refresh() {
        cardsGrid.removeAll();
        
        List<JobOffer> offers = store.getOffers();
        
        if (offers.isEmpty()) {
            cardsGrid.setLayout(new GridLayout(1, 1));
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setBackground(Color.WHITE);
            emptyPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)
            ));
            
            JLabel emptyLabel = new JLabel("No job offers yet. Click 'Create New Offer' to add one.");
            emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyPanel.add(emptyLabel, BorderLayout.CENTER);
            
            cardsGrid.add(emptyPanel);
        } else {
            cardsGrid.setLayout(new GridLayout(0, 3, 15, 15));
            for (JobOffer offer : offers) {
                cardsGrid.add(buildCard(offer));
            }
        }
        
        cardsGrid.revalidate();
        cardsGrid.repaint();
        wrapper.revalidate();
        wrapper.repaint();
    }
    
    private JPanel buildCard(JobOffer offer) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        
        // Top row: title + status badge
        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel(offer.getTitle());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        
        boolean closed = isOfferClosed(offer);

        JLabel badge = new JLabel(closed ? "Closed" : "Open");
        badge.setOpaque(true);
        badge.setBackground(closed ? STATUS_CLOSED : STATUS_OPEN);
        badge.setForeground(Color.WHITE);
        badge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        
        top.add(title, BorderLayout.WEST);
        top.add(badge, BorderLayout.EAST);
        
        // Department
        JLabel dept = new JLabel(offer.getDepartment());
        dept.setForeground(new Color(80, 80, 80));
        
        JPanel topWrap = new JPanel(new GridLayout(2, 1, 0, 4));
        topWrap.add(top);
        topWrap.add(dept);
        
        card.add(topWrap, BorderLayout.NORTH);
        
        // Middle info
        JPanel info = new JPanel(new GridLayout(2, 2, 10, 6));
        info.add(new JLabel("📍 " + offer.getLocation()));
        info.add(new JLabel("🕒 " + offer.getType()));
        info.add(new JLabel("👥 " + offer.getApplicants() + " applicant(s)"));
        info.add(new JLabel("📅 " + offer.getDeadline()));
        card.add(info, BorderLayout.CENTER);
        
        // Skills chips
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        String[] skills = offer.getSkills();
        int show = Math.min(skills.length, 4);
        for (int i = 0; i < show; i++) {
            chips.add(createChip(skills[i]));
        }
        card.add(chips, BorderLayout.SOUTH);
        
        // View details button
        JButton btn = new JButton("View Details");
        btn.setBackground(ACCENT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        btn.addActionListener(e -> showDetails(offer));
        
        JPanel btnWrap = new JPanel(new BorderLayout());
        btnWrap.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        btnWrap.add(btn, BorderLayout.CENTER);
        
        JPanel outer = new JPanel(new BorderLayout());
        outer.add(card, BorderLayout.CENTER);
        outer.add(btnWrap, BorderLayout.SOUTH);
        outer.setBackground(MAIN_BG);
        outer.setPreferredSize(new Dimension(320, 220));
        
        return outer;
    }
    
    private JLabel createChip(String text) {
        JLabel c = new JLabel(text);
        c.setOpaque(true);
        c.setBackground(new Color(230, 235, 245));
        c.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        return c;
    }
    private boolean isOfferClosed(JobOffer offer) {
    try {
        LocalDate deadline = LocalDate.parse(offer.getDeadline().trim(), DEADLINE_FMT);
        return LocalDate.now().isAfter(deadline); // after deadline => closed
    } catch (Exception e) {
        // If deadline format is wrong, keep it OPEN (safe default)
        return false;
    }
}
    
    private void showDetails(JobOffer offer) {
        // Parent window (so dialog centers properly)
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        
        JDialog dialog = new JDialog(owner, "Job Offer Details", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(880, 650);
        dialog.setLocationRelativeTo(this);
        
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        root.setBackground(MAIN_BG);
        
        // ================= HEADER (Title + badge + close) =================
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(MAIN_BG);
        
        JPanel left = new JPanel(new GridLayout(2, 1, 0, 4));
        left.setBackground(MAIN_BG);
        
        JLabel lblTitle = new JLabel(offer.getTitle());
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 22f));
        lblTitle.setForeground(ACCENT_DARK);
        
        JLabel lblDept = new JLabel(offer.getDepartment());
        lblDept.setForeground(new Color(90, 90, 90));
        
        left.add(lblTitle);
        left.add(lblDept);
        
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(MAIN_BG);
        
        boolean closed = isOfferClosed(offer);

        JLabel badge = new JLabel(closed ? "Closed" : "Open");
        badge.setOpaque(true);
        badge.setBackground(closed ? STATUS_CLOSED : STATUS_OPEN);
        badge.setForeground(Color.WHITE);
        badge.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        
        JButton btnClose = new JButton("✕");
        btnClose.setFocusPainted(false);
        btnClose.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btnClose.addActionListener(e -> dialog.dispose());
        
        right.add(badge);
        right.add(btnClose);
        
        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        
        root.add(header, BorderLayout.NORTH);
        
        // ================= TOP INFO ROW =================
        JPanel info = new JPanel(new GridLayout(3, 2, 14, 8));
        info.setBackground(Color.WHITE);
        info.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        
        info.add(createInfoLabel(offer.getLocation()));
        info.add(createInfoLabel(offer.getType()));
        info.add(createInfoLabel(offer.getSalary()));
        info.add(createInfoLabel(offer.getPositions() + " position(s)"));
        info.add(createInfoLabel("Deadline: " + offer.getDeadline()));
        
        String created = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        info.add(createInfoLabel("Created: " + created));
        
        root.add(info, BorderLayout.CENTER);
        
        // ================= MAIN CONTENT (scroll) =================
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setAlignmentX(LEFT_ALIGNMENT);
        content.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        content.setBackground(MAIN_BG);
        
        // ---- Description
        content.add(createSectionTitle("Description"));
        content.add(createSectionText(offer.getDescription()));
        content.add(Box.createVerticalStrut(12));
        
        // ---- Skills
        content.add(createSectionTitle("Required Skills"));
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        chips.setAlignmentX(LEFT_ALIGNMENT);
        chips.setBackground(MAIN_BG);
        
        String[] skills = offer.getSkills();
        for (String skill : skills) {
            if (skill != null && !skill.trim().isEmpty()) {
                chips.add(createSkillChip(skill.trim()));
            }
        }
        content.add(chips);
        content.add(Box.createVerticalStrut(12));
        
        // ---- Qualifications
        content.add(createSectionTitle("Qualifications"));
        content.add(createSectionText(offer.getQualifications()));
        content.add(Box.createVerticalStrut(12));
        
        // ---- Applicants table
        content.add(createSectionTitle("Applicants"));
        
        String[] cols = {"Student", "Year", "University", "Applied", "Status", "Actions"};
        Object[][] rows = {
            {"Amahle Zungu", "Year 3", "University of Pretoria", "25 Jan 2026", "Pending", "Actions"},
            {"Lerato Phiri", "Year 2", "University of Pretoria", "28 Jan 2026", "Pending", "Actions"},
            {"James Mokoena", "Year 3", "University of Pretoria", "05 Feb 2026", "Accepted", "Actions"}
        };
        
        DefaultTableModel model = new DefaultTableModel(rows, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        JTable table = new JTable(model);
        styleTable(table);
        
        // Popup menu for actions
        JPopupMenu menu = new JPopupMenu();
        JMenuItem miShortlist = new JMenuItem("Shortlist");
        JMenuItem miAccept = new JMenuItem("Accept");
        JMenuItem miReject = new JMenuItem("Reject");
        
        menu.add(miShortlist);
        menu.add(miAccept);
        menu.addSeparator();
        menu.add(miReject);
        
        int[] currentRow = {-1};
        
        miShortlist.addActionListener(ev -> {
            if (currentRow[0] >= 0) {
                model.setValueAt("Shortlisted", currentRow[0], 4);
            }
        });
        
        miAccept.addActionListener(ev -> {
            if (currentRow[0] >= 0) {
                model.setValueAt("Accepted", currentRow[0], 4);
            }
        });
        
        miReject.addActionListener(ev -> {
            if (currentRow[0] >= 0) {
                model.setValueAt("Rejected", currentRow[0], 4);
            }
        });
        
        // Mouse listener for actions column
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col < 0) return;
                
                int actionsCol = table.getColumnCount() - 1;
                if (col == actionsCol) {
                    table.setRowSelectionInterval(row, row);
                    currentRow[0] = row;
                    menu.show(table, e.getX(), e.getY());
                }
            }
        });
        
        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(820, 150));
        sp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        content.add(sp);
        
        JScrollPane mainScroll = new JScrollPane(content);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.setBorder(BorderFactory.createEmptyBorder());
        
        root.add(mainScroll, BorderLayout.SOUTH);
        
        dialog.add(root, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text == null || text.isEmpty() ? "-" : text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return label;
    }
    
    private JLabel createSectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 14f));
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setForeground(ACCENT_DARK);
        return l;
    }
    
    private JLabel createSectionText(String text) {
        String safe = (text == null || text.trim().isEmpty()) ? "-" : text.trim();
        JLabel l = new JLabel("<html><div style='width:780px;'>" + escapeHtml(safe) + "</div></html>");
        l.setForeground(new Color(70, 70, 70));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }
    
    private JLabel createSkillChip(String text) {
        JLabel c = new JLabel(text);
        c.setOpaque(true);
        c.setBackground(new Color(230, 235, 245));
        c.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        return c;
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
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    
    private void styleTable(JTable table) {
        table.setRowHeight(24);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(ACCENT_DARK);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setSelectionBackground(new Color(230, 240, 255));
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(140); // Student
        table.getColumnModel().getColumn(1).setPreferredWidth(60);  // Year
        table.getColumnModel().getColumn(2).setPreferredWidth(180); // University
        table.getColumnModel().getColumn(3).setPreferredWidth(90);  // Applied
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Status
        table.getColumnModel().getColumn(5).setPreferredWidth(90);  // Actions
    }
    
    private String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
    
    // ==================== DATA STORE (Placeholder - replace with your actual data layer) ====================
    public static class CompanyDataStore {
        private static CompanyDataStore instance;
        private List<JobOffer> offers = new ArrayList<>();
        
        private CompanyDataStore() {
            // Add some sample data
            offers.add(new JobOffer(
                "Software Engineer", "Engineering", "Ebène", "Full-time",
                "Rs 55,000/month", 2, "31/12/2024",
                "Develop and maintain software applications...",
                "Bachelor's in Computer Science or related field",
                new String[]{"Java", "Spring", "SQL", "Git"}, 3
            ));
        }
        
        public static CompanyDataStore getInstance() {
            if (instance == null) {
                instance = new CompanyDataStore();
            }
            return instance;
        }
        
        public List<JobOffer> getOffers() {
            return offers;
        }
        
        public void addOffer(JobOffer offer) {
            offers.add(offer);
        }
    }
    
    // ==================== JOB OFFER MODEL ====================
    public static class JobOffer {
        private String title;
        private String department;
        private String location;
        private String type;
        private String salary;
        private int positions;
        private String deadline;
        private String description;
        private String qualifications;
        private String[] skills;
        private int applicants;
        
        public JobOffer(String title, String department, String location, String type,
                       String salary, int positions, String deadline, String description,
                       String qualifications, String[] skills, int applicants) {
            this.title = title;
            this.department = department;
            this.location = location;
            this.type = type;
            this.salary = salary;
            this.positions = positions;
            this.deadline = deadline;
            this.description = description;
            this.qualifications = qualifications;
            this.skills = skills;
            this.applicants = applicants;
        }
        
        // Getters
        public String getTitle() { return title; }
        public String getDepartment() { return department; }
        public String getLocation() { return location; }
        public String getType() { return type; }
        public String getSalary() { return salary; }
        public int getPositions() { return positions; }
        public String getDeadline() { return deadline; }
        public String getDescription() { return description; }
        public String getQualifications() { return qualifications; }
        public String[] getSkills() { return skills; }
        public int getApplicants() { return applicants; }
    }
}