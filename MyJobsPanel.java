package placementportal.company.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MyJobsPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final CompanyDataStore store;
    private JPanel cardsGrid; // re-built on refresh
    private JPanel wrapper;//for grid not to stretch
    private static final java.awt.Color MAIN_BG = new java.awt.Color(0xCF, 0xCF, 0xCF);
    private static final java.awt.Color ACCENT_DARK = new java.awt.Color(0x54, 0x54, 0x54);
    private static final java.awt.Color ACCENT_BUTTON = new java.awt.Color(0x7D, 0x7D, 0x7D);
    public MyJobsPanel(CompanyDataStore store) {
        this.store = store;

        setLayout(new BorderLayout(0, 12));
        setBackground(MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1));
        JLabel title = new JLabel("My Job Offers");
        title.setForeground(ACCENT_DARK);
        title.setFont(title.getFont().deriveFont(java.awt.Font.BOLD, 22f));
        JLabel sub = new JLabel("Manage and review your posted job vacancies");
        sub.setForeground(new java.awt.Color(100, 100, 100));
        header.add(title);
        header.add(sub);
        header.setBackground(MAIN_BG);        

        add(header, BorderLayout.NORTH);

        // Grid area inside scroll pane
        cardsGrid = new JPanel(new GridLayout(0, 3, 15, 15));
        cardsGrid.setBackground(MAIN_BG);
        
        wrapper = new JPanel(new BorderLayout());
        wrapper.add(cardsGrid, BorderLayout.NORTH);  
        wrapper.setBackground(MAIN_BG);

        JScrollPane sp = new JScrollPane(wrapper);
        sp.getVerticalScrollBar().setUnitIncrement(16); 
        add(sp, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        cardsGrid.removeAll();

        if (store.getOffers().isEmpty()) {
            JPanel empty = new JPanel(new FlowLayout(FlowLayout.LEFT));
            empty.add(new JLabel("No job offers yet. Click 'Create Job' to add one."));
            cardsGrid.setLayout(new GridLayout(1, 1));
            cardsGrid.add(empty);
        } else {
            cardsGrid.setLayout(new GridLayout(0, 3, 15, 15));
            for (JobOffer o : store.getOffers()) {
                cardsGrid.add(buildCard(o));
            }
        }

        cardsGrid.revalidate();
        cardsGrid.repaint();
        wrapper.revalidate();
        wrapper.repaint();
    }

    private JPanel buildCard(JobOffer o) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(java.awt.Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(java.awt.Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        // top row: title + "Open" badge
        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel(o.getTitle());
        title.setFont(title.getFont().deriveFont(java.awt.Font.BOLD, 14f));

        JLabel badge = new JLabel("Open");
        badge.setOpaque(true);
        badge.setBackground(new java.awt.Color(0, 180, 80));
        badge.setForeground(java.awt.Color.WHITE);
        badge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        top.add(title, BorderLayout.WEST);
        top.add(badge, BorderLayout.EAST);

        // dept
        JLabel dept = new JLabel(o.getDepartment());
        dept.setForeground(new java.awt.Color(80, 80, 80));

        JPanel topWrap = new JPanel(new GridLayout(2, 1, 0, 4));
        topWrap.add(top);
        topWrap.add(dept);

        card.add(topWrap, BorderLayout.NORTH);

        // middle info
        JPanel info = new JPanel(new GridLayout(2, 2, 10, 6));
        info.add(new JLabel("📍 " + o.getLocation()));
        info.add(new JLabel("🕒 " + o.getType()));
        info.add(new JLabel("👥 " + o.getApplicants() + " applicant(s)"));
        info.add(new JLabel("📅 " + o.getDeadline()));
        card.add(info, BorderLayout.CENTER);

        // skills chips
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        String[] skills = o.getSkills();
        int show = Math.min(skills.length, 4);
        for (int i = 0; i < show; i++) {
            chips.add(chip(skills[i]));
        }
        card.add(chips, BorderLayout.SOUTH);

        // view details button
        JButton btn = new JButton("View Details");
        btn.setBackground(ACCENT_BUTTON);
        btn.setForeground(java.awt.Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        btn.addActionListener(e -> showDetails(o));

        JPanel btnWrap = new JPanel(new BorderLayout());
        btnWrap.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        btnWrap.add(btn, BorderLayout.CENTER);

        JPanel outer = new JPanel(new BorderLayout());
        outer.add(card, BorderLayout.CENTER);
        outer.add(btnWrap, BorderLayout.SOUTH);
        outer.setBackground(MAIN_BG);

        // add an outer border 
        outer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 0, 0),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        outer.setPreferredSize(new java.awt.Dimension(320, 220));
        return outer;
    }

    private JLabel chip(String text) {
        JLabel c = new JLabel(text);
        c.setOpaque(true);
        c.setBackground(new java.awt.Color(230, 235, 245));
        c.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        return c;
    }

    private void showDetails(JobOffer o) {

    	    // Parent window (so dialog centers properly)
    	    Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);

    	    JDialog dialog = new JDialog(owner, "Job Offer Details", true);
    	    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    	    dialog.setLayout(new BorderLayout());
    	    dialog.setSize(880, 650);
    	    dialog.setLocationRelativeTo(this);

    	    JPanel root = new JPanel(new BorderLayout(0, 12));
    	    root.setBorder(BorderFactory.createEmptyBorder(18, 10, 18, 18));
    	    root.setBackground(MAIN_BG);
    	    
    	    // ================= HEADER (Title + badge + close) =================
    	    JPanel header = new JPanel(new BorderLayout(10, 0));

    	    JPanel left = new JPanel(new GridLayout(2, 1, 0, 4));
    	    JLabel lblTitle = new JLabel(o.getTitle());
    	    lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 22f));
    	    lblTitle.setForeground(ACCENT_DARK);
    	    JLabel lblDept = new JLabel(o.getDepartment());
    	    lblDept.setForeground(new java.awt.Color(90, 90, 90));
    	    left.add(lblTitle);
    	    left.add(lblDept);

    	    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

    	    JLabel badge = new JLabel("Open");
    	    badge.setOpaque(true);
    	    badge.setBackground(new java.awt.Color(0, 180, 80));
    	    badge.setForeground(java.awt.Color.WHITE);
    	    badge.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));

    	    right.add(badge);

    	    header.add(left, BorderLayout.WEST);
    	    header.add(right, BorderLayout.EAST);

    	    root.add(header, BorderLayout.NORTH);

    	    // ================= TOP INFO ROW =================
    	    JPanel info = new JPanel(new GridLayout(3, 2, 14, 8));
    	    info.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

    	    info.add(new JLabel("📍  " + o.getLocation()));
    	    info.add(new JLabel("🕒  " + o.getType()));
    	    info.add(new JLabel("💰  " + (o.getSalary() == null || o.getSalary().isEmpty() ? "-" : o.getSalary())));
    	    info.add(new JLabel("👥  " + o.getPositions() + " position(s)"));
    	    info.add(new JLabel("📅  Deadline: " + (o.getDeadline() == null ? "-" : o.getDeadline())));
    	    String created = LocalDate.now()
    	            .format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

    	    info.add(new JLabel("🧾  Created: " + created));
    	    root.add(info, BorderLayout.CENTER);

    	    // ================= MAIN CONTENT (scroll) =================
    	    JPanel content = new JPanel();
    	    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    	    content.setAlignmentX(LEFT_ALIGNMENT);
    	    content.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
    	    content.setBackground(MAIN_BG);

    	    // divider
    	    content.add(new JSeparator());

    	    // ---- Description
    	    content.add(sectionTitle("Description"));
    	    content.add(sectionText(o.getDescription()));

    	    // ---- Skills
    	    content.add(sectionTitle("Required Skills"));
    	    JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
    	    chips.setAlignmentX(LEFT_ALIGNMENT);  
    	    String[] skills = o.getSkills() == null ? new String[0] : o.getSkills();
    	    for (String s : skills) {
    	        if (s == null || s.trim().isEmpty()) continue;
    	        chips.add(skillChip(s.trim()));
    	    }
    	    content.add(chips);

    	    // ---- Qualifications
    	    content.add(sectionTitle("Qualifications"));
    	    content.add(sectionText(o.getQualifications()));

    	    // divider
    	    content.add(new JSeparator());

    	    // ---- Applicants (demo table)
    	    content.add(sectionTitle("Applicants (demo)"));

    	    String[] cols = {"Student", "Year", "University", "Applied", "Status", "Actions"};

    	    Object[][] rows = {
    	        {"Amahle Zungu", "Year 3", "University of Pretoria", "25 Jan 2026", "Pending", "Actions ▼"},
    	        {"Lerato Phiri", "Year 2", "University of Pretoria", "28 Jan 2026", "Pending", "Actions ▼"},
    	        {"James Mokoena", "Year 3", "University of Pretoria", "05 Feb 2026", "Accepted", "Actions ▼"}
    	    };

    	    DefaultTableModel model = new DefaultTableModel(rows, cols) {
    	        @Override public boolean isCellEditable(int r, int c) { return false; }
    	    };
    	    

    	    JTable table = new JTable(model);
    	    table.setBackground(java.awt.Color.WHITE);
    	    table.getTableHeader().setBackground(ACCENT_DARK);
    	    table.getTableHeader().setForeground(java.awt.Color.WHITE);
    	    final int[] currentRow = {-1};
    	 // --- Popup menu (Actions) ---
    	    JPopupMenu menu = new JPopupMenu();

    	    JMenuItem miShortlist = new JMenuItem("Shortlist");
    	    JMenuItem miAccept = new JMenuItem("Accept");
    	    JMenuItem miReject = new JMenuItem("Reject");

    	    miShortlist.setBackground(java.awt.Color.WHITE);
    	    miAccept.setBackground(java.awt.Color.WHITE);
    	    miReject.setBackground(java.awt.Color.WHITE);
    	    
    	    menu.add(miShortlist);
    	    menu.add(miAccept);
    	    menu.addSeparator();
    	    menu.add(miReject);
    	    
    	    miShortlist.addActionListener(ev -> {
    	        int r = currentRow[0];
    	        if (r < 0) return;
    	        model.setValueAt("Shortlisted", r, 4);
    	    });

    	    miAccept.addActionListener(ev -> {
    	        int r = currentRow[0];
    	        if (r < 0) return;
    	        model.setValueAt("Accepted", r, 4);
    	    });

    	    miReject.addActionListener(ev -> {
    	        int r = currentRow[0];
    	        if (r < 0) return;
    	        model.setValueAt("Rejected", r, 4);
    	    });

    	    // helper: get selected applicant name from a row
    	    java.util.function.IntFunction<String> getStudentName = (row) ->
    	            String.valueOf(table.getValueAt(row, 0));

    	    // When user clicks the Actions column -> show popup
    	            table.addMouseListener(new java.awt.event.MouseAdapter() {
    	                @Override
    	                public void mousePressed(java.awt.event.MouseEvent e) {
    	                    int row = table.rowAtPoint(e.getPoint());
    	                    int col = table.columnAtPoint(e.getPoint());
    	                    if (row < 0 || col < 0) return;

    	                    int actionsCol = table.getColumnCount() - 1;

    	                    if (col == actionsCol) {
    	                        table.setRowSelectionInterval(row, row);
    	                        currentRow[0] = row;     // ✅ store clicked row
    	                        menu.show(table, e.getX(), e.getY());
    	                    }
    	                }
    	            });

        	table.setRowHeight(18);
    	    table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    	    table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
    	    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    	    table.getColumnModel().getColumn(0).setPreferredWidth(140); // Student
    	    table.getColumnModel().getColumn(1).setPreferredWidth(60);  // Year
    	    table.getColumnModel().getColumn(2).setPreferredWidth(180); // University
    	    table.getColumnModel().getColumn(3).setPreferredWidth(90);  // Applied
    	    table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Status
    	    table.getColumnModel().getColumn(5).setPreferredWidth(90); // Actions
    	    
    	    JScrollPane sp = new JScrollPane(table);
    	    sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    	    sp.setPreferredSize(new Dimension(820, 230));
    	    content.add(sp);

    	    JScrollPane scroll = new JScrollPane(content);
    	    scroll.getVerticalScrollBar().setUnitIncrement(16);

    	    root.add(scroll, BorderLayout.SOUTH);

    	    dialog.add(root, BorderLayout.CENTER);
    	    dialog.setVisible(true);
    	}

    	private JLabel sectionTitle(String text) {
    	    JLabel l = new JLabel(text);
    	    l.setFont(l.getFont().deriveFont(Font.BOLD, 14f));
    	    l.setBorder(BorderFactory.createEmptyBorder(14, 0, 6, 0));
    	    l.setAlignmentX(LEFT_ALIGNMENT);
    	    l.setForeground(ACCENT_DARK);
    	    return l;
    	}

    	private JLabel sectionText(String text) {
    	    String safe = (text == null || text.trim().isEmpty()) ? "-" : text.trim();
    	    JLabel l = new JLabel("<html><div style='width:780px;'>" + escapeHtml(safe) + "</div></html>");
    	    l.setForeground(new java.awt.Color(70, 70, 70));
    	    l.setAlignmentX(LEFT_ALIGNMENT);
    	    return l;
    	}

    	private JLabel skillChip(String text) {
    	    JLabel c = new JLabel(text);
    	    c.setOpaque(true);
    	    c.setBackground(new java.awt.Color(230, 235, 245));
    	    c.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
    	    return c;
    	}

    	private String escapeHtml(String s) {
    	    return s.replace("&", "&amp;")
    	            .replace("<", "&lt;")
    	            .replace(">", "&gt;")
    	            .replace("\"", "&quot;");
    	}
}
