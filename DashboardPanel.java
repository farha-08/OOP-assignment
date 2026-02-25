package placementportal.company.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
//trial demo data
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import java.awt.Color;

public class DashboardPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final Color MAIN_BG = new Color(0xCF, 0xCF, 0xCF);
    private static final Color ACCENT_DARK = new Color(0x54, 0x54, 0x54);
    private static final Color ACCENT_BTN = new Color(0x7D, 0x7D, 0x7D);
    // demo values (later you can connect to datastore)
    private JLabel lblOffers = new JLabel("0");
    private JLabel lblApps = new JLabel("0");
    private JLabel lblPending = new JLabel("0");
    private JLabel lblAccepted = new JLabel("0");
    
    private DefaultTableModel tableModel;

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(MAIN_BG);
        
        // Header
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(MAIN_BG);   // match outer background

        JLabel h1 = new JLabel("Dashboard");
        h1.setFont(h1.getFont().deriveFont(Font.BOLD, 22f));
        h1.setForeground(ACCENT_DARK);   // #545454

        JLabel h2 = new JLabel("Overview of your placement activities");
        h2.setForeground(new Color(100, 100, 100));

        header.add(h1);
        header.add(h2);

        add(header, BorderLayout.NORTH);

        // Stats row (4 cards)
        JPanel stats = new JPanel(new GridLayout(1, 4, 12, 12));
        stats.setBackground(MAIN_BG);
        stats.add(card("Total Job Offers", lblOffers));
        stats.add(card("Total Applications", lblApps));
        stats.add(card("Pending Review", lblPending));
        stats.add(card("Accepted", lblAccepted));

        JPanel statsWrapper = new JPanel(new BorderLayout());
        statsWrapper.setBackground(MAIN_BG);
        statsWrapper.add(stats, BorderLayout.CENTER);

        // ✅ reserve space so cards don't collapse
        statsWrapper.setPreferredSize(new java.awt.Dimension(10, 120));


        // Recent applications placeholder (you can replace with JTable later)
     // ----- RECENT APPLICATIONS-trial demo data -----
        JPanel recent = new JPanel(new BorderLayout());
        recent.setBackground(Color.WHITE);
        recent.setBorder(BorderFactory.createTitledBorder("Recent Applications"));

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setBackground(MAIN_BG);

        center.add(statsWrapper, BorderLayout.NORTH);
        center.add(recent, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
        // Column names
        String[] columns = {"Student", "Job Title", "Year of Study", "Applied", "Status"};

        // Demo rows
        Object[][] data = {
                {"Naledi Dlamini", "IT Support Specialist", "Year 4", "06 Feb 2026", "Pending"},
                {"James Mokoena", "Data Analyst Intern", "Year 3", "05 Feb 2026", "Accepted"},
                {"Thabo Nkosi", "Junior Software Developer", "Year 2", "02 Feb 2026", "Rejected"},
                {"Lerato Phiri", "Data Analyst Intern", "Year 2", "28 Jan 2026", "Pending"}
        };

        // Table model
        tableModel = new DefaultTableModel(data, columns) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // JTable
        JTable table = new JTable(tableModel);
        table.setBackground(Color.WHITE);
        table.getTableHeader().setBackground(ACCENT_DARK);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // ScrollPane (important!)
        JScrollPane scrollPane = new JScrollPane(table);

        recent.add(scrollPane, BorderLayout.CENTER);

        updateStats();
    }

    private JPanel card(String titleText, JLabel value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(java.awt.Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(java.awt.Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        // number
        value.setFont(new Font("Segoe UI", Font.BOLD, 22));
        value.setForeground(ACCENT_DARK);

        // title
        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        title.setForeground(ACCENT_DARK);

        // stack number + title vertically
        JPanel inner = new JPanel(new GridLayout(2, 1, 0, 4));
        inner.setBackground(java.awt.Color.WHITE);
        inner.add(value);
        inner.add(title);

        p.add(inner, BorderLayout.CENTER);

        return p;
    }

    // If later you connect to real data, update labels here
    public void refresh() {
    	// For now, nothing dynamic. 
    	// Example later: 
    	// lblOffers.setText(String.valueOf(store.getOffersCount()));
    }
	private void updateStats() {

	    int totalApps = tableModel.getRowCount();
	    int pending = 0;
	    int accepted = 0;

	    for (int i = 0; i < totalApps; i++) {

	        String status = tableModel.getValueAt(i, 4).toString();

	        if (status.equalsIgnoreCase("Pending")) {
	            pending++;
	        }
	        else if (status.equalsIgnoreCase("Accepted")) {
	            accepted++;
	        }
	    }

	    lblApps.setText(String.valueOf(totalApps));
	    lblPending.setText(String.valueOf(pending));
	    lblAccepted.setText(String.valueOf(accepted));
	}
    
}