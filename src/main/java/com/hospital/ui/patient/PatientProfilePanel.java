package com.hospital.ui.patient;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.hospital.dao.PatientDAO;
import com.hospital.model.Patient;
import com.hospital.ui.Theme;
import com.hospital.util.Session;
import com.hospital.util.Validator;

import net.miginfocom.swing.MigLayout;

// Patient ki personal details dekhne aur update karne deta hai.
public class PatientProfilePanel extends JPanel {
    private final PatientDAO dao = new PatientDAO();
    private final JTextField name = Theme.styledField();
    private final JTextField contact = Theme.styledField();
    private final JTextField dob = Theme.styledField();
    private final JComboBox<String> gender = Theme.styledCombo(new String[]{"Male", "Female", "Other"});
    private final JComboBox<String> blood = Theme.styledCombo(
            new String[]{"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"});
    private final JTextField address = Theme.styledField();

    // Profile inputs aur save action ko arrange karta hai.
    public PatientProfilePanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        JPanel form = new JPanel(new MigLayout("fillx, insets 36 40 32 40, gapy 12", "[150!][grow]", "[]"));
        form.setBackground(Theme.BG);
        form.add(Theme.pageTitle("My Profile"), "span 2, wrap 16");
        addField(form, "Full Name", name);
        addField(form, "Contact Number", contact);
        addField(form, "Date of Birth (YYYY-MM-DD)", dob);
        addField(form, "Gender", gender);
        addField(form, "Blood Group", blood);
        addField(form, "Address", address);
        JButton save = Theme.primaryButton("Save Changes");
        save.addActionListener(e -> onSaveClicked());
        form.add(save, "span 2, right, h 44!, gaptop 12");
        add(form, BorderLayout.NORTH);
        loadProfile();
    }

    // Label aur input ko form ki ek row mein rakhta hai.
    private void addField(JPanel form, String label, JComponent field) {
        form.add(Theme.mutedLabel(label));
        form.add(field, "growx, h 42!, wrap");
    }

    // Session se current patient ki details fields mein bharta hai.
    private void loadProfile() {
        Patient patient = Session.currentPatient;
        if (patient == null) return;
        name.setText(patient.getFullName());
        contact.setText(patient.getContactNumber());
        dob.setText(patient.getDob() == null ? "" : patient.getDob());
        gender.setSelectedItem(patient.getGender());
        blood.setSelectedItem(patient.getBloodGroup());
        address.setText(patient.getAddress());
    }

    // Input validate karke patient profile database mein save karta hai.
    private void onSaveClicked() {
        String fullName = name.getText().trim();
        String phone = contact.getText().trim();
        String birthDate = dob.getText().trim();
        if (!Validator.isNotEmpty(fullName) || !Validator.isValidPhone(phone)) {
            showError("Naam bharein aur valid contact number dein."); return;
        }
        if (!birthDate.isEmpty()) {
            try {
                if (LocalDate.parse(birthDate).isAfter(LocalDate.now())) {
                    showError("Date of birth future mein nahi ho sakti."); return;
                }
            } catch (Exception ex) { showError("DOB YYYY-MM-DD format mein likhein."); return; }
        }
        try {
            Patient patient = Session.currentPatient;
            boolean saved = dao.updateProfile(patient.getId(), fullName, phone,
                    birthDate, (String) gender.getSelectedItem(), (String) blood.getSelectedItem(),
                    address.getText().trim());
            if (saved) {
                patient.setFullName(fullName);
                patient.setContactNumber(phone);
                patient.setDob(birthDate);
                patient.setGender((String) gender.getSelectedItem());
                patient.setBloodGroup((String) blood.getSelectedItem());
                patient.setAddress(address.getText().trim());
                JOptionPane.showMessageDialog(this, "Profile update ho gayi.", "Saved", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) { showError("Profile save nahi hui: " + ex.getMessage()); }
    }

    // Validation ya database error ko popup mein dikhata hai.
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Profile", JOptionPane.WARNING_MESSAGE);
    }
}