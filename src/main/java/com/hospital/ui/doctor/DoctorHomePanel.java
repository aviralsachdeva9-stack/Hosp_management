package com.hospital.ui.doctor;

import java.awt.BorderLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.hospital.dao.AppointmentDAO;
import com.hospital.model.Appointment;
import com.hospital.ui.Theme;
import com.hospital.util.Session;

import net.miginfocom.swing.MigLayout;

// Doctor ke aaj ke appointments aur unka count dikhata hai.
public class DoctorHomePanel extends JPanel {
    private final AppointmentDAO dao = new AppointmentDAO();
    private final JLabel count = new JLabel("0");
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Time", "Patient", "Reason", "Status"}, 0) {
        // Appointment table ko read-only rakhta hai.
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    // Greeting, count aur aaj ki list arrange karta hai.
    public DoctorHomePanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        JPanel content = new JPanel(new MigLayout("fillx, insets 36 40 32 40, gapy 18", "[grow]", "[]"));
        content.setBackground(Theme.BG);
        String doctorName = Session.currentDoctor == null ? "Doctor" : Session.currentDoctor.getFullName();
        content.add(Theme.pageTitle("Welcome, Dr. " + doctorName), "wrap");
        JPanel stat = Theme.card();
        stat.setLayout(new MigLayout("insets 16, fillx", "[grow]", "[]4[]"));
        count.setFont(new Font("Dialog", Font.BOLD, 30));
        count.setForeground(Theme.ACCENT);
        stat.add(count, "wrap");
        stat.add(Theme.mutedLabel("Appointments today"));
        content.add(stat, "w 240!, wrap 6");
        content.add(Theme.sectionLabel("Today's Appointments"), "wrap 8");
        JTable table = new JTable(model);
        Theme.styleTable(table);
        content.add(new JScrollPane(table), "grow, push");
        add(content, BorderLayout.CENTER);
        loadToday();
    }

    // Doctor ke aaj ke appointments load karta hai.
    private void loadToday() {
        try {
            int doctorId = Session.currentDoctor.getId();
            List<Appointment> appointments = dao.getForDoctorToday(doctorId);
            count.setText(String.valueOf(appointments.size()));
            for (Appointment appointment : appointments) {
                model.addRow(new Object[]{appointment.getApptTime(), appointment.getPatientName(),
                        appointment.getNotes(), appointment.getStatus()});
            }
        } catch (SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Appointments load nahi hui: " + ex.getMessage());
        }
    }
}