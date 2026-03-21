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
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import com.placement.system.models.Job;
import com.placement.system.models.Application;
import com.placement.system.models.Company;
import com.placement.system.models.User;
import com.placement.system.models.Student;
import com.placement.system.utils.SessionManager;
import com.placement.system.dao.JobDAO;
import com.placement.system.dao.ApplicationDAO;
import com.placement.system.dao.CompanyDAO;
import com.placement.system.dao.StudentDAO;

public class CompanyOffersPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    // Color scheme matching their design
    private static final Color MAIN_BG = new Color(0xCF, 0xCF, 0xCF);      // #CFCFCF
    private static final Color ACCENT_DARK = new Color(0x54, 0x54, 0x54);  // #545454
    private static final Color ACCENT_BUTTON = new Color(0x7D, 0x7D, 0x7D); // #7D7D7D
    private static final Color STATUS_OPEN = new Color(0, 180, 80);
    private static final Color STATUS_CLOSED = new Color(120, 120, 120);
    private static final Color ACCENT = new Color(0x54, 0x54, 0x54);
    
    private static final DateTimeFormatter DEADLINE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_DEADLINE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private Company currentCompany;
    private List<Job> companyJobs;
    private Map<Integer, Integer> applicationCountCache = new HashMap<>();
    private Runnable onCreateOfferCallback;
    
    // Store references to components for rebuilding
    private JPanel cardsGrid;
    private JPanel wrapper;
    private JScrollPane scrollPane;
    
    public CompanyOffersPanel(Runnable onCreateOfferCallback) {
        this.onCreateOfferCallback = onCreateOfferCallback;
        
        setLayout(new BorderLayout(0, 12));
        setBackground(MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Load current company
        loadCurrentCompany();
        
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
        stylePrimary(btnCreateOffer);
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
        
        scrollPane = new JScrollPane(wrapper);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
        
        // Initial refresh
        refresh();
    }
    
    /**
     * Load current company from session/database
     */
    private void loadCurrentCompany() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            if (user instanceof Company) {
                this.currentCompany = (Company) user;
            } else {
                // Load from DAO if not Company instance
                this.currentCompany = CompanyDAO.getInstance().getCompany(user.getId());
                if (this.currentCompany != null) {
                    SessionManager.getInstance().setCurrentUser(this.currentCompany);
                }
            }
        }
    }
    
    /**
     * Refresh the job offers panel
     */
    public void refresh() {
        if (currentCompany == null) {
            loadCurrentCompany();
            if (currentCompany == null) {
                return;
            }
        }
        
        // Fetch jobs for this company from database
        JobDAO jobDAO = JobDAO.getInstance();
        companyJobs = jobDAO.getJobsByCompany(currentCompany.getId());
        
        // Pre-cache application counts for each job
        cacheApplicationCounts();
        
        // Rebuild the grid
        rebuildGrid();
    }
    
    /**
     * Cache application counts for all company jobs
     */
    private void cacheApplicationCounts() {
        ApplicationDAO appDAO = ApplicationDAO.getInstance();
        for (Job job : companyJobs) {
            int count = appDAO.getApplicationCountForJob(job.getJobId());
            applicationCountCache.put(job.getJobId(), count);
        }
    }
    
    /**
     * Get application count for a job
     */
    private int getApplicationCount(Job job) {
        return applicationCountCache.getOrDefault(job.getJobId(), 0);
    }
    
    /**
     * Check if a job is closed (deadline passed)
     */
    private boolean isJobClosed(Job job) {
        if (job.getApplicationDeadline() == null) return false;
        return LocalDate.now().isAfter(job.getApplicationDeadline());
    }
    
    /**
     * Format deadline for display
     */
    private String formatDeadline(LocalDate deadline) {
        if (deadline == null) return "N/A";
        return deadline.format(DISPLAY_DEADLINE_FMT);
    }
    
    /**
     * Rebuild the grid of job cards
     */
    private void rebuildGrid() {
        // Clear existing cards
        cardsGrid.removeAll();
        
        if (companyJobs == null || companyJobs.isEmpty()) {
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
            for (Job job : companyJobs) {
                cardsGrid.add(buildCard(job));
            }
        }
        
        // Refresh the UI
        cardsGrid.revalidate();
        cardsGrid.repaint();
        wrapper.revalidate();
        wrapper.repaint();
    }
    
    /**
     * Build a card for a single job
     */
    private JPanel buildCard(Job job) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        
        boolean closed = isJobClosed(job);
        
        // Top row: title + status badge
        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel(job.getJobTitle());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        
        JLabel badge = new JLabel(closed ? "Closed" : "Open");
        badge.setOpaque(true);
        badge.setBackground(closed ? STATUS_CLOSED : STATUS_OPEN);
        badge.setForeground(Color.WHITE);
        badge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        
        top.add(title, BorderLayout.WEST);
        top.add(badge, BorderLayout.EAST);
        
        // Department
        JLabel dept = new JLabel(job.getDepartment() != null ? job.getDepartment() : "General");
        dept.setForeground(new Color(80, 80, 80));
        
        JPanel topWrap = new JPanel(new GridLayout(2, 1, 0, 4));
        topWrap.add(top);
        topWrap.add(dept);
        
        card.add(topWrap, BorderLayout.NORTH);
        
        // Middle info
        JPanel info = new JPanel(new GridLayout(2, 2, 10, 6));
        info.add(new JLabel(job.getLocation() != null ? job.getLocation() : "TBD"));
        info.add(new JLabel(job.getEmploymentType() != null ? job.getEmploymentType() : "N/A"));
        info.add(new JLabel(getApplicationCount(job) + " applicant(s)"));
        info.add(new JLabel(formatDeadline(job.getApplicationDeadline())));
        card.add(info, BorderLayout.CENTER);
        
        // View details button
        JButton btn = new JButton("View Details");
        btn.setBackground(ACCENT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        btn.addActionListener(e -> showJobDetails(job));
        
        JPanel btnWrap = new JPanel(new BorderLayout());
        btnWrap.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        btnWrap.add(btn, BorderLayout.CENTER);
        
        JPanel outer = new JPanel(new BorderLayout());
        outer.add(card, BorderLayout.CENTER);
        outer.add(btnWrap, BorderLayout.SOUTH);
        outer.setBackground(MAIN_BG);
        outer.setPreferredSize(new Dimension(320, 180));
        
        return outer;
    }
    
    /**
     * Show job details dialog with applicants
     */
    private void showJobDetails(Job job) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        
        JDialog dialog = new JDialog(owner, "Job Offer Details", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(900, 700);
        dialog.setLocationRelativeTo(this);
        
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        root.setBackground(MAIN_BG);
        
        // ================= HEADER =================
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(MAIN_BG);
        
        JPanel left = new JPanel(new GridLayout(2, 1, 0, 4));
        left.setBackground(MAIN_BG);
        
        JLabel lblTitle = new JLabel(job.getJobTitle());
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 22f));
        lblTitle.setForeground(ACCENT_DARK);
        
        JLabel lblDept = new JLabel(job.getDepartment() != null ? job.getDepartment() : "General");
        lblDept.setForeground(new Color(90, 90, 90));
        
        left.add(lblTitle);
        left.add(lblDept);
        
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(MAIN_BG);
        
        boolean closed = isJobClosed(job);
        JLabel badge = new JLabel(closed ? "Closed" : "Open");
        badge.setOpaque(true);
        badge.setBackground(closed ? STATUS_CLOSED : STATUS_OPEN);
        badge.setForeground(Color.WHITE);
        badge.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        
        right.add(badge);
        
        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        
        root.add(header, BorderLayout.NORTH);
        
     // ================= TOP INFO ROW (COMPACT - FLOW LAYOUT) =================
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);
        info.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        // First row of info
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        row1.setBackground(Color.WHITE);
        row1.add(createInfoPair("Location:", job.getLocation() != null ? job.getLocation() : "TBD"));
        row1.add(createInfoPair("Type:", job.getEmploymentType() != null ? job.getEmploymentType() : "N/A"));
        row1.add(createInfoPair("Salary:", job.getSalaryRange() != null ? job.getSalaryRange() : "Negotiable"));

        // Second row of info
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        row2.setBackground(Color.WHITE);
        row2.add(createInfoPair("Vacancies:", String.valueOf(job.getVacancies())));
        row2.add(createInfoPair("Deadline:", formatDeadline(job.getApplicationDeadline())));
        row2.add(createInfoPair("Minimum CGPA:", String.valueOf(job.getMinCgpa())));

        info.add(row1);
        info.add(Box.createVerticalStrut(5));
        info.add(row2);

        root.add(info, BorderLayout.CENTER);
        
        // ================= MAIN CONTENT (scroll) =================
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setAlignmentX(LEFT_ALIGNMENT);
        content.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        content.setBackground(MAIN_BG);
        
        // Description
        content.add(createSectionTitle("Description"));
        content.add(createSectionText(job.getDescription()));
        content.add(Box.createVerticalStrut(12));
        
        // Applicants table
        content.add(createSectionTitle("Applicants"));
        
        String[] cols = {"Student Name", "Course", "CGPA", "Applied Date", "Status", "Actions"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        
        // Load actual applicants from database
        ApplicationDAO appDAO = ApplicationDAO.getInstance();
        StudentDAO studentDAO = StudentDAO.getInstance();
        List<Application> applications = appDAO.getApplicationsByJob(job.getJobId());
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        
        for (Application app : applications) {
            Student student = studentDAO.getStudent(app.getStudentId());
            String studentName = student != null ? student.getFullName() : "Unknown";
            String course = student != null ? student.getCourse() : "N/A";
            double cgpa = student != null ? student.getCgpa() : 0;
            String appliedDate = app.getApplicationDate().format(dateFormatter);
            
            model.addRow(new Object[]{
                studentName,
                course,
                cgpa,
                appliedDate,
                app.getStatus(),
                "Actions"
            });
        }
        
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
        int[] currentAppId = {-1};
        
        miShortlist.addActionListener(ev -> {
            if (currentRow[0] >= 0 && currentAppId[0] >= 0) {
                appDAO.updateApplicationStatus(currentAppId[0], "Shortlisted");
                model.setValueAt("Shortlisted", currentRow[0], 4);
                JOptionPane.showMessageDialog(dialog, "Applicant shortlisted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        miAccept.addActionListener(ev -> {
            if (currentRow[0] >= 0 && currentAppId[0] >= 0) {
                appDAO.updateApplicationStatus(currentAppId[0], "Accepted");
                model.setValueAt("Accepted", currentRow[0], 4);
                JOptionPane.showMessageDialog(dialog, "Applicant accepted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        miReject.addActionListener(ev -> {
            if (currentRow[0] >= 0 && currentAppId[0] >= 0) {
                appDAO.updateApplicationStatus(currentAppId[0], "Rejected");
                model.setValueAt("Rejected", currentRow[0], 4);
                JOptionPane.showMessageDialog(dialog, "Applicant rejected.", "Success", JOptionPane.INFORMATION_MESSAGE);
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
                    // Store application ID
                    if (row < applications.size()) {
                        currentAppId[0] = applications.get(row).getApplicationId();
                    }
                    menu.show(table, e.getX(), e.getY());
                }
            }
        });
        
        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(850, 200));
        sp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        content.add(sp);
        
        JScrollPane mainScroll = new JScrollPane(content);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.setBorder(BorderFactory.createEmptyBorder());
        
        root.add(mainScroll, BorderLayout.SOUTH);
        
        dialog.add(root, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private JPanel createInfoPair(String label, String value) {
        JPanel pair = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pair.setBackground(Color.WHITE);
        
        JLabel labelField = new JLabel(label);
        labelField.setFont(new Font("SansSerif", Font.BOLD, 12));
        labelField.setForeground(new Color(70, 70, 70));
        
        JLabel valueField = new JLabel(value);
        valueField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        valueField.setForeground(Color.BLACK);
        
        pair.add(labelField);
        pair.add(valueField);
        
        return pair;
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
    
    private void stylePrimary(JButton b) {
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // border same as fill colour for solid look
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT),
                BorderFactory.createEmptyBorder(9, 16, 9, 16)
        ));
        b.setBackground(ACCENT);
        b.setForeground(Color.WHITE);
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
        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(ACCENT_DARK);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setSelectionBackground(new Color(230, 240, 255));
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(150); // Student Name
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Course
        table.getColumnModel().getColumn(2).setPreferredWidth(60);  // CGPA
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Applied Date
        table.getColumnModel().getColumn(4).setPreferredWidth(90);  // Status
        table.getColumnModel().getColumn(5).setPreferredWidth(80);  // Actions
    }
    
    private String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}