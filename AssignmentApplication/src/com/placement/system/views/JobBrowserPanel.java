// com.placement.system.views/JobBrowserPanel.java
package com.placement.system.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import com.placement.system.models.User;
import com.placement.system.utils.SessionManager;

public class JobBrowserPanel extends JPanel {
    
    // Color palette matching the system
    private static final Color MAIN_BG = Color.decode("#CFCFCF");
    private static final Color ACCENT  = Color.decode("#545454");
    private static final Color BTN_BG  = Color.decode("#7D7D7D");
    private static final Color ROW_ALT = new Color(0xE2E2E2);
    private static final Font TEXTAREA_FONT = new Font("SansSerif", Font.PLAIN, 12);
    
    // Current student from session
    private StudentProfile currentStudent;
    
    // UI Components
    private JTextField txtSearch = new JTextField();
    private JComboBox<String> cbType = new JComboBox<>();
    private JComboBox<String> cbLocation = new JComboBox<>();
    private JComboBox<String> cbCompany = new JComboBox<>();
    private JComboBox<String> cbTime = new JComboBox<>();
    
    private JLabel lblStatus = new JLabel(" ");
    
    private DefaultTableModel model;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    
    private List<Offer> allOffers = MockData.offers();
    
    // Reference to parent frame for navigation
    private JFrame parentFrame;
    
    public JobBrowserPanel() {
        // Get current student from session
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            // In a real implementation, you'd get these details from the Student model
            currentStudent = new StudentProfile(
                user.getFullName(),
                7.5, // This should come from the actual Student object
                "BSc (Hons) Computer Science", // This should come from the actual Student object
                "Computer Science" // This should come from the actual Student object
            );
        } else {
            // Fallback for testing
            currentStudent = new StudentProfile(
                "Test Student",
                7.5,
                "BSc (Hons) Computer Science",
                "Computer Science"
            );
        }
        
        initializePanel();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(MAIN_BG);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Main content panel with border
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(MAIN_BG);
        center.setBorder(BorderFactory.createLineBorder(ACCENT));
        
        // Top wrapper for header + filters
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(MAIN_BG);
        
        // Section header
        JPanel sectionHeader = new JPanel(new BorderLayout());
        sectionHeader.setBackground(ACCENT);
        JLabel sec = new JLabel("  Recruitment Offers");
        sec.setForeground(Color.WHITE);
        sec.setFont(new Font("SansSerif", Font.BOLD, 13));
        sectionHeader.add(sec, BorderLayout.CENTER);
        
        topSection.add(sectionHeader, BorderLayout.NORTH);
        topSection.add(buildFiltersPanel(), BorderLayout.CENTER);
        
        center.add(topSection, BorderLayout.NORTH);
        
