package placementportal.company.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.awt.Font;

public class CreateJobPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final Color MAIN_BG = new Color(0xCF, 0xCF, 0xCF);      // #CFCFCF
    private static final Color ACCENT_DARK = new Color(0x54, 0x54, 0x54);  // #545454
    private static final Color ACCENT_BTN = new Color(0x7D, 0x7D, 0x7D);   // #7D7D7D
    private final CompanyDataStore store;

    // Top fields
    private JTextField jt_title = new JTextField(20);
    private JTextField jt_dept = new JTextField(20);
    private JTextField jt_location = new JTextField(20);
    private JComboBox<String> jc_type = new JComboBox<>(new String[]{"Full-time", "Internship"});
    private JTextField jt_salary = new JTextField(20);
    private JSpinner js_positions = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private JTextField jt_deadline = new JTextField(20); // dd/mm/yyyy

    // Text areas
    private JTextArea ta_desc = new JTextArea(5, 30);
    private JTextArea ta_qual = new JTextArea(4, 30);

    // Skills
    private JTextField jt_skill = new JTextField(20);
    private JButton btnAddSkill = new JButton("Add");
    private final List<String> skillsList = new ArrayList<>(); 
    
    public CreateJobPanel(CompanyDataStore store) {
        this.store = store;
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(MAIN_BG);
        
        // ---------- HEADER ----------
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(MAIN_BG);

        JLabel h1 = new JLabel("Create Job Offer");
        h1.setForeground(ACCENT_DARK);
        h1.setFont(h1.getFont().deriveFont(java.awt.Font.BOLD, 22f));

        JLabel h2 = new JLabel("Fill in the details to post a new job vacancy");
        h2.setForeground(new java.awt.Color(100, 100, 100));

        header.add(h1);
        header.add(h2);

        add(header, BorderLayout.NORTH);

        // ---------- CARD ----------
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(java.awt.Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel jobDetails = new JLabel("Job Details");
        jobDetails.setForeground(ACCENT_DARK);
        jobDetails.setFont(jobDetails.getFont().deriveFont(Font.BOLD, 14f));
        card.add(jobDetails, BorderLayout.NORTH);
        // ---------- FORM GRID (2 columns) ----------
        JPanel formGrid = new JPanel(new GridLayout(0, 2, 15, 10));
        formGrid.setBackground(Color.WHITE);
        formGrid.add(new JLabel("Job Title"));
        formGrid.add(new JLabel("Department"));
        formGrid.add(jt_title);
        formGrid.add(jt_dept);

        formGrid.add(new JLabel("Location"));
        formGrid.add(new JLabel("Employment Type"));
        formGrid.add(jt_location);
        formGrid.add(jc_type);

        formGrid.add(new JLabel("Salary Range"));
        formGrid.add(new JLabel("Number of Positions"));
        formGrid.add(jt_salary);
        formGrid.add(js_positions);

        formGrid.add(new JLabel("Application Deadline (dd/mm/yyyy)"));
        formGrid.add(new JLabel("")); // empty
        formGrid.add(jt_deadline);
        formGrid.add(new JLabel("")); // empty

        // ---------- DESCRIPTION ----------
        ta_desc.setLineWrap(true);
        ta_desc.setWrapStyleWord(true);

        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBackground(Color.WHITE);
        descPanel.add(new JLabel("Job Description"), BorderLayout.NORTH);
        descPanel.add(ta_desc, BorderLayout.CENTER);

        // ---------- QUALIFICATIONS ----------
        ta_qual.setLineWrap(true);
        ta_qual.setWrapStyleWord(true);

        JPanel qualPanel = new JPanel(new BorderLayout());
        qualPanel.setBackground(Color.WHITE);
        qualPanel.add(new JLabel("Qualifications"), BorderLayout.NORTH);
        qualPanel.add(ta_qual, BorderLayout.CENTER);

        // ---------- SKILLS ----------
        JPanel skillsRow = new JPanel(new BorderLayout(10, 0));
        skillsRow.setBackground(Color.WHITE);
        styleButton(btnAddSkill);   // ✅ ADD THIS
        skillsRow.add(jt_skill, BorderLayout.CENTER);
        skillsRow.add(btnAddSkill, BorderLayout.EAST);

        JPanel skillsPanel = new JPanel(new BorderLayout());
        skillsPanel.setBackground(Color.WHITE);
        skillsPanel.add(new JLabel("Required Skills"), BorderLayout.NORTH);
        skillsPanel.add(skillsRow, BorderLayout.CENTER);

        // ---------- BODY LAYOUT (IMPORTANT FIX) ----------
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(Color.WHITE);
        body.add(formGrid, BorderLayout.NORTH);

        JPanel middle = new JPanel(new GridLayout(2, 1, 0, 12));
        middle.setBackground(Color.WHITE);
        middle.add(descPanel);
        middle.add(qualPanel);
        body.add(middle, BorderLayout.CENTER);

        body.add(skillsPanel, BorderLayout.SOUTH);

        card.add(body, BorderLayout.CENTER);

        // ---------- BOTTOM BUTTON ----------
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(Color.WHITE);
        JButton btnCreate = new JButton("Create Job Offer");
        styleButton(btnCreate);
        bottom.add(btnCreate);

        // Demo button actions
        btnAddSkill.addActionListener(e -> {
            String skill = jt_skill.getText().trim();
            if (skill.isEmpty()) return;

            // Optional: prevent duplicates
            for (String s : skillsList) {
                if (s.equalsIgnoreCase(skill)) {
                    jt_skill.setText("");
                    return;
                }
            }

            skillsList.add(skill);   
            jt_skill.setText("");    
        });

        btnCreate.addActionListener(e -> {

            String title = jt_title.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Job Title is required.");
                return;
            }

            String dept = jt_dept.getText().trim();
            String loc = jt_location.getText().trim();
            String type = jc_type.getSelectedItem().toString();
            String salary = jt_salary.getText().trim();
            int positions = (Integer) js_positions.getValue();
            String deadline = jt_deadline.getText().trim();
            String desc = ta_desc.getText().trim();
            String qual = ta_qual.getText().trim();

            String[] skills = skillsList.isEmpty()
                    ? new String[]{"+1 more"}
                    : skillsList.toArray(new String[0]);

            JobOffer offer = new JobOffer(
                    title, dept, loc, type,
                    salary, positions, deadline,
                    desc, qual, skills,
                    0   // applicants default 0
            );

            store.addOffer(offer);

            JOptionPane.showMessageDialog(this,
                    "Job Offer saved! Go to 'My Jobs' to view it.");

            clearForm();
        });

        card.add(bottom, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
    }

    // Optional helper: clear form when opening Create Job page
    public void clearForm() {
        jt_title.setText("");
        jt_dept.setText("");
        jt_location.setText("");
        jc_type.setSelectedIndex(0);
        jt_salary.setText("");
        js_positions.setValue(1);
        jt_deadline.setText("");
        ta_desc.setText("");
        ta_qual.setText("");
        jt_skill.setText("");
        skillsList.clear();
    }
    private void styleButton(JButton b) {
        b.setBackground(ACCENT_BTN);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
    }
}
