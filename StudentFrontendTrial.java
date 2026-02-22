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
    private static Company currentCompany = null;
    private static Admin currentAdmin = null;

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
        private final CompanyShellPanel companyShellPanel;
        private final AdminShellPanel adminShellPanel;

        MainFrame() {
            setTitle("Student Placement System");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1180, 700);
            setLocationRelativeTo(null);

            loginPanel = new LoginPanel(this);
            registrationPanel = new RegistrationPanel(this);

            studentShellPanel = new StudentShellPanel(this);
            companyShellPanel = new CompanyShellPanel(this);
            adminShellPanel = new AdminShellPanel(this);

            root.add(loginPanel, "LOGIN");
            root.add(registrationPanel, "REGISTER");
            root.add(studentShellPanel, "STUDENT_SHELL");
            root.add(companyShellPanel, "COMPANY_SHELL");
            root.add(adminShellPanel, "ADMIN_SHELL");

            setContentPane(root);
            showLogin();
        }

        void showLogin() {
            currentStudent = null;
            currentCompany = null;
            currentAdmin = null;

            cards.show(root, "LOGIN");
            loginPanel.reset();
        }

        void showRegister() {
            cards.show(root, "REGISTER");
            registrationPanel.reset();
        }

        void showStudentShell(Student student) {
            currentStudent = student;
            currentCompany = null;
            currentAdmin = null;

            studentShellPanel.refreshForStudent(student);
            cards.show(root, "STUDENT_SHELL");
        }

        void showCompanyShell(Company company) {
            currentCompany = company;
            currentStudent = null;
            currentAdmin = null;

            companyShellPanel.refreshForCompany(company);
            cards.show(root, "COMPANY_SHELL");
        }

        void showAdminShell(Admin admin) {
            currentAdmin = admin;
            currentStudent = null;
            currentCompany = null;

            adminShellPanel.refreshForAdmin(admin);
            cards.show(root, "ADMIN_SHELL");
        }
    }

    // ==================== LOGIN PANEL (FULL-WINDOW) ====================
    static class LoginPanel extends JPanel {
        private final MainFrame frame;

        private final JRadioButton rbStudent = new JRadioButton("STUDENT");
        private final JRadioButton rbCompany = new JRadioButton("COMPANY");
        private final JRadioButton rbAdmin = new JRadioButton("ADMIN");
        private final ButtonGroup roleGroup = new ButtonGroup();

        private final JTextField txtIdentifier = new JTextField();
        private final JPasswordField txtPass = new JPasswordField();
        private final JLabel lblError = new JLabel(" ");

        private final JLabel title = new JLabel("Student Login");
        private final JLabel sub = new JLabel("Enter your email and password to continue.");

        LoginPanel(MainFrame frame) {
            this.frame = frame;

            setLayout(new BorderLayout());
            setBackground(new Color(245, 247, 250));

            JPanel page = new JPanel(new BorderLayout());
            page.setOpaque(false);
            page.setBorder(new EmptyBorder(26, 26, 26, 26));

            // Header (full width)
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(Color.WHITE);
            header.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 235, 242)),
                    new EmptyBorder(18, 18, 18, 18)
            ));

            JLabel brand = new JLabel("Student Placement System");
            brand.setFont(new Font("SansSerif", Font.BOLD, 16));

            JLabel hint = new JLabel("Frontend-only demo (mock data)");
            hint.setForeground(new Color(120, 120, 120));
            hint.setFont(new Font("SansSerif", Font.PLAIN, 12));

            header.add(brand, BorderLayout.WEST);
            header.add(hint, BorderLayout.EAST);

            // Body (fills all remaining space)
            JPanel body = new JPanel(new GridBagLayout());
            body.setOpaque(false);

            JPanel formCard = buildLoginFormCard();
            formCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 235, 242)),
                    new EmptyBorder(22, 22, 22, 22)
            ));
            formCard.setBackground(Color.WHITE);

            // Let card grow with window (instead of fixed preferred size)
            formCard.setMinimumSize(new Dimension(520, 420));

            GridBagConstraints gc = new GridBagConstraints();
            gc.gridx = 0;
            gc.gridy = 0;
            gc.weightx = 1;
            gc.weighty = 1;
            gc.fill = GridBagConstraints.BOTH; // <-- this makes it take the space
            body.add(formCard, gc);

            page.add(header, BorderLayout.NORTH);
            page.add(body, BorderLayout.CENTER);

            add(page, BorderLayout.CENTER);

            updateLoginTitleByRole();
        }

        private JPanel buildLoginFormCard() {
            JPanel card = new JPanel(new GridBagLayout());
            card.setOpaque(true);

            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(8, 8, 8, 8);
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.weightx = 1;

            title.setFont(new Font("SansSerif", Font.BOLD, 26));
            sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
            sub.setForeground(new Color(90, 90, 90));

            // row 0: title
            gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
            card.add(title, gc);

            // row 1: sub
            gc.gridy++;
            card.add(sub, gc);

            // row 2: role label
            gc.gridy++;
            gc.gridwidth = 2;
            card.add(label("Role"), gc);

            // row 3: role radios
            gc.gridy++;
            JPanel roleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            roleRow.setBackground(Color.WHITE);

            setupRoleRadio(rbStudent);
            setupRoleRadio(rbCompany);
            setupRoleRadio(rbAdmin);

            roleGroup.add(rbStudent);
            roleGroup.add(rbCompany);
            roleGroup.add(rbAdmin);

            rbStudent.setSelected(true);
            rbStudent.addActionListener(e -> updateLoginTitleByRole());
            rbCompany.addActionListener(e -> updateLoginTitleByRole());
            rbAdmin.addActionListener(e -> updateLoginTitleByRole());

            roleRow.add(rbStudent);
            roleRow.add(rbCompany);
            roleRow.add(rbAdmin);
            card.add(roleRow, gc);

            // row 4: identifier label
            gc.gridy++;
            card.add(label("Username or Email"), gc);

            // row 5: identifier field
            gc.gridy++;
            styleField(txtIdentifier);
            card.add(txtIdentifier, gc);

            // row 6: password label
            gc.gridy++;
            card.add(label("Password"), gc);

            // row 7: password field
            gc.gridy++;
            styleField(txtPass);
            card.add(txtPass, gc);

            // row 8: error
            gc.gridy++;
            lblError.setForeground(new Color(190, 40, 40));
            lblError.setFont(new Font("SansSerif", Font.PLAIN, 12));
            card.add(lblError, gc);

            // row 9: buttons (login + register)
            gc.gridy++;
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            actions.setBackground(Color.WHITE);

            JButton btnLogin = new JButton("Login");
            stylePrimary(btnLogin);
            btnLogin.addActionListener(e -> doLogin());

            JButton btnRegister = new JButton("Register");
            styleSecondary(btnRegister);
            btnRegister.addActionListener(e -> frame.showRegister());

            actions.add(btnLogin);
            actions.add(btnRegister);

            card.add(actions, gc);

            // Spacer to allow vertical stretching nicely
            gc.gridy++;
            gc.weighty = 1;
            gc.fill = GridBagConstraints.BOTH;
            card.add(Box.createVerticalGlue(), gc);

            return card;
        }

        void reset() {
            txtIdentifier.setText("");
            txtPass.setText("");
            lblError.setText(" ");
            rbStudent.setSelected(true);
            updateLoginTitleByRole();
        }

        private void updateLoginTitleByRole() {
            if (rbStudent.isSelected()) title.setText("Student Login");
            else if (rbCompany.isSelected()) title.setText("Company Login");
            else title.setText("Admin Login");
        }

        private void doLogin() {
            String identifier = txtIdentifier.getText().trim();
            String pass = new String(txtPass.getPassword());

            if (identifier.isEmpty() || pass.isEmpty()) {
                lblError.setText("Please enter username/email and password.");
                return;
            }

            if (rbStudent.isSelected()) {
                Student s = MockAuthService.loginStudent(identifier, pass);
                if (s == null) lblError.setText("Invalid student credentials.");
                else { lblError.setText(" "); frame.showStudentShell(s); }
                return;
            }

            if (rbCompany.isSelected()) {
                Company c = MockAuthService.loginCompany(identifier, pass);
                if (c == null) lblError.setText("Invalid company credentials.");
                else { lblError.setText(" "); frame.showCompanyShell(c); }
                return;
            }

            Admin a = MockAuthService.loginAdmin(identifier, pass);
            if (a == null) lblError.setText("Invalid admin credentials.");
            else { lblError.setText(" "); frame.showAdminShell(a); }
        }

        private void setupRoleRadio(JRadioButton rb) {
            rb.setBackground(Color.WHITE);
            rb.setFocusPainted(false);
            rb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            rb.setFont(new Font("SansSerif", Font.PLAIN, 12));
        }

        private JLabel label(String text) {
            JLabel l = new JLabel(text);
            l.setFont(new Font("SansSerif", Font.PLAIN, 12));
            l.setForeground(new Color(60, 60, 60));
            return l;
        }

        private void styleField(JComponent c) {
            c.setFont(new Font("SansSerif", Font.PLAIN, 13));
            c.setPreferredSize(new Dimension(10, 36));
            c.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 225, 232)),
                    new EmptyBorder(8, 10, 8, 10)
            ));
        }

        private void stylePrimary(JButton b) {
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        }

        private void styleSecondary(JButton b) {
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        }
    }

    // ==================== REGISTRATION PANEL (FULL-WINDOW) ====================
    static class RegistrationPanel extends JPanel {
        private final MainFrame frame;

        private final JRadioButton rbStudent = new JRadioButton("STUDENT");
        private final JRadioButton rbCompany = new JRadioButton("COMPANY");
        private final ButtonGroup roleGroup = new ButtonGroup();

        private final JLabel title = new JLabel("Student Registration");
        private final JLabel sub = new JLabel("Provide your details to create an account.");
        private final JLabel lblError = new JLabel(" ");

        private final CardLayout regCards = new CardLayout();
        private final JPanel regRoot = new JPanel(regCards);

        // Student fields
        private final JTextField txtFirst = new JTextField();
        private final JTextField txtLast = new JTextField();
        private final JTextField txtEmail = new JTextField();
        private final JPasswordField txtPass = new JPasswordField();
        private final JPasswordField txtConfirm = new JPasswordField();
        private final JTextField txtFaculty = new JTextField();
        private final JTextField txtCourse = new JTextField();
        private final JTextField txtYear = new JTextField();

        // Company fields
        private final JTextField txtCompanyName = new JTextField();
        private final JTextField txtCompanyEmail = new JTextField();
        private final JPasswordField txtCompanyPass = new JPasswordField();
        private final JPasswordField txtCompanyConfirm = new JPasswordField();

        RegistrationPanel(MainFrame frame) {
            this.frame = frame;

            setLayout(new BorderLayout());
            setBackground(new Color(245, 247, 250));

            JPanel page = new JPanel(new BorderLayout());
            page.setOpaque(false);
            page.setBorder(new EmptyBorder(26, 26, 26, 26));

            // Header full width
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(Color.WHITE);
            header.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 235, 242)),
                    new EmptyBorder(18, 18, 18, 18)
            ));

            title.setFont(new Font("SansSerif", Font.BOLD, 26));
            sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
            sub.setForeground(new Color(90, 90, 90));

            JPanel headerLeft = new JPanel();
            headerLeft.setBackground(Color.WHITE);
            headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS));
            headerLeft.add(title);
            headerLeft.add(Box.createVerticalStrut(6));
            headerLeft.add(sub);

            JButton btnBack = new JButton("Back to Login");
            btnBack.setFocusPainted(false);
            btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnBack.addActionListener(e -> frame.showLogin());

            header.add(headerLeft, BorderLayout.WEST);
            header.add(btnBack, BorderLayout.EAST);

            // Main body (fills window)
            JPanel body = new JPanel(new BorderLayout());
            body.setOpaque(false);

            JPanel card = new JPanel(new BorderLayout(0, 12));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 235, 242)),
                    new EmptyBorder(18, 18, 18, 18)
            ));

            // Role row
            JPanel roleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            roleRow.setBackground(Color.WHITE);

            setupRoleRadio(rbStudent);
            setupRoleRadio(rbCompany);
            roleGroup.add(rbStudent);
            roleGroup.add(rbCompany);

            rbStudent.setSelected(true);
            rbStudent.addActionListener(e -> switchRole());
            rbCompany.addActionListener(e -> switchRole());

            roleRow.add(label("Role:"));
            roleRow.add(rbStudent);
            roleRow.add(rbCompany);

            card.add(roleRow, BorderLayout.NORTH);

            // Forms in a scroll that fills remaining space
            regRoot.setBackground(Color.WHITE);
            regRoot.add(buildStudentForm(), "STUDENT");
            regRoot.add(buildCompanyForm(), "COMPANY");

            JScrollPane sc = new JScrollPane(regRoot,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            sc.setBorder(null);

            card.add(sc, BorderLayout.CENTER);

            // Bottom actions
            JPanel bottom = new JPanel(new BorderLayout());
            bottom.setBackground(Color.WHITE);

            lblError.setForeground(new Color(190, 40, 40));
            lblError.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblError.setBorder(new EmptyBorder(6, 0, 0, 0));

            JButton btnRegister = new JButton("Register");
            btnRegister.setFocusPainted(false);
            btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnRegister.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
            btnRegister.addActionListener(e -> doRegistration());

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            actions.setBackground(Color.WHITE);
            actions.add(btnRegister);

            bottom.add(lblError, BorderLayout.NORTH);
            bottom.add(actions, BorderLayout.WEST);

            card.add(bottom, BorderLayout.SOUTH);

            body.add(card, BorderLayout.CENTER);

            page.add(header, BorderLayout.NORTH);
            page.add(body, BorderLayout.CENTER);

            add(page, BorderLayout.CENTER);

            switchRole();
        }

        void reset() {
            rbStudent.setSelected(true);

            txtFirst.setText("");
            txtLast.setText("");
            txtEmail.setText("");
            txtPass.setText("");
            txtConfirm.setText("");
            txtFaculty.setText("");
            txtCourse.setText("");
            txtYear.setText("");

            txtCompanyName.setText("");
            txtCompanyEmail.setText("");
            txtCompanyPass.setText("");
            txtCompanyConfirm.setText("");

            lblError.setText(" ");
            switchRole();
        }

        private void switchRole() {
            lblError.setText(" ");
            if (rbStudent.isSelected()) {
                title.setText("Student Registration");
                regCards.show(regRoot, "STUDENT");
            } else {
                title.setText("Company Registration");
                regCards.show(regRoot, "COMPANY");
            }
        }

        private JPanel buildStudentForm() {
            JPanel p = new JPanel(new GridBagLayout());
            p.setBackground(Color.WHITE);

            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(8, 8, 8, 8);
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.weightx = 1;

            int r = 0;

            addField(p, gc, r++, "First Name", txtFirst);
            addField(p, gc, r++, "Last Name", txtLast);
            addField(p, gc, r++, "Email", txtEmail);
            addField(p, gc, r++, "Password", txtPass);
            addField(p, gc, r++, "Confirm Password", txtConfirm);
            addField(p, gc, r++, "Faculty", txtFaculty);
            addField(p, gc, r++, "Course", txtCourse);
            addField(p, gc, r++, "Year of Study", txtYear);

            // spacer to allow nice filling in scroll
            gc.gridx = 0; gc.gridy = r; gc.gridwidth = 2;
            gc.weighty = 1;
            gc.fill = GridBagConstraints.BOTH;
            p.add(Box.createVerticalGlue(), gc);

            return p;
        }

        private JPanel buildCompanyForm() {
            JPanel p = new JPanel(new GridBagLayout());
            p.setBackground(Color.WHITE);

            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(8, 8, 8, 8);
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.weightx = 1;

            int r = 0;

            addField(p, gc, r++, "Company Name", txtCompanyName);
            addField(p, gc, r++, "Company Email", txtCompanyEmail);
            addField(p, gc, r++, "Password", txtCompanyPass);
            addField(p, gc, r++, "Confirm Password", txtCompanyConfirm);

            gc.gridx = 0; gc.gridy = r; gc.gridwidth = 2;
            gc.weighty = 1;
            gc.fill = GridBagConstraints.BOTH;
            p.add(Box.createVerticalGlue(), gc);

            return p;
        }

        private void addField(JPanel p, GridBagConstraints gc, int row, String lab, JComponent field) {
            gc.gridy = row;

            gc.gridx = 0;
            gc.gridwidth = 1;
            gc.weightx = 0.25;
            p.add(label(lab), gc);

            gc.gridx = 1;
            gc.weightx = 0.75;
            styleField(field);
            p.add(field, gc);
        }

        private void doRegistration() {
            if (rbStudent.isSelected()) doStudentRegistration();
            else doCompanyRegistration();
        }

        private void doStudentRegistration() {
            String first = txtFirst.getText().trim();
            String last = txtLast.getText().trim();
            String email = txtEmail.getText().trim();
            String pass = new String(txtPass.getPassword());
            String conf = new String(txtConfirm.getPassword());
            String faculty = txtFaculty.getText().trim();
            String course = txtCourse.getText().trim();
            String yearStr = txtYear.getText().trim();

            if (first.isEmpty() || last.isEmpty() || email.isEmpty() || pass.isEmpty() || conf.isEmpty()
                    || faculty.isEmpty() || course.isEmpty() || yearStr.isEmpty()) {
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
            Student newStudent = new Student(
                    newId, fullName, email, pass,
                    course, faculty, String.valueOf(year), 0.0
            );

            MockDB.students.add(newStudent);

            JOptionPane.showMessageDialog(this,
                    "Student registration successful. You can now login.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            frame.showLogin();
        }

        private void doCompanyRegistration() {
            String name = txtCompanyName.getText().trim();
            String email = txtCompanyEmail.getText().trim();
            String pass = new String(txtCompanyPass.getPassword());
            String conf = new String(txtCompanyConfirm.getPassword());

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || conf.isEmpty()) {
                lblError.setText("All fields are required.");
                return;
            }
            if (!pass.equals(conf)) {
                lblError.setText("Passwords do not match.");
                return;
            }

            for (Company c : MockDB.companies) {
                if (c.email.equalsIgnoreCase(email)) {
                    lblError.setText("Company email already in use.");
                    return;
                }
            }

            String newId = "C" + String.format("%03d", MockDB.companies.size() + 1);
            Company newCompany = new Company(newId, name, email, pass);
            MockDB.companies.add(newCompany);

            JOptionPane.showMessageDialog(this,
                    "Company registration successful. You can now login.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            frame.showLogin();
        }

        private void setupRoleRadio(JRadioButton rb) {
            rb.setBackground(Color.WHITE);
            rb.setFocusPainted(false);
            rb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            rb.setFont(new Font("SansSerif", Font.PLAIN, 12));
        }

        private JLabel label(String text) {
            JLabel l = new JLabel(text);
            l.setFont(new Font("SansSerif", Font.PLAIN, 12));
            l.setForeground(new Color(60, 60, 60));
            return l;
        }

        private void styleField(JComponent c) {
            c.setFont(new Font("SansSerif", Font.PLAIN, 13));
            c.setPreferredSize(new Dimension(10, 36));
            c.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 225, 232)),
                    new EmptyBorder(8, 10, 8, 10)
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

    // ==================== COMPANY SHELL (Placeholder) ====================
    static class CompanyShellPanel extends JPanel {
        private final MainFrame frame;
        private final JLabel lbl = new JLabel("Company Dashboard (placeholder)");

        CompanyShellPanel(MainFrame frame) {
            this.frame = frame;
            setLayout(new BorderLayout());
            setBackground(new Color(245, 247, 250));

            lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
            lbl.setBorder(new EmptyBorder(24, 24, 24, 24));

            JButton logout = new JButton("Logout");
            logout.setFocusPainted(false);
            logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            logout.addActionListener(e -> frame.showLogin());

            JPanel top = new JPanel(new BorderLayout());
            top.setBackground(Color.WHITE);
            top.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 235, 242)));
            top.setPreferredSize(new Dimension(0, 56));
            top.add(lbl, BorderLayout.WEST);

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            right.setBackground(Color.WHITE);
            right.add(logout);

            top.add(right, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);

            JLabel msg = new JLabel("<html>This is a placeholder screen.<br/>You only implemented Login + Registration for now.</html>");
            msg.setBorder(new EmptyBorder(60, 24, 24, 24));
            add(msg, BorderLayout.CENTER);
        }

        void refreshForCompany(Company c) {
            lbl.setText("Welcome, " + c.companyName + " (Company)");
        }
    }

    // ==================== ADMIN SHELL (Placeholder) ====================
    static class AdminShellPanel extends JPanel {
        private final MainFrame frame;
        private final JLabel lbl = new JLabel("Admin Dashboard (placeholder)");

        AdminShellPanel(MainFrame frame) {
            this.frame = frame;
            setLayout(new BorderLayout());
            setBackground(new Color(245, 247, 250));

            lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
            lbl.setBorder(new EmptyBorder(24, 24, 24, 24));

            JButton logout = new JButton("Logout");
            logout.setFocusPainted(false);
            logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            logout.addActionListener(e -> frame.showLogin());

            JPanel top = new JPanel(new BorderLayout());
            top.setBackground(Color.WHITE);
            top.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 235, 242)));
            top.setPreferredSize(new Dimension(0, 56));
            top.add(lbl, BorderLayout.WEST);

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            right.setBackground(Color.WHITE);
            right.add(logout);

            top.add(right, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);

            JLabel msg = new JLabel("<html>This is a placeholder screen.<br/>Admin accounts are pre-created only.</html>");
            msg.setBorder(new EmptyBorder(60, 24, 24, 24));
            add(msg, BorderLayout.CENTER);
        }

        void refreshForAdmin(Admin a) {
            lbl.setText("Welcome, " + a.username + " (Admin)");
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
            workingCopy = new Student(s);
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

            if (!editing) lblMsg.setText(" ");
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

    static class Student {
        String id;
        String fullName;
        String email;
        String password;
        String course;
        String branch;   // faculty
        String section;  // year
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

    static class Company {
        String id;
        String companyName;
        String email;
        String password;

        Company(String id, String companyName, String email, String password) {
            this.id = id;
            this.companyName = companyName;
            this.email = email;
            this.password = password;
        }
    }

    static class Admin {
        String username;
        String password;

        Admin(String username, String password) {
            this.username = username;
            this.password = password;
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

    static class MockDB {
        static final List<Student> students = new ArrayList<>();
        static final List<Company> companies = new ArrayList<>();
        static final List<Admin> admins = new ArrayList<>();
        static final List<ApplicationRecord> applications = new ArrayList<>();

        static {
            students.add(new Student("S001", "Tony Student", "tony@uom.mu", "1234",
                    "BSc (Hons) Computer Science", "Software Eng", "2", 3.45));

            companies.add(new Company("C001", "TechNova Ltd", "hr@technova.mu", "1234"));

            admins.add(new Admin("admin", "1234"));

            applications.add(new ApplicationRecord("S001", "TechNova Ltd", "Software Intern",
                    LocalDate.now().minusDays(8), ApplicationStatus.APPLIED));
        }
    }

    static class MockAuthService {
        static Student loginStudent(String identifier, String password) {
            for (Student s : MockDB.students) {
                if ((s.email.equalsIgnoreCase(identifier) || s.fullName.equalsIgnoreCase(identifier))
                        && s.password.equals(password)) {
                    return s;
                }
            }
            return null;
        }

        static Company loginCompany(String identifier, String password) {
            for (Company c : MockDB.companies) {
                if ((c.email.equalsIgnoreCase(identifier) || c.companyName.equalsIgnoreCase(identifier))
                        && c.password.equals(password)) {
                    return c;
                }
            }
            return null;
        }

        static Admin loginAdmin(String identifier, String password) {
            for (Admin a : MockDB.admins) {
                if (a.username.equalsIgnoreCase(identifier) && a.password.equals(password)) {
                    return a;
                }
            }
            return null;
        }
    }

    static class MockStudentService {
        static void updateStudent(Student updated) {
            for (int i = 0; i < MockDB.students.size(); i++) {
                if (MockDB.students.get(i).id.equals(updated.id)) {
                    MockDB.students.set(i, updated);
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