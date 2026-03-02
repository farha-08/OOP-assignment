package placementportal.company.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;

public class CompanyPortalFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Color MAIN_BG = new Color(0xCF, 0xCF, 0xCF);      // #CFCFCF
    private static final Color ACCENT_DARK = new Color(0x54, 0x54, 0x54);  // #545454
    private static final Color ACCENT_BTN = new Color(0x7D, 0x7D, 0x7D);   // #7D7D7D
    
    private JPanel jp_navbar;
    private JPanel jp_content;   // CENTER area that changes
    private final CompanyDataStore store = new CompanyDataStore();

    // Screens
    private DashboardPanel dashboardPanel;
    private CreateJobPanel createJobPanel;
    private MyJobsPanel myJobsPanel;

    public CompanyPortalFrame() {
        super("Nexus Technologies - Placement Portal");
        setLayout(new BorderLayout());
        getContentPane().setBackground(MAIN_BG);
        
        // Create the screens
        dashboardPanel = new DashboardPanel();
        createJobPanel = new CreateJobPanel(store);
        myJobsPanel = new MyJobsPanel(store);

        // ---------------- NAVBAR ----------------
        jp_navbar = new JPanel(new BorderLayout());

        // Left brand
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        left.setBackground(MAIN_BG);   // keep navbar consistent

        JLabel lblBrand = new JLabel("Nexus Technologies");
        lblBrand.setForeground(ACCENT_DARK);
        lblBrand.setFont(lblBrand.getFont().deriveFont(Font.BOLD, 15f));

        JLabel lblPortal = new JLabel("| Placement Portal");
        lblPortal.setForeground(ACCENT_DARK);

        left.add(lblBrand);
        left.add(lblPortal);

        // Right buttons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnDashboard = new JButton("Dashboard");
        JButton btnMyJobs = new JButton("My Jobs");
        JButton btnCreate = new JButton("Create Job");
        styleNavButton(btnDashboard);
        styleNavButton(btnMyJobs);
        styleNavButton(btnCreate);
        right.add(btnDashboard);
        right.add(btnMyJobs);
        right.add(btnCreate);
        

        jp_navbar.add(left, BorderLayout.WEST);
        jp_navbar.add(right, BorderLayout.EAST);

        add(jp_navbar, BorderLayout.NORTH);
        jp_navbar.setBackground(MAIN_BG);
        left.setBackground(MAIN_BG);
        right.setBackground(MAIN_BG);
        
        // ---------------- CONTENT AREA ----------------
        jp_content = new JPanel(new BorderLayout());
        add(jp_content, BorderLayout.CENTER);
        jp_content.setBackground(MAIN_BG);
        
        // Default page
        showPanel(dashboardPanel);

        // Navbar actions
        btnDashboard.addActionListener(e -> {
            dashboardPanel.refresh(); // update stats if needed
            showPanel(dashboardPanel);
        });

        btnCreate.addActionListener(e -> {
            createJobPanel.clearForm();
            showPanel(createJobPanel);
        });

        btnMyJobs.addActionListener(e -> {
            myJobsPanel.refresh();
            showPanel(myJobsPanel);
        });

        // Frame settings
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Switch pages (NO CardLayout)
    private void showPanel(JPanel panel) {
        jp_content.removeAll();
        jp_content.add(panel, BorderLayout.CENTER);
        jp_content.revalidate();
        jp_content.repaint();
    }

    // Placeholder screen
    private void showMessagePanel(String msg) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.add(new JLabel(msg));
        showPanel(p);
    }
    
    public static void main(String[] args) {
        new CompanyPortalFrame();
    }
    private void styleNavButton(JButton b) {
        b.setBackground(ACCENT_BTN);
        b.setForeground(java.awt.Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
    }
}