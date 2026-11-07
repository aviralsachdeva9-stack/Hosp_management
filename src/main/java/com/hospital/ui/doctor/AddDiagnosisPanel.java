package com.hospital.ui.doctor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.hospital.dao.AppointmentDAO;
import com.hospital.dao.RecordDAO;
import com.hospital.model.Appointment;
import com.hospital.ui.Theme;
import com.hospital.util.Session;

import net.miginfocom.swing.MigLayout;

// Doctor ko appointment par diagnosis aur prescription save karne deta hai.
public class AddDiagnosisPanel extends JPanel {
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final RecordDAO recordDAO = new RecordDAO();
    private final JComboBox<Appointment> appointmentCombo = new JComboBox<>();
    private final JTextArea diagnosis = new JTextArea(5, 40);
    private final JTextArea prescription = new JTextArea(5, 40);

    // Appointment selector aur record form banata hai.
    public AddDiagnosisPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        JPanel form = new JPanel(new MigLayout("fillx, insets 36 40 32 40, gapy 12", "[grow]", "[]"));
        form.setBackground(Theme.BG);
        form.add(Theme.pageTitle("Add Diagnosis"), "wrap 8");
        form.add(Theme.mutedLabel("Scheduled or completed appointment"), "wrap 4");
        appointmentCombo.setFont(Theme.fontBody());
        appointmentCombo.setRenderer(new AppointmentRenderer());
        form.add(appointmentCombo, "growx, h 42!, wrap 12");
        styleArea(diagnosis);
        styleArea(prescription);
        form.add(Theme.mutedLabel("Diagnosis Log"), "wrap 4");
        form.add(new JScrollPane(diagnosis), "growx, h 140!, wrap 10");
        form.add(Theme.mutedLabel("Prescription"), "wrap 4");
        form.add(new JScrollPane(prescription), "growx, h 140!, wrap 12");
        JButton save = Theme.primaryButton("Save Medical Record");
        save.addActionListener(e -> onSaveClicked());
        form.add(save, "right, h 44!");
        add(new JScrollPane(form), BorderLayout.CENTER);
        loadAppointments();
    }

    // Text area ko readable aur wrapped banata hai.
    private void styleArea(JTextArea area) {
        area.setFont(Theme.fontBody());
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
    }

    // Doctor ki appointments dropdown mein load karta hai.
    private void loadAppointments() {
        try {
            List<Appointment> all = appointmentDAO.getForDoctor(Session.currentDoctor.getId());
            for (Appointment a : all) if ("Scheduled".equals(a.getStatus()) || "Completed".equals(a.getStatus()))
                appointmentCombo.addItem(a);
        } catch (SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Appointments load nahi hui: " + ex.getMessage());
        }
    }

    // Diagnosis validate karke record save karta hai.
    private void onSaveClicked() {
        Appointment a = (Appointment) appointmentCombo.getSelectedItem();
        String diagnosisText = diagnosis.getText().trim();
        if (a == null) { showError("Appointment select karein."); return; }
        if (diagnosisText.isEmpty()) { showError("Diagnosis likhna zaroori hai."); return; }
        try {
            recordDAO.add(a.getPatientId(), Session.currentDoctor.getId(), a.getAppointmentId(),
                    diagnosisText, prescription.getText().trim(), LocalDate.now().toString());
            if ("Scheduled".equals(a.getStatus())) {
                appointmentDAO.markCompleted(a.getAppointmentId(), Session.currentDoctor.getId());
            }
            diagnosis.setText("");
            prescription.setText("");
            JOptionPane.showMessageDialog(this, "Medical record save ho gaya.", "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) { showError("Record save nahi hua: " + ex.getMessage()); }
    }

    // Error ko warning popup mein dikhata hai.
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Diagnosis", JOptionPane.WARNING_MESSAGE);
    }

    // Appointment selector mein readable details dikhata hai.
    private static class AppointmentRenderer extends DefaultListCellRenderer {
        // Appointment ko date, patient aur status ke saath render karta hai.
        @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean selected, boolean focus) {
            super.getListCellRendererComponent(list, value, index, selected, focus);
            if (value instanceof Appointment a) setText(a.getApptDate() + " " + a.getApptTime()
                    + " | " + a.getPatientName() + " | " + a.getStatus());
            return this;
        }
    }
}