        // Table setup
        String[] cols = {"Company", "Title", "Type", "Salary", "Location", "Deadline", "Eligible"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        
        table = new JTable(model);
        styleTable(table);
        
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        // Custom renderers
        table.getColumnModel().getColumn(6).setCellRenderer(new EligibleRenderer());
        
        AlternateRowRenderer alt = new AlternateRowRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 6) table.getColumnModel().getColumn(i).setCellRenderer(alt);
        }
        
        // Load data
        reloadRows(allOffers);
        
        // Double-click listener
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    openSelectedOffer();
                }
            }
        });
        
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, ACCENT));
        center.add(sp, BorderLayout.CENTER);
        
        // Bottom panel with status and buttons
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(MAIN_BG);
        lblStatus.setBorder(new EmptyBorder(6, 10, 6, 10));
        bottom.add(lblStatus, BorderLayout.WEST);
        
        JButton reset = new JButton("Reset Filters");
        styleButton(reset, 140);
        reset.addActionListener(e -> resetFilters());
        
        JButton viewDetails = new JButton("View Details");
        styleButton(viewDetails, 140);
        viewDetails.addActionListener(e -> openSelectedOffer());
        
        JPanel bottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        bottomRight.setBackground(MAIN_BG);
        bottomRight.add(reset);
        bottomRight.add(viewDetails);
        
        bottom.add(bottomRight, BorderLayout.EAST);
        center.add(bottom, BorderLayout.SOUTH);
        
        add(center, BorderLayout.CENTER);
        
        updateStatus();
        applyFilters();
    }
    
    private JPanel buildFiltersPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(MAIN_BG);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(MAIN_BG);
        
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 0, 8, 12);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 1: Search + Type
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        row.add(label("Search:"), gc);
        
        gc.gridx = 1; gc.gridy = 0; gc.weightx = 1;
        txtSearch.setToolTipText("Company or job title...");
        row.add(txtSearch, gc);
        
        gc.gridx = 2; gc.gridy = 0; gc.weightx = 0;
        row.add(label("Type:"), gc);
        
        gc.gridx = 3; gc.gridy = 0; gc.weightx = 0.5;
        row.add(cbType, gc);
        
        // Row 2: Location + Company
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0;
        row.add(label("Location:"), gc);
        
        gc.gridx = 1; gc.gridy = 1; gc.weightx = 1;
        row.add(cbLocation, gc);
        
        gc.gridx = 2; gc.gridy = 1; gc.weightx = 0;
        row.add(label("Company:"), gc);
        
        gc.gridx = 3; gc.gridy = 1; gc.weightx = 0.5;
        row.add(cbCompany, gc);
        
        // Row 3: Time
        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0;
        row.add(label("Time:"), gc);
        
        gc.gridx = 1; gc.gridy = 2; gc.weightx = 1;
        row.add(cbTime, gc);
        
        fillFiltersFromData();
        
        // Add document listener for search
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });
        
        // Add action listeners for combo boxes
        ActionListener filterListener = e -> applyFilters();
        cbType.addActionListener(filterListener);
        cbLocation.addActionListener(filterListener);
        cbCompany.addActionListener(filterListener);
        cbTime.addActionListener(filterListener);
        
        p.add(row, BorderLayout.CENTER);
        
        JPanel sep = new JPanel();
        sep.setPreferredSize(new Dimension(1, 1));
        sep.setBackground(ACCENT);
        p.add(sep, BorderLayout.SOUTH);
        
        return p;
    }
    
    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.BLACK);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return l;
    }
    
    private void fillFiltersFromData() {
        Set<String> types = allOffers.stream().map(o -> o.type).collect(Collectors.toCollection(TreeSet::new));
        cbType.removeAllItems();
        cbType.addItem("All Types");
        for (String t : types) cbType.addItem(t);
        
        Set<String> locs = allOffers.stream().map(o -> o.location).collect(Collectors.toCollection(TreeSet::new));
        cbLocation.removeAllItems();
        cbLocation.addItem("All Locations");
        for (String loc : locs) cbLocation.addItem(loc);
        
        Set<String> companies = allOffers.stream().map(o -> o.company).collect(Collectors.toCollection(TreeSet::new));
        cbCompany.removeAllItems();
        cbCompany.addItem("All Companies");
        for (String c : companies) cbCompany.addItem(c);
        
        cbTime.removeAllItems();
        cbTime.addItem("Any time");
        cbTime.addItem("Past month");
        cbTime.addItem("Past week");
        cbTime.addItem("Past 24 hours");
    }
    
    private void resetFilters() {
        txtSearch.setText("");
        cbType.setSelectedIndex(0);
        cbLocation.setSelectedIndex(0);
        cbCompany.setSelectedIndex(0);
        cbTime.setSelectedIndex(0);
        applyFilters();
    }
    
    private void reloadRows(List<Offer> offers) {
        model.setRowCount(0);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Offer o : offers) {
            boolean eligible = o.isEligibleFor(currentStudent);
            model.addRow(new Object[]{
                    o.company,
                    o.title,
                    o.type,
                    o.salaryText,
                    o.location,
                    df.format(o.deadline),
                    eligible ? "Yes" : "No"
            });
        }
    }
    
    private void applyFilters() {
        RowFilter<DefaultTableModel, Integer> rf = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                int modelRow = entry.getIdentifier();
                Offer o = allOffers.get(modelRow);
                
                String q = txtSearch.getText().trim().toLowerCase(Locale.ROOT);
                if (!q.isEmpty()) {
                    String hay = (o.company + " " + o.title).toLowerCase(Locale.ROOT);
                    if (!hay.contains(q)) return false;
                }
                
                String typePick = (String) cbType.getSelectedItem();
                if (typePick != null && !typePick.equals("All Types")) {
                    if (!o.type.equals(typePick)) return false;
                }
                
                String locPick = (String) cbLocation.getSelectedItem();
                if (locPick != null && !locPick.equals("All Locations")) {
                    if (!o.location.equals(locPick)) return false;
                }
                
                String compPick = (String) cbCompany.getSelectedItem();
                if (compPick != null && !compPick.equals("All Companies")) {
                    if (!o.company.equals(compPick)) return false;
                }
                
                String timePick = (String) cbTime.getSelectedItem();
                if (timePick != null && !timePick.equals("Any time")) {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime cutoff;
                    switch (timePick) {
                        case "Past month" -> cutoff = now.minusDays(30);
                        case "Past week" -> cutoff = now.minusDays(7);
                        case "Past 24 hours" -> cutoff = now.minusHours(24);
                        default -> cutoff = null;
                    }
                    if (cutoff != null && o.postedAt.isBefore(cutoff)) return false;
                }
                
                return true;
            }
        };
        
        sorter.setRowFilter(rf);
        updateStatus();
    }
    
    private void updateStatus() {
        int shown = table.getRowCount();
        lblStatus.setText(shown + " offer(s) found. Double-click an offer to view details.");
    }
    
    private void openSelectedOffer() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an offer first.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        Offer selected = allOffers.get(modelRow);
        
        showOfferDetailsDialog(selected);
    }
    
    private void showOfferDetailsDialog(Offer offer) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Offer Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(MAIN_BG);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ACCENT);
        JLabel titleLabel = new JLabel("  " + offer.title + " at " + offer.company);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Job Details
        mainPanel.add(createInfoSection("Job Details", new String[][]{
            {"Company:", offer.company},
            {"Title:", offer.title},
            {"Type:", offer.type},
            {"Salary:", offer.salaryText},
            {"Location:", offer.location},
            {"Deadline:", offer.deadline.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))},
            {"Posted:", offer.postedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}
        }));
        
        // Description
        JTextArea descArea = new JTextArea(offer.description);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(TEXTAREA_FONT);
        descArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT), "Description"));
        descScroll.setPreferredSize(new Dimension(650, 100));
        mainPanel.add(descScroll);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Eligibility Criteria
        mainPanel.add(createInfoSection("Eligibility Criteria", new String[][]{
            {"Minimum CGPA:", String.valueOf(offer.minCgpa)},
            {"Accepted Courses:", String.join(", ", offer.acceptedCourses)},
            {"Accepted Branches:", String.join(", ", offer.acceptedBranches)}
        }));
        
        // Your Eligibility
        EligibilityCheck check = offer.checkEligibility(currentStudent);
        JPanel eligPanel = new JPanel(new BorderLayout());
        eligPanel.setBackground(MAIN_BG);
        eligPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT), "Your Eligibility"));
        
        JLabel eligStatus = new JLabel("Status: " + (check.eligible ? "Eligible" : "Not Eligible"));
        eligStatus.setFont(new Font("SansSerif", Font.BOLD, 12));
        eligStatus.setForeground(check.eligible ? new Color(0x0A6E2A) : Color.RED);
        eligStatus.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        JTextArea eligReasons = new JTextArea();
        eligReasons.setEditable(false);
        eligReasons.setLineWrap(true);
        eligReasons.setWrapStyleWord(true);
        eligReasons.setFont(TEXTAREA_FONT);
        eligReasons.setMargin(new Insets(10, 10, 10, 10));
        eligReasons.setText(check.eligible ? "You meet all the criteria!" :
            "You do not meet the following criteria:\n" + String.join("\n", check.reasons));
        
        eligPanel.add(eligStatus, BorderLayout.NORTH);
        eligPanel.add(new JScrollPane(eligReasons), BorderLayout.CENTER);
        mainPanel.add(eligPanel);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(MAIN_BG);
        
        JButton closeBtn = new JButton("Close");
        styleButton(closeBtn, 100);
        closeBtn.addActionListener(e -> dialog.dispose());
        
        JButton applyBtn = new JButton("Apply");
        styleButton(applyBtn, 100);
        applyBtn.addActionListener(e -> {
            if (!check.eligible) {
                JOptionPane.showMessageDialog(dialog,
                    "You are not eligible for this position.",
                    "Cannot Apply",
                    JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Application submitted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });
        
        buttonPanel.add(closeBtn);
        buttonPanel.add(applyBtn);
        mainPanel.add(buttonPanel);
        
        JScrollPane mainScroll = new JScrollPane(mainPanel);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        
        dialog.add(mainScroll, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private JPanel createInfoSection(String title, String[][] data) {
        JPanel panel = new JPanel(new GridLayout(data.length, 2, 10, 5));
        panel.setBackground(MAIN_BG);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT), title));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        for (String[] row : data) {
            JLabel label = new JLabel(row[0]);
            label.setFont(new Font("SansSerif", Font.BOLD, 12));
            
            JLabel value = new JLabel(row[1]);
            value.setFont(new Font("SansSerif", Font.PLAIN, 12));
            
            panel.add(label);
            panel.add(value);
        }
        
        return panel;
    }
    
    // ==================== STATIC UTILITY METHODS ====================
    
    private static void styleTable(JTable table) {
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(ACCENT);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        
        JTableHeader hdr = table.getTableHeader();
        hdr.setBackground(MAIN_BG);
        hdr.setForeground(Color.BLACK);
        hdr.setBorder(BorderFactory.createLineBorder(ACCENT));
        hdr.setFont(new Font("SansSerif", Font.BOLD, 12));
    }
    
    private static void styleButton(JButton b, int fixedWidth) {
        b.setBackground(BTN_BG);
        b.setForeground(Color.BLACK);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(true);
        b.setBorder(BorderFactory.createLineBorder(ACCENT));
        b.setUI(new SolidButtonUI());
        
        if (fixedWidth > 0) b.setPreferredSize(new Dimension(fixedWidth, 32));
        else b.setPreferredSize(new Dimension(160, 32));
    }
    
    // ==================== INNER CLASSES ====================
    
    static class SolidButtonUI extends BasicButtonUI {
        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(b.getBackground());
            g2.fillRect(0, 0, b.getWidth(), b.getHeight());
            g2.dispose();
            super.paint(g, c);
        }
    }
    
    static class AlternateRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus,
                                                      int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (isSelected) {
                setBackground(ACCENT);
                setForeground(Color.WHITE);
            } else {
                setBackground((row % 2 == 0) ? Color.WHITE : ROW_ALT);
                setForeground(Color.BLACK);
            }
            return this;
        }
    }
    
    static class EligibleRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus,
                                                      int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            String s = String.valueOf(value);
            setFont(getFont().deriveFont(Font.BOLD));
            
            if (isSelected) {
                setBackground(ACCENT);
                setForeground(Color.WHITE);
                return this;
            }
            
            setBackground((row % 2 == 0) ? Color.WHITE : ROW_ALT);
            if ("Yes".equalsIgnoreCase(s)) setForeground(new Color(0x0A6E2A));
            else if ("No".equalsIgnoreCase(s)) setForeground(Color.RED);
            else setForeground(Color.BLACK);
            
            return this;
        }
    }
    
    // ==================== DATA MODELS ====================
    
    static class StudentProfile {
        final String name;
        final double cgpa;
        final String course;
        final String branch;
        
        StudentProfile(String name, double cgpa, String course, String branch) {
            this.name = name;
            this.cgpa = cgpa;
            this.course = course;
            this.branch = branch;
        }
    }
    
    static class Offer {
        final String company, title, type, salaryText, location;
        final LocalDate deadline;
        final LocalDateTime postedAt;
        final String description;
        final double minCgpa;
        final List<String> acceptedCourses;
        final List<String> acceptedBranches;
        
        Offer(String company, String title, String type, String salaryText, String location,
              LocalDate deadline, LocalDateTime postedAt,
              String description, double minCgpa, List<String> courses, List<String> branches) {
            this.company = company;
            this.title = title;
            this.type = type;
            this.salaryText = salaryText;
            this.location = location;
            this.deadline = deadline;
            this.postedAt = postedAt;
            this.description = description;
            this.minCgpa = minCgpa;
            this.acceptedCourses = courses;
            this.acceptedBranches = branches;
        }
        
        boolean isEligibleFor(StudentProfile s) { return checkEligibility(s).eligible; }
        
        EligibilityCheck checkEligibility(StudentProfile s) {
            List<String> reasons = new ArrayList<>();
            if (s.cgpa < minCgpa) reasons.add("- Your CGPA (" + s.cgpa + ") is below minimum (" + minCgpa + ")");
            if (!acceptedCourses.isEmpty() && !acceptedCourses.contains(s.course))
                reasons.add("- Your course (" + s.course + ") is not accepted");
            if (!acceptedBranches.isEmpty() && !acceptedBranches.contains(s.branch))
                reasons.add("- Your branch (" + s.branch + ") is not accepted");
            return new EligibilityCheck(reasons.isEmpty(), reasons);
        }
    }
    
    static class EligibilityCheck {
        final boolean eligible;
        final List<String> reasons;
        
        EligibilityCheck(boolean eligible, List<String> reasons) {
            this.eligible = eligible;
            this.reasons = reasons;
        }
    }
    
    // ==================== MOCK DATA ====================
    
    static class MockData {
        static List<Offer> offers() {
            LocalDateTime now = LocalDateTime.now();
            
            return List.of(
                new Offer("MCB Ltd", "Software Engineer", "Full-Time", "Rs 55,000 / month", "Ebène",
                    LocalDate.now().plusDays(25), now.minusDays(2),
                    "Develop and maintain backend services, APIs, and database integrations for banking systems.",
                    7.0, List.of("BSc (Hons) Computer Science", "BSc (Hons) Information Systems"), List.of("Computer Science")),
                    
                new Offer("SBM Bank (Mauritius)", "Data Analyst", "Full-Time", "Rs 50,000 / month", "Port Louis",
                    LocalDate.now().plusDays(30), now.minusDays(10),
                    "Analyze datasets, create reports and dashboards, and support business decision-making.",
                    7.0, List.of("BSc (Hons) Computer Science", "BSc (Hons) Data Science"), List.of("Computer Science", "Data Science")),
                    
                new Offer("Mauritius Telecom", "Network Support Intern", "Internship", "Rs 18,000 / month", "Port Louis",
                    LocalDate.now().plusDays(14), now.minusHours(18),
                    "Assist in network monitoring, troubleshooting, and documentation for telecom infrastructure.",
                    6.5, List.of("BSc (Hons) Computer Science", "BSc (Hons) Networking"), List.of("Computer Science", "Networking")),
                    
                new Offer("Ceridian Mauritius", "Junior Java Developer", "Full-Time", "Rs 48,000 / month", "Moka",
                    LocalDate.now().plusDays(18), now.minusDays(4),
                    "Work on enterprise applications using Java, SQL, and REST APIs. Collaborate with agile teams.",
                    7.0, List.of("BSc (Hons) Computer Science"), List.of("Computer Science")),
                    
                new Offer("Infosys Mauritius", "Frontend Developer", "Full-Time", "Rs 45,000 / month", "Trianon",
                    LocalDate.now().plusDays(22), now.minusDays(3),
                    "Build UI screens using modern web practices. Work with APIs and responsive layouts.",
                    6.8, List.of("BSc (Hons) Computer Science"), List.of("Computer Science"))
            );
        }
    }
}