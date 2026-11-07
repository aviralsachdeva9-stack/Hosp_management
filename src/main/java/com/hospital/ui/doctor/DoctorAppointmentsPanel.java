package com.hospital.ui.doctor;

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

// Doctor ki appointment table aur completion action dikhata hai.
public class DoctorAppointmentsPanel extends JPanel {
    private final AppointmentDAO dao = new AppointmentDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Date", "Time", "Patient", "Reason", "Status"}, 0) {
        // Appointment table ko read-only rakhta hai.
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private List<Appointment> appointments = new ArrayList<>();

    // Table, refresh aur completion controls banata hai.
    public DoctorAppointmentsPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(36, 40, 32, 40));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(Theme.pageTitle("Appointments"), BorderLayout.WEST);
        JButton refresh = Theme.secondaryButton("Refresh");
        refresh.addActionListener(e -> onRefreshClicked());
        top.add(refresh, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);
        Theme.styleTable(table);
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusPillRenderer());
        add(new JScrollPane(table), BorderLayout.CENTER);
        JButton complete = Theme.primaryButton("Mark Completed");
        complete.addActionListener(e -> onCompleteClicked());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.add(complete);
        add(actions, BorderLayout.SOUTH);
        loadAppointments();
    }

    // Appointment list ko database se dobara load karta hai.
    private void loadAppointments() {
        model.setRowCount(0);
        try {
            appointments = dao.getForDoctor(Session.currentDoctor.getId());
            for (Appointment a : appointments) model.addRow(new Object[]{a.getApptDate(), a.getApptTime(),
                    a.getPatientName(), a.getNotes(), a.getStatus()});
        } catch (SQLException | NullPointerException ex) { showError("Appointments load nahi hui: " + ex.getMessage()); }
    }

    // Refresh control par list reload karta hai.
    private void onRefreshClicked() { loadAppointments(); }

    // Chuni scheduled appointment complete mark karta hai.
    private void onCompleteClicked() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Pehle appointment select karein."); return; }
        Appointment a = appointments.get(table.convertRowIndexToModel(row));
        if (!"Scheduled".equals(a.getStatus())) { showError("Sirf scheduled appointment complete ho sakti hai."); return; }
        try {
            if (dao.markCompleted(a.getAppointmentId(), Session.currentDoctor.getId())) loadAppointments();
            else showError("Appointment update nahi hui. Refresh karke dobara try karein.");
        } catch (SQLException ex) { showError("Update nahi hua: " + ex.getMessage()); }
    }

    // Error ko warning popup mein dikhata hai.
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Appointments", JOptionPane.WARNING_MESSAGE);
    }
}