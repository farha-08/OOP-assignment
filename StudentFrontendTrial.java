package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentFrontendTrial {

    // -------------------- Mock Session --------------------
    private static Student currentStudent = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            new MainFrame().setVisible(true);
        });
    }

    // ==================== MAIN FRAME (CardLayout) ====================
    static class MainFrame extends JFrame {
        private final CardLayout cards = new CardLayout();
        private final JPanel root = new JPanel(cards);

        private final LoginPanel loginPanel;
        private final RegistrationPanel registrationPanel;
        private final StudentShellPanel studentShellPanel;

        MainFrame() {
            setTitle("Student Placement System - Student Module");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(980, 560);
            setLocationRelativeTo(null);

            loginPanel = new LoginPanel(this);
            registrationPanel = new RegistrationPanel(this);
            studentShellPanel = new StudentShellPanel(this);

            root.add(loginPanel, "LOGIN");
            root.add(registrationPanel, "REGISTER");
            root.add(studentShellPanel, "SHELL");

            setContentPane(root);
            showLogin();
        }

        void showLogin() {
            currentStudent = null;
            cards.show(root, "LOGIN");
            loginPanel.reset();
        }

        void showStudentShell(Student student) {
            currentStudent = student;
            studentShellPanel.refreshForStudent(student);
            cards.show(root, "SHELL");
        }

        void showRegister() {
            registrationPanel.reset();
            cards.show(root, "REGISTER");
        }
    }

    // ==================== LOGIN PANEL ====================
    static class LoginPanel extends JPanel {
        private final MainFrame frame;
        private final JTextField txtEmail = new JTextField(); // reused for username/first+last
        private final JPasswordField txtPass = new JPasswordField();
        private final JLabel lblError = new JLabel(" ");

        LoginPanel(MainFrame frame) {
            this.frame = frame;
            setLayout(new GridBagLayout());
            setBackground(new Color(245, 247, 250));

            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(new EmptyBorder(22, 22, 22, 22));
            card.setBackground(Color.WHITE);
            card.setPreferredSize(new Dimension(420, 320));

            JLabel title = new JLabel("Student Login");
            title.setFont(new Font("SansSerif", Font.BOLD, 20));
            title.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel sub = new JLabel("Enter your email and password to continue.");
            sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
            sub.setForeground(new Color(90, 90, 90));
            sub.setAlignmentX(Component.LEFT_ALIGNMENT);

            card.add(title);
            card.add(Box.createVerticalStrut(6));
            card.add(sub);
            card.add(Box.createVerticalStrut(18));

            card.add(label("Username or Email"));
            styleField(txtEmail);
            card.add(txtEmail);
            card.add(Box.createVerticalStrut(10));

            card.add(label("Password"));
            styleField(txtPass);
            card.add(txtPass);

            lblError.setForeground(new Color(190, 40, 40));
            lblError.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton btnLogin = new JButton("Login");
            btnLogin.setFocusPainted(false);
            btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnLogin.setPreferredSize(new Dimension(120, 36));

            btnLogin.addActionListener(e -> doLogin());

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            actions.setBackground(Color.WHITE);
            actions.add(btnLogin);
            actions.setAlignmentX(Component.LEFT_ALIGNMENT);

            card.add(Box.createVerticalStrut(12));
            card.add(lblError);
            card.add(Box.createVerticalStrut(10));
            card.add(actions);

            // register button below login
            card.add(Box.createVerticalStrut(8));
            JButton btnRegister = new JButton("Register");
            btnRegister.setFocusPainted(false);
            btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnRegister.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnRegister.addActionListener(e -> frame.showRegister());
            card.add(btnRegister);

            add(card);
        }

        void reset() {
            txtEmail.setText("");
            txtPass.setText("");
            lblError.setText(" ");
        }

        private void doLogin() {
            String email = txtEmail.getText().trim();
            String pass = new String(txtPass.getPassword());

            if (email.isEmpty() || pass.isEmpty()) {
                lblError.setText("Please enter email and password.");
                return;
            }

            Student s = MockAuthService.loginStudent(email, pass);
            if (s == null) {
                lblError.setText("Invalid credentials. Try: tony@uom.mu or Tony Student / 1234");
            } else {
                lblError.setText(" ");
                frame.showStudentShell(s);
            }
        }

        private JLabel label(String text) {
            JLabel l = new JLabel(text);
            l.setFont(new Font("SansSerif", Font.PLAIN, 12));
            l.setForeground(new Color(60, 60, 60));
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            return l;
        }

        private void styleField(JComponent c) {
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
            c.setFont(new Font("SansSerif", Font.PLAIN, 13));
            c.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 225, 232)),
                    new EmptyBorder(6, 8, 6, 8)
            ));
        }
    }

    // ==================== REGISTRATION PANEL ====================
    static class RegistrationPanel extends JPanel {
        private final MainFrame frame;
        private final JTextField txtFirst = new JTextField();
        private final JTextField txtLast = new JTextField();
        private final JTextField txtEmail = new JTextField();
        private final JPasswordField txtPass = new JPasswordField();
        private final JPasswordField txtConfirm = new JPasswordField();
        private final JTextField txtFaculty = new JTextField();
        private final JTextField txtCourse = new JTextField();
        private final JTextField txtYear = new JTextField();
        private final JLabel lblError = new JLabel(" ");

        RegistrationPanel(MainFrame frame) {
            this.frame = frame;
            setLayout(new BorderLayout()); // use border layout for scrolling
            setBackground(new Color(245, 247, 250));

            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(new EmptyBorder(22, 22, 22, 22));
            card.setBackground(Color.WHITE);

            JLabel title = new JLabel("Student Registration");
            title.setFont(new Font("SansSerif", Font.BOLD, 20));
            title.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel sub = new JLabel("Provide your details to create an account.");
            sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
            sub.setForeground(new Color(90, 90, 90));
            sub.setAlignmentX(Component.LEFT_ALIGNMENT);

            card.add(title);
            card.add(Box.createVerticalStrut(6));
            card.add(sub);
            card.add(Box.createVerticalStrut(18));

            card.add(label("First Name"));
            styleField(txtFirst);
            card.add(txtFirst);
            card.add(Box.createVerticalStrut(10));

            card.add(label("Last Name"));
            styleField(txtLast);
            card.add(txtLast);
            card.add(Box.createVerticalStrut(10));

            card.add(label("Email"));
            styleField(txtEmail);
            card.add(txtEmail);
            card.add(Box.createVerticalStrut(10));
            card.add(label("Password"));
            styleField(txtPass);
            card.add(txtPass);
            card.add(Box.createVerticalStrut(10));

            card.add(label("Confirm Password"));
            styleField(txtConfirm);
            card.add(txtConfirm);
            card.add(Box.createVerticalStrut(10));

            card.add(label("Faculty"));
            styleField(txtFaculty);
            card.add(txtFaculty);
            card.add(Box.createVerticalStrut(10));

            card.add(label("Course"));
            styleField(txtCourse);
            card.add(txtCourse);
            card.add(Box.createVerticalStrut(10));

            card.add(label("Year of Study"));
            styleField(txtYear);
            card.add(txtYear);

            lblError.setForeground(new Color(190, 40, 40));
            lblError.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton btnRegister = new JButton("Register");
            btnRegister.setFocusPainted(false);
            btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnRegister.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnRegister.setPreferredSize(new Dimension(120, 36));
            btnRegister.addActionListener(e -> doRegistration());

            JButton btnBack = new JButton("Back to Login");
            btnBack.setFocusPainted(false);
            btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnBack.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnBack.addActionListener(e -> frame.showLogin());

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            actions.setBackground(Color.WHITE);
            actions.add(btnRegister);
            actions.add(Box.createHorizontalStrut(8));
            actions.add(btnBack);
            actions.setAlignmentX(Component.LEFT_ALIGNMENT);

            card.add(Box.createVerticalStrut(12));
            card.add(lblError);
            card.add(Box.createVerticalStrut(10));
            card.add(actions);


            // wrap card in scroll pane so user can scroll if window is small
            JScrollPane sc = new JScrollPane(card,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            sc.setBorder(null);
            add(sc, BorderLayout.CENTER);
        }

        void reset() {
            txtFirst.setText("");
            txtLast.setText("");
            txtEmail.setText("");
            txtPass.setText("");
            txtConfirm.setText("");
            txtFaculty.setText("");
            txtCourse.setText("");
            txtYear.setText("");
            lblError.setText(" ");
        }

        private void doRegistration() {
            String first = txtFirst.getText().trim();
            String last = txtLast.getText().trim();
            String email = txtEmail.getText().trim();
            String pass = new String(txtPass.getPassword());
            String conf = new String(txtConfirm.getPassword());
            String faculty = txtFaculty.getText().trim();
            String course = txtCourse.getText().trim();
            String yearStr = txtYear.getText().trim();

            if (first.isEmpty() || last.isEmpty() || email.isEmpty() || pass.isEmpty() || conf.isEmpty() ||
                    faculty.isEmpty() || course.isEmpty() || yearStr.isEmpty()) {
                lblError.setText("All fields are required.");
                return;
            }
            if (!pass.equals(conf)) {
                lblError.setText("Passwords do not match.");
                return;
            }
            int year;
            try {
                year = Integer.parseInt(yearStr);
            } catch (Exception ex) {
                lblError.setText("Year must be a number.");
                return;
            }
            // ensure unique username (full name) and email among existing students
            String fullName = first + " " + last;
            for (Student s : MockDB.students) {
                if (s.fullName.equalsIgnoreCase(fullName)) {
                    lblError.setText("A student with that name already exists.");
                    return;
                }
                if (s.email.equalsIgnoreCase(email)) {
                    lblError.setText("Email already in use.");
                    return;
                }
            }

            String newId = "S" + String.format("%03d", MockDB.students.size() + 1);
            // reuse existing fields: email set to fullName, branch stores faculty, section stores year
            Student newStudent = new Student(newId, fullName, email, pass,
                    course, faculty, String.valueOf(year), 0.0);
            MockDB.students.add(newStudent);
            JOptionPane.showMessageDialog(this, "Registration successful. You can now login.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            frame.showLogin();
        }

        private JLabel label(String text) {
            JLabel l = new JLabel(text);
            l.setFont(new Font("SansSerif", Font.PLAIN, 12));
            l.setForeground(new Color(60, 60, 60));
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            return l;
        }

        private void styleField(JComponent c) {
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
            c.setFont(new Font("SansSerif", Font.PLAIN, 13));
            c.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 225, 232)),
                    new EmptyBorder(6, 8, 6, 8)
            ));
        }
    }

    // ==================== STUDENT SHELL (Sidebar + Content) ====================
    static class StudentShellPanel extends JPanel {
        private final MainFrame frame;

        private final CardLayout contentCards = new CardLayout();
        private final JPanel content = new JPanel(contentCards);

        private final JLabel lblWelcome = new JLabel("Welcome, Student");

        private final StudentProfilePanel profilePanel = new StudentProfilePanel();
        private final StudentApplicationsPanel appsPanel = new StudentApplicationsPanel();

        StudentShellPanel(MainFrame frame) {
            this.frame = frame;
            setLayout(new BorderLayout());

            JPanel sidebar = buildSidebar();
            JPanel topbar = buildTopbar();

            content.add(buildHomePanel(), "HOME");
            content.add(profilePanel, "PROFILE");
            content.add(appsPanel, "APPS");

            add(sidebar, BorderLayout.WEST);
            add(topbar, BorderLayout.NORTH);
            add(content, BorderLayout.CENTER);
        }

        void refreshForStudent(Student s) {
            lblWelcome.setText("Welcome, " + s.fullName);
            profilePanel.loadStudent(s);
            appsPanel.loadApplicationsForStudent(s.id);
            contentCards.show(content, "HOME");
        }

        private JPanel buildSidebar() {
            JPanel side = new JPanel();
            side.setPreferredSize(new Dimension(220, 0));
            side.setBackground(new Color(26, 35, 50));
            side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
            side.setBorder(new EmptyBorder(18, 14, 18, 14));

            JLabel brand = new JLabel("Placement System");
            brand.setForeground(Color.WHITE);
            brand.setFont(new Font("SansSerif", Font.BOLD, 15));

            JLabel module = new JLabel("Student Module");
            module.setForeground(new Color(180, 190, 205));
            module.setFont(new Font("SansSerif", Font.PLAIN, 12));

            side.add(brand);
            side.add(Box.createVerticalStrut(4));
            side.add(module);
            side.add(Box.createVerticalStrut(18));

            side.add(sideBtn("Dashboard", () -> contentCards.show(content, "HOME")));
            side.add(Box.createVerticalStrut(8));
            side.add(sideBtn("Profile", () -> contentCards.show(content, "PROFILE")));
            side.add(Box.createVerticalStrut(8));
            side.add(sideBtn("My Applications", () -> contentCards.show(content, "APPS")));

            side.add(Box.createVerticalGlue());

            JButton logout = sideBtn("Logout", frame::showLogin);
            logout.setBackground(new Color(60, 76, 108));
            side.add(logout);

            return side;
        }

        private JPanel buildTopbar() {
            JPanel top = new JPanel(new BorderLayout());
            top.setBackground(Color.WHITE);
            top.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 235, 242)));
            top.setPreferredSize(new Dimension(0, 56));

            lblWelcome.setBorder(new EmptyBorder(0, 18, 0, 0));
            lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 14));

            top.add(lblWelcome, BorderLayout.WEST);

            JLabel hint = new JLabel("Frontend-only demo (mock data)");
            hint.setBorder(new EmptyBorder(0, 0, 0, 18));
            hint.setForeground(new Color(120, 120, 120));
            hint.setFont(new Font("SansSerif", Font.PLAIN, 12));

            top.add(hint, BorderLayout.EAST);
            return top;
        }

        private JPanel buildHomePanel() {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(new Color(245, 247, 250));

            JPanel card = new JPanel();
            card.setBackground(Color.WHITE);
            card.setBorder(new EmptyBorder(18, 18, 18, 18));
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

            JLabel t = new JLabel("Student Dashboard");
            t.setFont(new Font("SansSerif", Font.BOLD, 18));

            JLabel d = new JLabel("<html>Use the sidebar to view your <b>Profile</b> and <b>My Applications</b>.</html>");
            d.setForeground(new Color(90, 90, 90));
            d.setFont(new Font("SansSerif", Font.PLAIN, 13));

            card.add(t);
            card.add(Box.createVerticalStrut(8));
            card.add(d);
            card.add(Box.createVerticalStrut(16));

            JPanel quick = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            quick.setBackground(Color.WHITE);

            JButton b1 = new JButton("Open Profile");
            JButton b2 = new JButton("Open My Applications");
            styleActionBtn(b1);
            styleActionBtn(b2);

            b1.addActionListener(e -> contentCards.show(content, "PROFILE"));
            b2.addActionListener(e -> contentCards.show(content, "APPS"));

            quick.add(b1);
            quick.add(b2);

            card.add(quick);

            JPanel wrap = new JPanel(new GridBagLayout());
            wrap.setBackground(new Color(245, 247, 250));
            wrap.add(card);

            p.add(wrap, BorderLayout.CENTER);
            return p;
        }

        private JButton sideBtn(String text, Runnable onClick) {
            JButton b = new JButton(text);
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setForeground(Color.WHITE);
            b.setBackground(new Color(40, 52, 74));
            b.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            b.addActionListener(e -> onClick.run());
            return b;
        }

        private void styleActionBtn(JButton b) {
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        }
    }

    // ==================== PROFILE PANEL ====================
    static class StudentProfilePanel extends JPanel {
        private Student workingCopy;

        private final JTextField txtName = new JTextField();
        private final JTextField txtEmail = new JTextField();
        private final JTextField txtCourse = new JTextField();
        private final JTextField txtBranch = new JTextField();
        private final JTextField txtSection = new JTextField();
        private final JTextField txtGpa = new JTextField();

        private final JButton btnEdit = new JButton("Edit");
        private final JButton btnSave = new JButton("Save");
        private final JLabel lblMsg = new JLabel(" ");

        StudentProfilePanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(245, 247, 250));

            JPanel card = new JPanel();
            card.setBackground(Color.WHITE);
            card.setBorder(new EmptyBorder(18, 18, 18, 18));
            card.setLayout(new BorderLayout(0, 14));

            JLabel title = new JLabel("Student Profile");
            title.setFont(new Font("SansSerif", Font.BOLD, 18));
            card.add(title, BorderLayout.NORTH);

            JPanel form = new JPanel(new GridBagLayout());
            form.setBackground(Color.WHITE);

            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(6, 6, 6, 6);
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.weightx = 1;

            int r = 0;
            addRow(form, gc, r++, "Full Name", txtName);
            addRow(form, gc, r++, "Email", txtEmail);
            addRow(form, gc, r++, "Course", txtCourse);
            addRow(form, gc, r++, "Branch", txtBranch);
            addRow(form, gc, r++, "Section", txtSection);
            addRow(form, gc, r++, "GPA/CGPA", txtGpa);

            styleField(txtName); styleField(txtEmail); styleField(txtCourse);
            styleField(txtBranch); styleField(txtSection); styleField(txtGpa);

            card.add(form, BorderLayout.CENTER);

            JPanel bottom = new JPanel(new BorderLayout());
            bottom.setBackground(Color.WHITE);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            actions.setBackground(Color.WHITE);

            btnEdit.setFocusPainted(false);
            btnSave.setFocusPainted(false);
            btnEdit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            btnEdit.addActionListener(e -> setEditing(true));
            btnSave.addActionListener(e -> save());

            actions.add(btnEdit);
            actions.add(btnSave);

            lblMsg.setForeground(new Color(40, 140, 60));
            lblMsg.setBorder(new EmptyBorder(6, 0, 0, 0));

            bottom.add(actions, BorderLayout.WEST);
            bottom.add(lblMsg, BorderLayout.SOUTH);

            card.add(bottom, BorderLayout.SOUTH);

            JPanel wrap = new JPanel(new GridBagLayout());
            wrap.setBackground(new Color(245, 247, 250));
            wrap.add(card);

            add(wrap, BorderLayout.CENTER);

            setEditing(false);
        }

        void loadStudent(Student s) {
            workingCopy = new Student(s); // copy so it feels "frontend"
            txtName.setText(workingCopy.fullName);
            txtEmail.setText(workingCopy.email);
            txtCourse.setText(workingCopy.course);
            txtBranch.setText(workingCopy.branch);
            txtSection.setText(workingCopy.section);
            txtGpa.setText(String.valueOf(workingCopy.gpa));
            lblMsg.setText(" ");
            setEditing(false);
        }

        private void save() {
            if (workingCopy == null) return;

            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            String course = txtCourse.getText().trim();

            if (name.isEmpty() || email.isEmpty() || course.isEmpty()) {
                lblMsg.setForeground(new Color(190, 40, 40));
                lblMsg.setText("Please fill in at least Name, Email and Course.");
                return;
            }

            double gpa;
            try {
                gpa = Double.parseDouble(txtGpa.getText().trim());
            } catch (Exception ex) {
                lblMsg.setForeground(new Color(190, 40, 40));
                lblMsg.setText("GPA must be a number (e.g. 3.45).");
                return;
            }

            workingCopy.fullName = name;
            workingCopy.email = email;
            workingCopy.course = course;
            workingCopy.branch = txtBranch.getText().trim();
            workingCopy.section = txtSection.getText().trim();
            workingCopy.gpa = gpa;

            MockStudentService.updateStudent(workingCopy);

            lblMsg.setForeground(new Color(40, 140, 60));
            lblMsg.setText("Profile saved (mock).");
            setEditing(false);
        }

        private void setEditing(boolean editing) {
            txtName.setEnabled(editing);
            txtEmail.setEnabled(editing);
            txtCourse.setEnabled(editing);
            txtBranch.setEnabled(editing);
            txtSection.setEnabled(editing);
            txtGpa.setEnabled(editing);

            btnSave.setEnabled(editing);
            btnEdit.setEnabled(!editing);

            if (!editing) {
                lblMsg.setText(" ");
            }
        }

        private void addRow(JPanel panel, GridBagConstraints gc, int row, String label, JComponent field) {
            gc.gridx = 0;
            gc.gridy = row;
            gc.weightx = 0.25;
            JLabel l = new JLabel(label);
            l.setFont(new Font("SansSerif", Font.PLAIN, 12));
            l.setForeground(new Color(70, 70, 70));
            panel.add(l, gc);

            gc.gridx = 1;
            gc.weightx = 0.75;
            panel.add(field, gc);
        }

        private void styleField(JTextField f) {
            f.setPreferredSize(new Dimension(260, 32));
            f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 225, 232)),
                    new EmptyBorder(6, 8, 6, 8)
            ));
        }
    }

    // ==================== APPLICATIONS PANEL ====================
    static class StudentApplicationsPanel extends JPanel {
        private final JComboBox<String> cmbFilter = new JComboBox<>(
                new String[]{"All", "APPLIED", "SHORTLISTED", "ACCEPTED", "REJECTED"}
        );

        private final DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Company", "Offer", "Applied Date", "Status"}, 0
        ) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        private final JTable table = new JTable(model);
        private String currentStudentId;

        StudentApplicationsPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(245, 247, 250));

            JPanel card = new JPanel(new BorderLayout(0, 12));
            card.setBackground(Color.WHITE);
            card.setBorder(new EmptyBorder(18, 18, 18, 18));

            JLabel title = new JLabel("My Applications");
            title.setFont(new Font("SansSerif", Font.BOLD, 18));
            card.add(title, BorderLayout.NORTH);

            JPanel topControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            topControls.setBackground(Color.WHITE);

            JLabel l = new JLabel("Filter by status:");
            l.setFont(new Font("SansSerif", Font.PLAIN, 12));

            cmbFilter.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            cmbFilter.addActionListener(e -> refreshTable());

            topControls.add(l);
            topControls.add(cmbFilter);

            card.add(topControls, BorderLayout.WEST);

            table.setRowHeight(26);
            table.getTableHeader().setReorderingAllowed(false);

            JScrollPane sp = new JScrollPane(table);
            sp.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 242)));

            card.add(sp, BorderLayout.CENTER);

            JPanel wrap = new JPanel(new GridBagLayout());
            wrap.setBackground(new Color(245, 247, 250));
            wrap.add(card);

            add(wrap, BorderLayout.CENTER);
        }

        void loadApplicationsForStudent(String studentId) {
            this.currentStudentId = studentId;
            cmbFilter.setSelectedIndex(0);
            refreshTable();
        }

        private void refreshTable() {
            model.setRowCount(0);
            if (currentStudentId == null) return;

            String filter = (String) cmbFilter.getSelectedItem();
            List<ApplicationView> rows = MockApplicationService.getApplicationsForStudent(currentStudentId);

            for (ApplicationView av : rows) {
                if (!"All".equals(filter) && !av.status.name().equals(filter)) continue;
                model.addRow(new Object[]{
                        av.companyName,
                        av.offerTitle,
                        av.appliedDate.toString(),
                        av.status.name()
                });
            }
        }
    }

    // ==================== MOCK DATA + SERVICES ====================

    // ----- Models -----
    static class Student {
        String id;
        String fullName;
        String email;
        String password;
        String course;
        String branch;
        String section;
        double gpa;

        Student(String id, String fullName, String email, String password,
                String course, String branch, String section, double gpa) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.password = password;
            this.course = course;
            this.branch = branch;
            this.section = section;
            this.gpa = gpa;
        }

        Student(Student other) {
            this(other.id, other.fullName, other.email, other.password,
                    other.course, other.branch, other.section, other.gpa);
        }
    }

    enum ApplicationStatus { APPLIED, SHORTLISTED, ACCEPTED, REJECTED }

    static class ApplicationView {
        String companyName;
        String offerTitle;
        LocalDate appliedDate;
        ApplicationStatus status;

        ApplicationView(String companyName, String offerTitle, LocalDate appliedDate, ApplicationStatus status) {
            this.companyName = companyName;
            this.offerTitle = offerTitle;
            this.appliedDate = appliedDate;
            this.status = status;
        }
    }

    // ----- Mock Database -----
    static class MockDB {
        static final List<Student> students = new ArrayList<>();
        static final List<ApplicationRecord> applications = new ArrayList<>();

        static {
            // Students
            students.add(new Student("S001", "Tony Student", "tony@uom.mu", "1234",
                    "BSc (Hons) Computer Science", "Software Eng", "A", 3.45));
            students.add(new Student("S002", "Aisha Student", "aisha@uom.mu", "1234",
                    "BSc (Hons) Information Systems", "IS", "B", 3.20));

            // Applications (for S001)
            applications.add(new ApplicationRecord("S001", "TechNova Ltd", "Software Intern", LocalDate.now().minusDays(8), ApplicationStatus.APPLIED));
            applications.add(new ApplicationRecord("S001", "CloudWave", "Junior Developer", LocalDate.now().minusDays(15), ApplicationStatus.SHORTLISTED));
            applications.add(new ApplicationRecord("S001", "DataZen", "Graduate Trainee", LocalDate.now().minusDays(22), ApplicationStatus.REJECTED));
            applications.add(new ApplicationRecord("S001", "FinLink", "IT Support Intern", LocalDate.now().minusDays(30), ApplicationStatus.ACCEPTED));

            // Applications (for S002)
            applications.add(new ApplicationRecord("S002", "IslandSoft", "Business Analyst Intern", LocalDate.now().minusDays(6), ApplicationStatus.APPLIED));
        }
    }

    static class ApplicationRecord {
        String studentId;
        String companyName;
        String offerTitle;
        LocalDate appliedDate;
        ApplicationStatus status;

        ApplicationRecord(String studentId, String companyName, String offerTitle, LocalDate appliedDate, ApplicationStatus status) {
            this.studentId = studentId;
            this.companyName = companyName;
            this.offerTitle = offerTitle;
            this.appliedDate = appliedDate;
            this.status = status;
        }
    }

    // ----- Mock Services -----
    static class MockAuthService {
        static Student loginStudent(String identifier, String password) {
            for (Student s : MockDB.students) {
                // support old-style email login as well as new full-name username
                if ((s.email.equalsIgnoreCase(identifier) || s.fullName.equalsIgnoreCase(identifier))
                        && s.password.equals(password)) {
                    return s;
                }
            }
            return null;
        }
    }

    static class MockStudentService {
        
        static void updateStudent(Student updated) {
            // frontend mock update in the list
            for (int i = 0; i < MockDB.students.size(); i++) {
                if (MockDB.students.get(i).id.equals(updated.id)) {
                    MockDB.students.set(i, updated);
                    // also refresh currentStudent reference (mock session)
                    if (currentStudent != null && currentStudent.id.equals(updated.id)) {
                        currentStudent = updated;
                    }
                    return;
                }
            }
        }
    }

    static class MockApplicationService {
        static List<ApplicationView> getApplicationsForStudent(String studentId) {
            List<ApplicationView> out = new ArrayList<>();
            for (ApplicationRecord ar : MockDB.applications) {
                if (ar.studentId.equals(studentId)) {
                    out.add(new ApplicationView(ar.companyName, ar.offerTitle, ar.appliedDate, ar.status));
                }
            }
            return out;
        }
    }
}
