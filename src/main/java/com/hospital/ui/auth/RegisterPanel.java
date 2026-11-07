package com.hospital.ui.auth;

// RegisterPanel.java - Registration screen.
// Same role toggle jaise Login mein.
// Common fields: name, email, password, confirm, contact.
// Patient extra: DOB, gender, blood group, address.
// Doctor extra: specialization, experience.
// Fields toggle hote hain role ke hisaab se.

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.hospital.dao.UserDAO;
import com.hospital.ui.MainFrame;
import com.hospital.ui.Theme;
import com.hospital.util.Validator;

import net.miginfocom.swing.MigLayout;

public class RegisterPanel extends JPanel {

    private final MainFrame frame;
    private final UserDAO   userDAO = new UserDAO();

    // Role toggle
    private JToggleButton patientToggle;
    private JToggleButton doctorToggle;

    // Common fields
    private JTextField     nameField;
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JTextField     contactField;

    // Patient-only fields (panel show/hide hota hai)
    private JPanel     patientExtraPanel;
    private DatePicker dobPicker;
    private JComboBox<String> genderCombo;
    private JComboBox<String> bloodGroupCombo;
    private JTextField addressField;

    // Doctor-only fields
    private JPanel     doctorExtraPanel;
    private JTextField specField;
    private JTextField expField;

