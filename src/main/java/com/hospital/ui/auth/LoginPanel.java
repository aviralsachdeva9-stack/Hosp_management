package com.hospital.ui.auth;

// LoginPanel.java - Login screen.
// Do halves: left = green branding, right = form.
// Role toggle (Patient / Doctor) + email + password + login button.
// "New here? Register" link Register screen pe le jaata hai.

import com.hospital.dao.DoctorDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.dao.UserDAO;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.ui.MainFrame;
import com.hospital.ui.Theme;
import com.hospital.util.Session;
import com.hospital.ui.patient.PatientDashboard;
import com.hospital.ui.doctor.DoctorDashboard;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginPanel extends JPanel {

    private final MainFrame frame;

    // Form fields
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JToggleButton  patientToggle;
    private JToggleButton  doctorToggle;
    private ButtonGroup    roleGroup;

    // DAO objects - sirf ek baar banao
    private final UserDAO    userDAO    = new UserDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO  doctorDAO  = new DoctorDAO();

    public LoginPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new GridLayout(1, 2)); // Do equal halves side by side
        add(buildLeftPanel());           // Green branding half
        add(buildRightPanel());          // Form half
    }

    // ── Left half: Green background + branding text ────────────
    private JPanel buildLeftPanel() {
        JPanel left = new JPanel(new MigLayout("fill, insets 60", "[grow]", "[grow][][]10[]"));
        left.setBackground(Theme.ACCENT);

        // Bada tagline
        JLabel headline = new JLabel("<html>Care,<br>organised.</html>");
        headline.setFont(new Font("Dialog", Font.BOLD, 42));
        headline.setForeground(Color.WHITE);

        // Chhota sub-text
        JLabel sub = new JLabel("Your health, managed simply.");
        sub.setFont(Theme.fontBody());
        sub.setForeground(new Color(0xC8E6E0));

        left.add(new JLabel(), "grow, wrap"); // Push content to center
        left.add(headline, "wrap 16");
        left.add(sub, "wrap");
        left.add(new JLabel(), "grow");       // Push content up slightly

        return left;
    }

    // ── Right half: Login form ─────────────────────────────────
    private JPanel buildRightPanel() {
        JPanel right = new JPanel(new MigLayout(
                "fill, insets 60 50 60 50",
                "[grow]",
                "[][][][20px][][20px][][][20px][][10px][]"
        ));
        right.setBackground(Theme.BG);

        // "Welcome back" heading
        JLabel title = Theme.pageTitle("Welcome back");
        JLabel sub   = Theme.mutedLabel("Sign in to continue");

        // Role toggle buttons
        patientToggle = new JToggleButton("Patient");
        doctorToggle  = new JToggleButton("Doctor");
        styleToggle(patientToggle);
        styleToggle(doctorToggle);
        patientToggle.setSelected(true); // Default: Patient

        roleGroup = new ButtonGroup();
        roleGroup.add(patientToggle);
        roleGroup.add(doctorToggle);

        JPanel togglePanel = new JPanel(new GridLayout(1, 2, 4, 0));
        togglePanel.setBackground(Theme.BG);
        togglePanel.add(patientToggle);
        togglePanel.add(doctorToggle);

        // Email field
        JLabel emailLbl = Theme.mutedLabel("Email");
        emailField = Theme.styledField();
        emailField.setPreferredSize(new Dimension(0, 42));

        // Password field + reveal button
        JLabel passLbl   = Theme.mutedLabel("Password");
        passwordField    = Theme.styledPasswordField();
        passwordField.setPreferredSize(new Dimension(0, 42));

        JPanel passRow   = buildPasswordRow();

        // Login button
        JButton loginBtn = Theme.primaryButton("Login");
        loginBtn.setPreferredSize(new Dimension(0, 44));
        loginBtn.addActionListener(e -> onLoginClicked());

        // Register link
        JButton registerLink = Theme.linkButton("New here? Register");
        registerLink.addActionListener(e -> frame.show(MainFrame.REGISTER));

        // Sab add karo
        right.add(title,        "growx, wrap 4");
        right.add(sub,          "wrap");
        right.add(togglePanel,  "growx, h 44!, wrap");
        right.add(emailLbl,     "wrap 4");
        right.add(emailField,   "growx, wrap");
        right.add(passLbl,      "wrap 4");
        right.add(passRow,      "growx, wrap");
        right.add(loginBtn,     "growx, wrap");
        right.add(new JLabel(), "wrap 4");
        right.add(registerLink, "center");

        return right;
    }

    // Password field aur reveal button ek row mein
    private JPanel buildPasswordRow() {
        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.setBackground(Theme.BG);

        passwordField.setPreferredSize(new Dimension(0, 42));
        JButton revealBtn = new JButton("Show");
        revealBtn.setFont(Theme.fontSmall());
        revealBtn.setForeground(Theme.MUTED);
        revealBtn.setBackground(Theme.CARD);
        revealBtn.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        revealBtn.setFocusPainted(false);
        revealBtn.addActionListener(e -> onRevealClicked(revealBtn));

        row.add(passwordField, BorderLayout.CENTER);
        row.add(revealBtn,     BorderLayout.EAST);
        return row;
    }

    // Password show/hide toggle
    private void onRevealClicked(JButton btn) {
        if (passwordField.getEchoChar() == 0) {
            passwordField.setEchoChar('•');
            btn.setText("Show");
        } else {
            passwordField.setEchoChar((char) 0);
            btn.setText("Hide");
        }
    }

    // Toggle button styling - selected state alag dikhega
    private void styleToggle(JToggleButton btn) {
        btn.setFont(Theme.fontBody());
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBackground(Theme.CARD);
        btn.setForeground(Theme.TEXT);
        btn.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        // FlatLaf selected state styling automatically handles the toggle appearance
    }

    // ── Login button click handler ─────────────────────────────
    // Yeh method actual login logic karta hai
    private void onLoginClicked() {
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role     = patientToggle.isSelected() ? "Patient" : "Doctor";

        // Basic validation
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Email aur password dono bharo.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // UserDAO se login check karo
            int userId = userDAO.login(email, password, role);

            if (userId == -1) {
                JOptionPane.showMessageDialog(this,
                        "Email ya password galat hai.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Session set karo
            Session.login(userId, role);

            // Role ke hisaab se profile load karo aur dashboard dikhao
            if ("Patient".equals(role)) {
                Patient p = patientDAO.getByUserId(userId);
                Session.currentPatient = p;
                PatientDashboard dash = new PatientDashboard(frame);
                frame.addCard(dash, MainFrame.PATIENT);
                frame.show(MainFrame.PATIENT);
            } else {
                Doctor d = doctorDAO.getByUserId(userId);
                Session.currentDoctor = d;
                DoctorDashboard dash = new DoctorDashboard(frame);
                frame.addCard(dash, MainFrame.DOCTOR);
                frame.show(MainFrame.DOCTOR);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
