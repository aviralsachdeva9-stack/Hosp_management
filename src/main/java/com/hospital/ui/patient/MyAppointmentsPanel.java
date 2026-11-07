package com.hospital.ui.patient;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.hospital.dao.AppointmentDAO;
import com.hospital.model.Appointment;
import com.hospital.ui.StatusPillRenderer;
import com.hospital.ui.Theme;
import com.hospital.util.Session;

// Patient ke appointments dikhata hai aur scheduled booking cancel karta hai.
public class MyAppointmentsPanel extends JPanel {
    private final AppointmentDAO dao = new AppointmentDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Date", "Time", "Doctor", "Reason", "Status"}, 0) {
        // Appointment table ko read-only rakhta hai.
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private List<Appointment> appointments = new ArrayList<>();

    // Appointment table, refresh aur cancel buttons banata hai.
    public MyAppointmentsPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(36, 40, 32, 40));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(Theme.pageTitle("My Appointments"), BorderLayout.WEST);
        JButton refresh = Theme.secondaryButton("Refresh");
        refresh.addActionListener(e -> onRefreshClicked());
        top.add(refresh, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);
        Theme.styleTable(table);
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusPillRenderer());
        add(new JScrollPane(table), BorderLayout.CENTER);
        JButton cancel = Theme.primaryButton("Cancel Appointment");
        cancel.addActionListener(e -> onCancelClicked());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.add(cancel);
        add(actions, BorderLayout.SOUTH);
        loadAppointments();
    }

    // Patient ki appointments database se dobara load karta hai.
    private void loadAppointments() {
        model.setRowCount(0);
        try {
            int patientId = Session.currentPatient.getId();
            appointments = dao.getForPatient(patientId);
            for (Appointment a : appointments) {
                model.addRow(new Object[]{a.getApptDate(), a.getApptTime(),
                        a.getDoctorName(), a.getNotes(), a.getStatus()});
            }
        } catch (SQLException | NullPointerException ex) {
            showError("Appointments load nahi hui: " + ex.getMessage());
        }
    }

    // Refresh button par appointment list reload karta hai.
    private void onRefreshClicked() { loadAppointments(); }

    // Chuni scheduled appointment confirm karke cancel karta hai.
    private void onCancelClicked() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Pehle appointment select karein."); return; }
        Appointment appointment = appointments.get(table.convertRowIndexToModel(row));
        if (!"Scheduled".equals(appointment.getStatus())) {
            showError("Sirf scheduled appointments cancel ho sakti hain."); return;
        }
        int choice = JOptionPane.showConfirmDialog(this, "Is appointment ko cancel karein?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            if (dao.cancel(appointment.getAppointmentId(), Session.currentPatient.getId())) {
                loadAppointments();
            } else {
                showError("Appointment cancel nahi hui. Refresh karke dobara try karein.");
            }
        } catch (SQLException ex) { showError("Cancel nahi hua: " + ex.getMessage()); }
    }

    // Error ko warning popup mein dikhata hai.
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Appointments", JOptionPane.WARNING_MESSAGE);
    }
}