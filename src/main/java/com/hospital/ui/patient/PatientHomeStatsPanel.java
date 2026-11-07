package com.hospital.ui.patient;

// Patient ke upcoming appointments aur completed visits count karta hai.

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hospital.dao.AppointmentDAO;
import com.hospital.model.Appointment;
import com.hospital.ui.Theme;
import com.hospital.util.Session;

import net.miginfocom.swing.MigLayout;

public class PatientHomeStatsPanel extends JPanel {
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    // Dono counters ko side-by-side dikhata hai.
    public PatientHomeStatsPanel() {
        super(new GridLayout(1, 2, 16, 0));
        setBackground(Theme.BG);
        int[] counts = loadCounts();
        add(buildStatCard("Upcoming Appointments", counts[0], Theme.ACCENT));
        add(buildStatCard("Total Visits", counts[1], new Color(0x1565C0)));
    }

    // Patient ki scheduled aur completed appointments ginta hai.
    private int[] loadCounts() {
        int upcoming = 0, completed = 0;
        try {
            int id = Session.currentPatient == null ? -1 : Session.currentPatient.getId();
            List<Appointment> appointments = appointmentDAO.getForPatient(id);
            for (Appointment appointment : appointments) {
                if ("Scheduled".equals(appointment.getStatus())) upcoming++;
                if ("Completed".equals(appointment.getStatus())) completed++;
            }
        } catch (SQLException ignored) { }
        return new int[]{upcoming, completed};
    }

    // Ek counter ko stat card mein dikhata hai.
    private JPanel buildStatCard(String label, int value, Color color) {
        JPanel card = Theme.card();
        card.setLayout(new MigLayout("insets 24, fillx", "[grow]", "[]8[]"));
        JLabel number = new JLabel(String.valueOf(value));
        number.setFont(new Font("Dialog", Font.BOLD, 36));
        number.setForeground(color);
        JLabel text = new JLabel(label);
        text.setFont(Theme.fontBody());
        text.setForeground(Theme.MUTED);
        card.add(number, "wrap");
        card.add(text);
        return card;
    }
}