package com.hospital.ui.patient;

// BookAppointmentPanel.java - Appointment book karne ki screen.
// 4 steps ek screen pe:
//   1. Specialization select karo
//   2. Doctor select karo (cards)
//   3. Date aur time slot select karo
//   4. Reason likho aur Confirm karo
// Sab logic methods mein split kiya hai for readability.

import java.awt.BorderLayout;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.hospital.dao.AppointmentDAO;
import com.hospital.model.Doctor;
import com.hospital.ui.Theme;
import com.hospital.util.Session;

import net.miginfocom.swing.MigLayout;

public class BookAppointmentPanel extends JPanel {

    private final DoctorPickerPanel doctorPicker = new DoctorPickerPanel();
    private final AppointmentTimeSlotPanel timeSlots = new AppointmentTimeSlotPanel();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final JTextArea reason = new JTextArea(3, 0);

    // Booking form ko screen par set karta hai.
    public BookAppointmentPanel(PatientDashboard dashboard) {
        setBackground(Theme.BG);
        setLayout(new BorderLayout());
        JPanel form = new JPanel(new MigLayout("fillx, insets 40, gapy 12", "[grow]", "[]"));
        form.setBackground(Theme.BG);
        form.add(Theme.pageTitle("Book Appointment"), "wrap 4");
        form.add(Theme.mutedLabel("Select a doctor and choose a time that works for you."), "wrap 12");
        doctorPicker.setDoctorSelectionListener(timeSlots::setDoctor);
        form.add(doctorPicker, "growx, wrap 8");
        form.add(Theme.sectionLabel("Step 3 - Date & Time"), "wrap 8");
        form.add(timeSlots, "growx, wrap 8");
        form.add(Theme.sectionLabel("Step 4 - Reason"), "wrap 8");
        reason.setFont(Theme.fontBody());
        reason.setForeground(Theme.TEXT);
        reason.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        reason.setLineWrap(true);
        form.add(new JScrollPane(reason), "growx, h 90!, wrap");
        JButton confirm = Theme.primaryButton("Confirm Appointment");
        confirm.addActionListener(event -> onConfirmClicked());
        form.add(confirm, "growx, h 48!");
        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.setBackground(Theme.BG);
        scroll.getViewport().setBackground(Theme.BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    // Booking form ko validate karke DAO mein appointment save karta hai.
    private void onConfirmClicked() {
        Doctor doctor = doctorPicker.getSelectedDoctor();
        if (doctor == null) { showError("Doctor select karo pehle."); return; }
        if (timeSlots.getDate() == null) { showError("Date select karo."); return; }
        if (timeSlots.getSelectedTime() == null) { showError("Time slot select karo."); return; }
        int patientId = Session.currentPatient != null ? Session.currentPatient.getId() : -1;
        if (patientId == -1) { showError("Session expired. Dobara login karo."); return; }
        try {
            int id = appointmentDAO.book(patientId, doctor.getId(), timeSlots.getDate().toString(),
                    timeSlots.getSelectedTime(), reason.getText().trim());
            JOptionPane.showMessageDialog(this, "Appointment book ho gayi! ID: " + id,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            reason.setText("");
            doctorPicker.clearSelection();
            timeSlots.clearSelection();
        } catch (SQLException ex) { showError("Appointment book nahi hui: " + ex.getMessage()); }
    }

    // Validation ya database error dikhata hai.
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Appointment", JOptionPane.WARNING_MESSAGE);
    }
}

