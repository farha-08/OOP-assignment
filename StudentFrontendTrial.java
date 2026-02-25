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

    // ===== UI THEME (matches screenshots) =====
    static final Color MAIN_BG = new Color(0xCF, 0xCF, 0xCF);      // #CFCFCF
    static final Color ACCENT = new Color(0x54, 0x54, 0x54);       // #545454
    static final Color BTN = new Color(0x7D, 0x7D, 0x7D);          // #7D7D7D
    static final Color CARD_BG = new Color(0xE6, 0xE3, 0xD6);      // beige-grey like screenshot
    static final Color BORDER = new Color(0x9A, 0x9A, 0x9A);

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
            setBackground(MAIN_BG);

            JPanel page = new JPanel(new BorderLayout());
            page.setOpaque(false);
            page.setBorder(new EmptyBorder(26, 26, 26, 26));

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

            JPanel body = new JPanel(new GridBagLayout());
            body.setOpaque(false);

            JPanel formCard = buildLoginFormCard();
            formCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 235, 242)),
                    new EmptyBorder(22, 22, 22, 22)
            ));
            formCard.setBackground(Color.WHITE);
            formCard.setMinimumSize(new Dimension(520, 420));

            GridBagConstraints gc = new GridBagConstraints();
            gc.gridx = 0;
            gc.gridy = 0;
            gc.weightx = 1;
            gc.weighty = 1;
            gc.fill = GridBagConstraints.BOTH;
            body.add(formCard, gc);

            page.add(header, BorderLayout.NORTH);
            page.add(body, BorderLayout.CENTER);

            add(page, BorderLayout.CENTER);

            updateLoginTitleByRole();
        }

        private JPanel buildLoginFormCard() {
            JPanel card = new JPanel(new GridBagLayout());
            card.setOpaque(true);
            card.setBackground(Color.WHITE);

            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(8, 8, 8, 8);
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.weightx = 1;

            title.setFont(new Font("SansSerif", Font.BOLD, 26));
            sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
            sub.setForeground(new Color(90, 90, 90));

            gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
            card.add(title, gc);

            gc.gridy++;
            card.add(sub, gc);

            gc.gridy++;
            gc.gridwidth = 2;
            card.add(label("Role"), gc);

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

            gc.gridy++;
            card.add(label("Username or Email"), gc);

            gc.gridy++;
            styleField(txtIdentifier);
            card.add(txtIdentifier, gc);

            gc.gridy++;
            card.add(label("Password"), gc);

            gc.gridy++;
            styleField(txtPass);
            card.add(txtPass, gc);

            gc.gridy++;
            lblError.setForeground(new Color(190, 40, 40));
            lblError.setFont(new Font("SansSerif", Font.PLAIN, 12));
            card.add(lblError, gc);

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
            setBackground(MAIN_BG);

            JPanel page = new JPanel(new BorderLayout());
            page.setOpaque(false);
            page.setBorder(new EmptyBorder(26, 26, 26, 26));

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

            JPanel body = new JPanel(new BorderLayout());
            body.setOpaque(false);

            JPanel card = new JPanel(new BorderLayout(0, 12));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 235, 242)),
                    new EmptyBorder(18, 18, 18, 18)
            ));

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

            regRoot.setBackground(Color.WHITE);
            regRoot.add(buildStudentForm(), "STUDENT");
            regRoot.add(buildCompanyForm(), "COMPANY");

            JScrollPane sc = new JScrollPane(regRoot,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            sc.setBorder(null);

            card.add(sc, BorderLayout.CENTER);

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
            try { year = Integer.parseInt(yearStr); }
            catch (Exception ex) { lblError.setText("Year must be a number."); return; }

            String fullName = first + " " + last;

            for (Student s : MockDB.students) {
                if (s.fullName.equalsIgnoreCase(fullName)) { lblError.setText("A student with that name already exists."); return; }
                if (s.email.equalsIgnoreCase(email)) { lblError.setText("Email already in use."); return; }
            }

            String newId = "S" + String.format("%03d", MockDB.students.size() + 1);
            Student newStudent = new Student(newId, fullName, email, pass,
                    course, faculty, String.valueOf(year), 0.0);

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
                if (c.email.equalsIgnoreCase(email)) { lblError.setText("Company email already in use."); return; }
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

    // ==================== STUDENT SHELL (Top Nav like screenshots) ====================
    static class StudentShellPanel extends JPanel {
        private final MainFrame frame;

        private final CardLayout pageCards = new CardLayout();
        private final JPanel pageRoot = new JPanel(pageCards);

        private final JLabel lblTitle = new JLabel();
        private final JLabel lblFooter = new JLabel("Logged in as: -");

        // Pages
        private final StudentDashboardPanel dashboardPanel = new StudentDashboardPanel();
        private final StudentProfilePanel profilePanel = new StudentProfilePanel();
        private final StudentApplicationsPanel appsPanel = new StudentApplicationsPanel();
        private final StudentPolicyPanel policyPanel = new StudentPolicyPanel();

        // Offers panel is done by your friend → plug it later
        private final JPanel offersPlaceholder = new JPanel(new BorderLayout());

        StudentShellPanel(MainFrame frame) {
            this.frame = frame;
            setLayout(new BorderLayout());
            setBackground(MAIN_BG);

            add(buildTopHeader(), BorderLayout.NORTH);
            add(buildCenter(), BorderLayout.CENTER);
            add(buildFooter(), BorderLayout.SOUTH);

            offersPlaceholder.setBackground(CARD_BG);
            offersPlaceholder.setBorder(BorderFactory.createLineBorder(BORDER));
            JLabel msg = new JLabel("Offers screen is implemented by another teammate.");
            msg.setBorder(new EmptyBorder(16, 16, 16, 16));
            offersPlaceholder.add(msg, BorderLayout.NORTH);
        }

        void refreshForStudent(Student s) {
            lblTitle.setText("Student Placement System – " + s.fullName);
            lblFooter.setText("Logged in as: " + s.email + " (Student)");

            dashboardPanel.loadStudent(s);
            profilePanel.loadStudent(s);
            appsPanel.loadApplicationsForStudent(s.id);

            showPage("DASH");
        }

        void showPage(String key) {
            pageCards.show(pageRoot, key);
        }

        private JPanel buildTopHeader() {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setBackground(MAIN_BG);

            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(ACCENT);
            header.setBorder(new EmptyBorder(6, 10, 6, 10));

            lblTitle.setForeground(Color.WHITE);
            lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
            header.add(lblTitle, BorderLayout.WEST);

            JButton btnLogout = greyButton("Log Out");
            btnLogout.addActionListener(e -> frame.showLogin());

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            right.setBackground(ACCENT);
            right.add(btnLogout);
            header.add(right, BorderLayout.EAST);

            JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
            nav.setBackground(MAIN_BG);
            nav.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

            JButton bDash = navButton("Dashboard");
            JButton bProfile = navButton("Profile");
            JButton bOffers = navButton("Offers");
            JButton bApps = navButton("Applications");
            JButton bPolicy = navButton("Policy");

            bDash.addActionListener(e -> showPage("DASH"));
            bProfile.addActionListener(e -> showPage("PROFILE"));
            bOffers.addActionListener(e -> showPage("OFFERS"));
            bApps.addActionListener(e -> showPage("APPS"));
            bPolicy.addActionListener(e -> showPage("POLICY"));

            nav.add(bDash);
            nav.add(bProfile);
            nav.add(bOffers);
            nav.add(bApps);
            nav.add(bPolicy);

            wrapper.add(header, BorderLayout.NORTH);
            wrapper.add(nav, BorderLayout.CENTER);

            return wrapper;
        }

        private JPanel buildCenter() {
            JPanel center = new JPanel(new BorderLayout());
            center.setBackground(MAIN_BG);
            center.setBorder(new EmptyBorder(10, 10, 10, 10));

            pageRoot.setBackground(MAIN_BG);
            pageRoot.add(dashboardPanel, "DASH");
            pageRoot.add(profilePanel, "PROFILE");
            pageRoot.add(offersPlaceholder, "OFFERS"); // replace with teammate panel later
            pageRoot.add(appsPanel, "APPS");
            pageRoot.add(policyPanel, "POLICY");

            center.add(pageRoot, BorderLayout.CENTER);
            return center;
        }

        private JPanel buildFooter() {
            JPanel foot = new JPanel(new BorderLayout());
            foot.setBackground(MAIN_BG);
            foot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
            lblFooter.setBorder(new EmptyBorder(4, 8, 4, 8));
            foot.add(lblFooter, BorderLayout.WEST);
            return foot;
        }
    }

    // ==================== DASHBOARD (matches screenshot layout) ====================
    static class StudentDashboardPanel extends JPanel {
        private Student student;

        private final JLabel lblWelcome = new JLabel("Welcome back, -");
        private final JLabel lblAppsCount = new JLabel("0");
        private final JLabel lblOffersCount = new JLabel("0");
        private final JLabel lblPlacement = new JLabel("Not Placed");

        private final JPanel recentList = new JPanel();

        StudentDashboardPanel() {
            setLayout(new BorderLayout());
            setBackground(MAIN_BG);

            JPanel container = new JPanel();
            container.setBackground(MAIN_BG);
            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

            container.add(sectionBar("Dashboard"));

            JPanel topArea = new JPanel();
            topArea.setBackground(CARD_BG);
            topArea.setBorder(BorderFactory.createLineBorder(BORDER));
            topArea.setLayout(new BoxLayout(topArea, BoxLayout.Y_AXIS));
            topArea.setAlignmentX(Component.LEFT_ALIGNMENT);

            lblWelcome.setBorder(new EmptyBorder(10, 10, 6, 10));
            lblWelcome.setFont(new Font("SansSerif", Font.PLAIN, 13));
            topArea.add(lblWelcome);

            JPanel cards = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            cards.setBackground(CARD_BG);

            cards.add(statCard("My Applications", lblAppsCount, "Total applications"));
            cards.add(statCard("Active Offers", lblOffersCount, "Available offers"));
            cards.add(statusCard("Placement Status", lblPlacement, "Keep applying!"));

            topArea.add(cards);
            container.add(topArea);

            container.add(Box.createVerticalStrut(10));

            container.add(sectionBar("Quick Links"));
            JPanel quick = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            quick.setBackground(CARD_BG);
            quick.setBorder(BorderFactory.createLineBorder(BORDER));
            quick.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton bBrowse = linkBtn("Browse Offers");
            JButton bMyApps = linkBtn("My Applications");
            JButton bEdit = linkBtn("Edit Profile");

            bBrowse.addActionListener(e -> switchTo("OFFERS"));
            bMyApps.addActionListener(e -> switchTo("APPS"));
            bEdit.addActionListener(e -> switchTo("PROFILE"));

            quick.add(bBrowse);
            quick.add(bMyApps);
            quick.add(bEdit);

            container.add(quick);

            container.add(Box.createVerticalStrut(10));

            container.add(sectionBar("Recent Applications"));
            recentList.setLayout(new BoxLayout(recentList, BoxLayout.Y_AXIS));
            recentList.setBackground(CARD_BG);
            recentList.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER),
                    new EmptyBorder(10, 10, 10, 10)
            ));
            recentList.setAlignmentX(Component.LEFT_ALIGNMENT);

            container.add(recentList);

            add(container, BorderLayout.NORTH);
        }

        void loadStudent(Student s) {
            this.student = s;
            lblWelcome.setText("Welcome back, " + s.fullName + "!");
            refreshStatsAndRecent();
        }

        private void refreshStatsAndRecent() {
            if (student == null) return;

            List<ApplicationView> apps = MockApplicationService.getApplicationsForStudent(student.id);
            lblAppsCount.setText(String.valueOf(apps.size()));

            // Offers count: teammate will handle real offers; keep a mock number for UI
            lblOffersCount.setText("6");

            boolean placed = false;
            for (ApplicationView a : apps) {
                if (a.status == ApplicationStatus.ACCEPTED) { placed = true; break; }
            }
            lblPlacement.setText(placed ? "Placed" : "Not Placed");

            recentList.removeAll();
            int shown = 0;
            for (ApplicationView a : apps) {
                recentList.add(recentRow(a.offerTitle, a.companyName, a.status));
                shown++;
                if (shown == 2) break;
            }
            if (apps.isEmpty()) {
                JLabel none = new JLabel("No recent applications.");
                recentList.add(none);
            }

            recentList.revalidate();
            recentList.repaint();
        }

        private JPanel sectionBar(String text) {
            JPanel bar = new JPanel(new BorderLayout());
            bar.setBackground(ACCENT);
            bar.setBorder(new EmptyBorder(4, 8, 4, 8));
            bar.add(headerLabel(text), BorderLayout.WEST);
            bar.setAlignmentX(Component.LEFT_ALIGNMENT);
            return bar;
        }

        private JPanel statCard(String title, JLabel big, String sub) {
            JPanel p = titledBlock(title);
            p.setPreferredSize(new Dimension(240, 95));
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            big.setFont(new Font("SansSerif", Font.BOLD, 28));
            big.setBorder(new EmptyBorder(2, 8, 0, 8));
            JLabel s = new JLabel(sub);
            s.setBorder(new EmptyBorder(0, 10, 8, 10));
            p.add(big);
            p.add(s);
            return p;
        }

        private JPanel statusCard(String title, JLabel big, String sub) {
            JPanel p = titledBlock(title);
            p.setPreferredSize(new Dimension(240, 95));
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            big.setFont(new Font("SansSerif", Font.BOLD, 18));
            big.setForeground(new Color(90, 90, 90));
            big.setBorder(new EmptyBorder(10, 10, 0, 10));
            JLabel s = new JLabel(sub);
            s.setBorder(new EmptyBorder(0, 10, 8, 10));
            p.add(big);
            p.add(s);
            return p;
        }

        private JPanel recentRow(String role, String company, ApplicationStatus status) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(CARD_BG);
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER),
                    new EmptyBorder(8, 10, 8, 10)
            ));

            JLabel left = new JLabel("<html><b>" + role + "</b> at " + company + "</html>");
            row.add(left, BorderLayout.WEST);

            JLabel tag = new JLabel(pretty(status));
            tag.setOpaque(true);
            tag.setBorder(new EmptyBorder(2, 8, 2, 8));
            tag.setForeground(Color.WHITE);
            tag.setBackground(status == ApplicationStatus.SHORTLISTED ? ACCENT : BTN);
            row.add(tag, BorderLayout.EAST);

            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            return row;
        }

        private JButton linkBtn(String text) {
            JButton b = new JButton(text);
            b.setFocusPainted(false);
            b.setBackground(MAIN_BG);
            b.setBorder(BorderFactory.createLineBorder(BORDER));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return b;
        }

        private String pretty(ApplicationStatus s) {
            String t = s.name().toLowerCase();
            return Character.toUpperCase(t.charAt(0)) + t.substring(1);
        }

        private void switchTo(String key) {
            Container c = this;
            while (c != null && !(c instanceof StudentShellPanel)) c = c.getParent();
            if (c instanceof StudentShellPanel shell) shell.showPage(key);
        }
    }

    // ==================== PROFILE (screenshot style, scrollable) ====================
    static class StudentProfilePanel extends JPanel {
        private Student workingCopy;

        private final JTextField txtFullName = new JTextField();
        private final JTextField txtEmail = new JTextField();
        private final JTextField txtUsername = new JTextField();
        private final JTextField txtPhone = new JTextField();

        private final JComboBox<String> cmbCourse = new JComboBox<>(new String[]{
                "B.Tech", "BSc (Hons) Computer Science", "BSc", "Other"
        });
        private final JComboBox<String> cmbBranch = new JComboBox<>(new String[]{
                "Computer Science", "Software Eng", "Information Systems", "Other"
        });
        private final JComboBox<String> cmbSection = new JComboBox<>(new String[]{"A", "B", "C"});
        private final JTextField txtCgpa = new JTextField();
        private final JTextField txtYear = new JTextField();

        private final JButton btnEdit = new JButton("Edit");
        private boolean editing = false;

        private final JPasswordField curPass = new JPasswordField();
        private final JPasswordField newPass = new JPasswordField();
        private final JPasswordField confPass = new JPasswordField();

        StudentProfilePanel() {
            setLayout(new BorderLayout());
            setBackground(MAIN_BG);

            JPanel page = new JPanel();
            page.setBackground(MAIN_BG);
            page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));

            page.add(sectionBar("My Profile"));

            JPanel personal = titledBlock("Personal Information");
            personal.setLayout(new GridBagLayout());
            addFormRows(personal,
                    "Full Name:", txtFullName,
                    "Email:", txtEmail,
                    "Username:", txtUsername,
                    "Phone:", txtPhone
            );

            JPanel academic = titledBlock("Academic Details");
            academic.setLayout(new GridBagLayout());
            addFormRows(academic,
                    "Course:", cmbCourse,
                    "Branch:", cmbBranch,
                    "Section:", cmbSection,
                    "CGPA:", txtCgpa,
                    "Year:", txtYear
            );

            JPanel editRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            editRow.setBackground(CARD_BG);
            btnEdit.setFocusPainted(false);
            btnEdit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnEdit.setBackground(MAIN_BG);
            btnEdit.setBorder(BorderFactory.createLineBorder(BORDER));
            btnEdit.addActionListener(e -> toggleEdit());
            editRow.add(btnEdit);

            page.add(wrapCard(personal));
            page.add(Box.createVerticalStrut(10));
            page.add(wrapCard(academic));
            page.add(Box.createVerticalStrut(10));
            page.add(wrapCard(editRow));

            page.add(Box.createVerticalStrut(12));
            page.add(sectionBar("Change Password"));

            JPanel pass = titledBlock("Password");
            pass.setLayout(new GridBagLayout());
            addFormRows(pass,
                    "Current Password:", curPass,
                    "New Password:", newPass,
                    "Confirm Password:", confPass
            );

            page.add(wrapCard(pass));

            JScrollPane sc = new JScrollPane(page);
            sc.setBorder(null);
            sc.getViewport().setBackground(MAIN_BG);

            add(sc, BorderLayout.CENTER);

            setEditing(false);
        }

        void loadStudent(Student s) {
            workingCopy = new Student(s);

            txtFullName.setText(workingCopy.fullName);
            txtEmail.setText(workingCopy.email);

            txtUsername.setText(workingCopy.fullName.toLowerCase().replace(" ", "_"));
            txtPhone.setText("555-0101");

            cmbCourse.setSelectedItem(workingCopy.course);
            cmbBranch.setSelectedItem(workingCopy.branch);
            cmbSection.setSelectedItem("A");
            txtCgpa.setText(String.valueOf(workingCopy.gpa));
            txtYear.setText(workingCopy.section);

            setEditing(false);
        }

        private void toggleEdit() {
            setEditing(!editing);

            if (!editing && workingCopy != null) {
                workingCopy.fullName = txtFullName.getText().trim();
                workingCopy.email = txtEmail.getText().trim();
                workingCopy.course = String.valueOf(cmbCourse.getSelectedItem());
                workingCopy.branch = String.valueOf(cmbBranch.getSelectedItem());
                workingCopy.section = txtYear.getText().trim();
                try { workingCopy.gpa = Double.parseDouble(txtCgpa.getText().trim()); } catch (Exception ignored) {}

                MockStudentService.updateStudent(workingCopy);
            }
        }

        private void setEditing(boolean on) {
            editing = on;
            btnEdit.setText(on ? "Save" : "Edit");

            txtFullName.setEditable(on);
            txtEmail.setEditable(on);
            txtUsername.setEditable(on);
            txtPhone.setEditable(on);

            cmbCourse.setEnabled(on);
            cmbBranch.setEnabled(on);
            cmbSection.setEnabled(on);
            txtCgpa.setEditable(on);
            txtYear.setEditable(on);
        }

        private JPanel sectionBar(String text) {
            JPanel bar = new JPanel(new BorderLayout());
            bar.setBackground(ACCENT);
            bar.setBorder(new EmptyBorder(4, 8, 4, 8));
            bar.add(headerLabel(text), BorderLayout.WEST);
            bar.setAlignmentX(Component.LEFT_ALIGNMENT);
            return bar;
        }

        private JPanel wrapCard(JComponent child) {
            JPanel wrap = new JPanel(new BorderLayout());
            wrap.setBackground(MAIN_BG);
            wrap.setBorder(new EmptyBorder(8, 8, 8, 8));
            wrap.add(child, BorderLayout.CENTER);
            wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
            return wrap;
        }

        private void addFormRows(JPanel p, Object... items) {
            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(6, 8, 6, 8);
            gc.fill = GridBagConstraints.HORIZONTAL;

            int r = 0;
            for (int i = 0; i < items.length; i += 2) {
                String label = (String) items[i];
                JComponent field = (JComponent) items[i + 1];

                gc.gridy = r;
                gc.gridx = 0;
                gc.weightx = 0.2;
                JLabel l = new JLabel(label);
                p.add(l, gc);

                gc.gridx = 1;
                gc.weightx = 0.8;

                field.setPreferredSize(new Dimension(10, 28));
                field.setBorder(BorderFactory.createLineBorder(BORDER));
                if (field instanceof JComboBox) ((JComboBox<?>) field).setBackground(Color.WHITE);

                p.add(field, gc);
                r++;
            }
        }
    }

    // ==================== APPLICATIONS (screenshot style) ====================
    static class StudentApplicationsPanel extends JPanel {
        private final DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Company", "Role", "Applied On", "Status"}, 0
        ) { public boolean isCellEditable(int r, int c) { return false; } };

        private final JTable table = new JTable(model);
        private final JLabel lblCount = new JLabel("0 application(s) total.");

        private String studentId;

        StudentApplicationsPanel() {
            setLayout(new BorderLayout());
            setBackground(MAIN_BG);

            JPanel page = new JPanel();
            page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
            page.setBackground(MAIN_BG);

            page.add(sectionBar("My Applications"));

            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(CARD_BG);
            card.setBorder(BorderFactory.createLineBorder(BORDER));

            table.setRowHeight(26);
            table.getTableHeader().setReorderingAllowed(false);

            JScrollPane sp = new JScrollPane(table);
            sp.setBorder(null);

            card.add(sp, BorderLayout.CENTER);

            lblCount.setBorder(new EmptyBorder(6, 8, 6, 8));
            card.add(lblCount, BorderLayout.SOUTH);

            JPanel wrap = new JPanel(new BorderLayout());
            wrap.setBackground(MAIN_BG);
            wrap.setBorder(new EmptyBorder(8, 8, 8, 8));
            wrap.add(card, BorderLayout.CENTER);

            page.add(wrap);

            add(page, BorderLayout.NORTH);
        }

        void loadApplicationsForStudent(String id) {
            this.studentId = id;
            refresh();
        }

        private void refresh() {
            model.setRowCount(0);
            if (studentId == null) return;

            List<ApplicationView> rows = MockApplicationService.getApplicationsForStudent(studentId);
            for (ApplicationView a : rows) {
                model.addRow(new Object[]{
                        a.companyName,
                        a.offerTitle,
                        a.appliedDate.toString(),
                        pretty(a.status)
                });
            }
            lblCount.setText(rows.size() + " application(s) total.");
        }

        private String pretty(ApplicationStatus s) {
            String t = s.name().toLowerCase();
            return Character.toUpperCase(t.charAt(0)) + t.substring(1);
        }

        private JPanel sectionBar(String text) {
            JPanel bar = new JPanel(new BorderLayout());
            bar.setBackground(ACCENT);
            bar.setBorder(new EmptyBorder(4, 8, 4, 8));
            bar.add(headerLabel(text), BorderLayout.WEST);
            bar.setAlignmentX(Component.LEFT_ALIGNMENT);
            return bar;
        }
    }

    // ==================== POLICY (screenshot style) ====================
    static class StudentPolicyPanel extends JPanel {
        StudentPolicyPanel() {
            setLayout(new BorderLayout());
            setBackground(MAIN_BG);

            JPanel page = new JPanel();
            page.setBackground(MAIN_BG);
            page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));

            page.add(sectionBar("Placement Policy"));

            page.add(wrap(block("General Rules",
                    "Students must register with their academic details to be eligible for placements.\n" +
                            "A student can hold only ONE approved offer at any time.\n" +
                            "Students must meet eligibility criteria (CGPA, course, branch) set by the recruiting company.\n" +
                            "Once an application is submitted, it cannot be withdrawn.\n" +
                            "Students must keep their profile information up to date."
            )));

            page.add(wrap(block("Application Statuses",
                    "Applied - Your application has been submitted and is awaiting review.\n" +
                            "Shortlisted - You have been shortlisted by the company for further rounds.\n" +
                            "Approved - Your application has been accepted. You are now placed.\n" +
                            "Rejected - Your application was not selected for this offer."
            )));

            page.add(wrap(block("Code of Conduct",
                    "Students must attend all scheduled placement activities and interviews.\n" +
                            "Providing false information is grounds for disqualification.\n" +
                            "Once placed, students should not apply to any more companies through the placement system.\n" +
                            "Any grievances should be directed to the Placement Department admin."
            )));

            page.add(wrap(block("Contact",
                    "For queries, contact the Placement Office:\n" +
                            "Email: admin@placement.edu\n" +
                            "Office: Room 101, Admin Block"
            )));

            JScrollPane sc = new JScrollPane(page);
            sc.setBorder(null);
            sc.getViewport().setBackground(MAIN_BG);
            add(sc, BorderLayout.CENTER);
        }

        private JPanel sectionBar(String text) {
            JPanel bar = new JPanel(new BorderLayout());
            bar.setBackground(ACCENT);
            bar.setBorder(new EmptyBorder(4, 8, 4, 8));
            bar.add(headerLabel(text), BorderLayout.WEST);
            bar.setAlignmentX(Component.LEFT_ALIGNMENT);
            return bar;
        }

        private JPanel block(String title, String text) {
            JPanel p = titledBlock(title);
            p.setLayout(new BorderLayout());
            JTextArea ta = new JTextArea(text);
            ta.setEditable(false);
            ta.setLineWrap(true);
            ta.setWrapStyleWord(true);
            ta.setBackground(CARD_BG);
            ta.setBorder(new EmptyBorder(8, 10, 8, 10));
            p.add(ta, BorderLayout.CENTER);
            return p;
        }

        private JPanel wrap(JComponent c) {
            JPanel w = new JPanel(new BorderLayout());
            w.setBackground(MAIN_BG);
            w.setBorder(new EmptyBorder(8, 8, 8, 8));
            w.add(c, BorderLayout.CENTER);
            w.setAlignmentX(Component.LEFT_ALIGNMENT);
            return w;
        }
    }

    // ==================== COMPANY SHELL (Placeholder) ====================
    static class CompanyShellPanel extends JPanel {
        private final MainFrame frame;
        private final JLabel lbl = new JLabel("Company Dashboard (placeholder)");

        CompanyShellPanel(MainFrame frame) {
            this.frame = frame;
            setLayout(new BorderLayout());
            setBackground(MAIN_BG);

            lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
            lbl.setBorder(new EmptyBorder(24, 24, 24, 24));

            JButton logout = greyButton("Logout");
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

            JLabel msg = new JLabel("<html>This is a placeholder screen.<br/>You only implemented Login + Registration + Student UI.</html>");
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
            setBackground(MAIN_BG);

            lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
            lbl.setBorder(new EmptyBorder(24, 24, 24, 24));

            JButton logout = greyButton("Logout");
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

    // ==================== THEME HELPERS ====================
    static JButton navButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBackground(MAIN_BG);
        b.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        return b;
    }

    static JButton greyButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBackground(BTN);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        return b;
    }

    static JPanel titledBlock(String title) {
        JPanel p = new JPanel();
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER),
                title
        ));
        return p;
    }

    static JLabel headerLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        return l;
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

        ApplicationRecord(String studentId, String companyName, String offerTitle,
                          LocalDate appliedDate, ApplicationStatus status) {
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
                    "BSc (Hons) Computer Science", "Computer Science", "2", 3.45));

            // extra student to demo
            students.add(new Student("S002", "Alice Johnson", "alice@university.edu", "1234",
                    "B.Tech", "Computer Science", "4", 8.5));

            companies.add(new Company("C001", "TechCorp Solutions", "hr@techcorp.com", "1234"));
            companies.add(new Company("C002", "DataWorks Analytics", "hr@dataworks.com", "1234"));

            admins.add(new Admin("admin", "1234"));

            applications.add(new ApplicationRecord("S002", "TechCorp Solutions", "Software Engineer",
                    LocalDate.of(2026, 2, 5), ApplicationStatus.SHORTLISTED));
            applications.add(new ApplicationRecord("S002", "DataWorks Analytics", "Data Analyst",
                    LocalDate.of(2026, 2, 12), ApplicationStatus.APPLIED));

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