    public RegisterPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new GridLayout(1, 2));
        add(buildLeftPanel());
        add(buildScrollableRight());
    }

    // Left half: same branding as login
    private JPanel buildLeftPanel() {
        JPanel left = new JPanel(new MigLayout("fill, insets 60", "[grow]", "[grow][][]10[]"));
        left.setBackground(Theme.ACCENT);

        JLabel headline = new JLabel("<html>Join us<br>today.</html>");
        headline.setFont(new Font("Dialog", Font.BOLD, 42));
        headline.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Create your hospital account.");
        sub.setFont(Theme.fontBody());
        sub.setForeground(new Color(0xC8E6E0));

        left.add(new JLabel(), "grow, wrap");
        left.add(headline, "wrap 16");
        left.add(sub, "wrap");
        left.add(new JLabel(), "grow");

        return left;
    }

    // Right half: form inside a scroll pane (fields bahut hain)
    private JScrollPane buildScrollableRight() {
        JPanel right = buildRightPanel();
        JScrollPane scroll = new JScrollPane(right);
        scroll.setBorder(null);
        scroll.setBackground(Theme.BG);
        scroll.getViewport().setBackground(Theme.BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // Right half: registration form
    private JPanel buildRightPanel() {
        JPanel right = new JPanel(new MigLayout(
                "fillx, insets 50 50 50 50",
                "[grow]",
                "[][]20[][20px][][][][][][20px][][20px][][20px][]"
        ));
        right.setBackground(Theme.BG);

        // Title
        right.add(Theme.pageTitle("Create Account"), "wrap 4");
        right.add(Theme.mutedLabel("Fill in your details below"), "wrap");

        // Role toggle
        patientToggle = new JToggleButton("Patient");
        doctorToggle  = new JToggleButton("Doctor");
        styleToggle(patientToggle);
        styleToggle(doctorToggle);
        patientToggle.setSelected(true);

        ButtonGroup grp = new ButtonGroup();
        grp.add(patientToggle);
        grp.add(doctorToggle);

        JPanel togglePanel = new JPanel(new GridLayout(1, 2, 4, 0));
        togglePanel.setBackground(Theme.BG);
        togglePanel.add(patientToggle);
        togglePanel.add(doctorToggle);
        right.add(togglePanel, "growx, h 44!, wrap");

        // ── Common fields ──────────────────────────────
        right.add(Theme.mutedLabel("Full Name"), "wrap 4");
        nameField = Theme.styledField();
        right.add(nameField, "growx, h 42!, wrap");

        right.add(Theme.mutedLabel("Email"), "wrap 4");
        emailField = Theme.styledField();
        right.add(emailField, "growx, h 42!, wrap");

        right.add(Theme.mutedLabel("Password"), "wrap 4");
        passwordField = Theme.styledPasswordField();
        right.add(passwordField, "growx, h 42!, wrap");

        right.add(Theme.mutedLabel("Confirm Password"), "wrap 4");
        confirmField = Theme.styledPasswordField();
        right.add(confirmField, "growx, h 42!, wrap");

        right.add(Theme.mutedLabel("Contact Number"), "wrap 4");
        contactField = Theme.styledField();
        right.add(contactField, "growx, h 42!, wrap");

        // ── Patient extra fields ───────────────────────
        patientExtraPanel = buildPatientExtra();
        right.add(patientExtraPanel, "growx, hidemode 3, wrap");

        // ── Doctor extra fields ────────────────────────
        doctorExtraPanel = buildDoctorExtra();
        doctorExtraPanel.setVisible(false); // Doctor fields pehle chhupe hain
        right.add(doctorExtraPanel, "growx, hidemode 3, wrap");

        // Toggle listeners - role badlone pe fields show/hide karo
        patientToggle.addActionListener(e -> onRoleToggled(true));
        doctorToggle.addActionListener(e -> onRoleToggled(false));

        // Register button
        JButton registerBtn = Theme.primaryButton("Create Account");
        registerBtn.setPreferredSize(new Dimension(0, 44));
        registerBtn.addActionListener(e -> onRegisterClicked());
        right.add(registerBtn, "growx, wrap");

        // Back to login link
        JButton backLink = Theme.linkButton("Already have an account? Login");
        backLink.addActionListener(e -> frame.show(MainFrame.LOGIN));
        right.add(backLink, "center");

        return right;
    }

    // Patient ke extra fields ka panel
    private JPanel buildPatientExtra() {
        JPanel p = new JPanel(new MigLayout("fillx, insets 0", "[grow]", "[]4[]10[]4[]10[]4[]10[]4[]"));
        p.setBackground(Theme.BG);

        p.add(Theme.mutedLabel("Date of Birth"), "wrap 4");
        dobPicker = buildDatePicker();
        p.add(dobPicker, "growx, wrap");

        p.add(Theme.mutedLabel("Gender"), "wrap 4");
        genderCombo = Theme.styledCombo(new String[]{"Male", "Female", "Other"});
        p.add(genderCombo, "growx, wrap");

        p.add(Theme.mutedLabel("Blood Group"), "wrap 4");
        bloodGroupCombo = Theme.styledCombo(new String[]{"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"});
        p.add(bloodGroupCombo, "growx, wrap");

        p.add(Theme.mutedLabel("Address"), "wrap 4");
        addressField = Theme.styledField();
        p.add(addressField, "growx, h 42!");

        return p;
    }

    // Doctor ke extra fields ka panel
    private JPanel buildDoctorExtra() {
        JPanel p = new JPanel(new MigLayout("fillx, insets 0", "[grow]", "[]4[]10[]4[]"));
        p.setBackground(Theme.BG);

        p.add(Theme.mutedLabel("Specialization"), "wrap 4");
        specField = Theme.styledField();
        p.add(specField, "growx, h 42!, wrap");

        p.add(Theme.mutedLabel("Experience (years)"), "wrap 4");
        expField = Theme.styledField();
        p.add(expField, "growx, h 42!");

        return p;
    }

    // LGoodDatePicker banao - past dates disabled.
    // ZARURI: pehle DatePicker banao, PHIR limit lagao.
    // Library ka rule: setDateRangeLimits() sirf picker banane ke BAAD kaam karta hai.
    private DatePicker buildDatePicker() {
        DatePickerSettings settings = new DatePickerSettings();
        DatePicker picker = new DatePicker(settings);        // 1. pehle picker banao
        settings.setDateRangeLimits(null, LocalDate.now()); // 2. phir limit lagao
        return picker;
    }

    // Role toggle hone pe patient/doctor panels show/hide karo
    private void onRoleToggled(boolean isPatient) {
        patientExtraPanel.setVisible(isPatient);
        doctorExtraPanel.setVisible(!isPatient);
        revalidate();
        repaint();
    }

    private void styleToggle(JToggleButton btn) {
        btn.setFont(Theme.fontBody());
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBackground(Theme.CARD);
        btn.setForeground(Theme.TEXT);
        btn.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
    }

    // ── Register button click handler ──────────────────────────
    private void onRegisterClicked() {
        // Common fields padhna
        String name     = nameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm  = new String(confirmField.getPassword());
        String contact  = contactField.getText().trim();

        // Validation - Validator.java use karo
        if (!Validator.isNotEmpty(name)) {
            showError("Poora naam bharo."); return;
        }
        if (!Validator.isValidEmail(email)) {
            showError("Valid email address likho."); return;
        }
        if (password.length() < 6) {
            showError("Password kam se kam 6 characters ka hona chahiye."); return;
        }
        if (!password.equals(confirm)) {
            showError("Dono passwords same nahi hain."); return;
        }
        if (!Validator.isValidPhone(contact)) {
            showError("Valid 10-digit phone number likho."); return;
        }

        try {
            if (patientToggle.isSelected()) {
                registerPatient(name, email, password, contact);
            } else {
                registerDoctor(name, email, password, contact);
            }
        } catch (SQLException ex) {
            showError(ex.getMessage());
        }
    }

    // Patient registration - UserDAO ko call karo
    private void registerPatient(String name, String email,
                                  String password, String contact) throws SQLException {
        // Date picker se date string nikalna
        LocalDate dob = dobPicker.getDate();
        String dobStr = (dob != null) ? dob.toString() : "";

        String gender     = (String) genderCombo.getSelectedItem();
        String bloodGroup = (String) bloodGroupCombo.getSelectedItem();
        String address    = addressField.getText().trim();

        userDAO.registerPatient(name, email, password, contact, dobStr, gender, bloodGroup, address);

        JOptionPane.showMessageDialog(this,
                "Account ban gaya! Ab login karo.", "Success", JOptionPane.INFORMATION_MESSAGE);
        frame.show(MainFrame.LOGIN);
    }

    // Doctor registration - UserDAO ko call karo
    private void registerDoctor(String name, String email,
                                 String password, String contact) throws SQLException {
        String spec = specField.getText().trim();
        if (!Validator.isNotEmpty(spec)) {
            showError("Specialization bharo."); return;
        }

        int exp = 0;
        try {
            exp = Integer.parseInt(expField.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Experience mein sirf number likho."); return;
        }

        userDAO.registerDoctor(name, email, password, contact, spec, exp);

        JOptionPane.showMessageDialog(this,
                "Doctor account ban gaya! Ab login karo.", "Success", JOptionPane.INFORMATION_MESSAGE);
        frame.show(MainFrame.LOGIN);
    }

    // Error popup shortcut
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }
}

