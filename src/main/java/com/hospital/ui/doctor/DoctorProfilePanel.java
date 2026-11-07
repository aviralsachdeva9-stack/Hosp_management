package com.hospital.ui.doctor;

import java.awt.BorderLayout;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.hospital.dao.DoctorDAO;
import com.hospital.model.Doctor;
import com.hospital.ui.Theme;
import com.hospital.util.Session;
import com.hospital.util.Validator;

import net.miginfocom.swing.MigLayout;

// Doctor ki professional details dekhne aur update karne deta hai.
public class DoctorProfilePanel extends JPanel {
    private final DoctorDAO dao = new DoctorDAO();
    private final JTextField name = Theme.styledField();
    private final JTextField contact = Theme.styledField();
    private final JTextField specialization = Theme.styledField();
    private final JTextField experience = Theme.styledField();

    // Profile inputs aur save action arrange karta hai.
    public DoctorProfilePanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        JPanel form = new JPanel(new MigLayout("fillx, insets 36 40 32 40, gapy 12", "[150!][grow]", "[]"));
        form.setBackground(Theme.BG);
        form.add(Theme.pageTitle("My Profile"), "span 2, wrap 16");
        addField(form, "Full Name", name);
        addField(form, "Contact Number", contact);
        addField(form, "Specialization", specialization);
        addField(form, "Experience (years)", experience);
        JButton save = Theme.primaryButton("Save Changes");
        save.addActionListener(e -> onSaveClicked());
        form.add(save, "span 2, right, h 44!, gaptop 12");
        add(form, BorderLayout.NORTH);
        loadProfile();
    }

    // Label aur field ko form row mein rakhta hai.
    private void addField(JPanel form, String label, JTextField field) {
        form.add(Theme.mutedLabel(label));
        form.add(field, "growx, h 42!, wrap");
    }

    // Session se current doctor ki details bharta hai.
    private void loadProfile() {
        Doctor doctor = Session.currentDoctor;
        if (doctor == null) return;
        name.setText(doctor.getFullName());
        contact.setText(doctor.getContactNumber());
        specialization.setText(doctor.getSpecialization());
        experience.setText(String.valueOf(doctor.getExperienceYears()));
    }

    // Profile validate karke database mein update karta hai.
    private void onSaveClicked() {
        String fullName = name.getText().trim();
        String phone = contact.getText().trim();
        String spec = specialization.getText().trim();
        int years;
        try { years = Integer.parseInt(experience.getText().trim()); }
        catch (NumberFormatException ex) { showError("Experience mein valid number likhein."); return; }
        if (!Validator.isNotEmpty(fullName) || !Validator.isValidPhone(phone)
                || !Validator.isNotEmpty(spec) || years < 0) {
            showError("Naam, valid contact, specialization aur non-negative experience dein."); return;
        }
        try {
            Doctor doctor = Session.currentDoctor;
            if (dao.updateProfile(doctor.getId(), fullName, phone, spec, years)) {
                doctor.setFullName(fullName);
                doctor.setContactNumber(phone);
                doctor.setSpecialization(spec);
                doctor.setExperienceYears(years);
                JOptionPane.showMessageDialog(this, "Profile update ho gayi.", "Saved", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) { showError("Profile save nahi hui: " + ex.getMessage()); }
    }

    // Validation ya database error popup mein dikhata hai.
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Profile", JOptionPane.WARNING_MESSAGE);
    }
}