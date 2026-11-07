package com.hospital.ui.patient;

// PatientHomePanel.java - Patient ka home/dashboard screen.
// Dikhata hai: greeting + 2 stat cards + next appointment card.
// DB se data padhta hai: AppointmentDAO.getForPatient().

import java.awt.BorderLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.hospital.dao.AppointmentDAO;
import com.hospital.model.Appointment;
import com.hospital.ui.Theme;
import com.hospital.util.Session;

import net.miginfocom.swing.MigLayout;

public class PatientHomePanel extends JPanel {

    private final PatientDashboard dashboard;
    private final AppointmentDAO   appointmentDAO = new AppointmentDAO();

    // Home screen ko dashboard navigation ke saath tayyar karta hai.
    public PatientHomePanel(PatientDashboard dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        buildUI();
    }

    // Poori UI build karna + DB se data load karna
    private void buildUI() {
        JPanel content = new JPanel(new MigLayout(
                "fillx, insets 40, gapy 24",
                "[grow]",
                "[][][][grow]"
        ));
        content.setBackground(Theme.BG);

        // Patient ka naam Session se
        String name = (Session.currentPatient != null)
                ? Session.currentPatient.getFullName() : "Patient";
        String firstName = name.split(" ")[0]; // Sirf pehla naam

        // ── Greeting ──────────────────────────────────────────
        content.add(Theme.pageTitle("Hello, " + firstName + "!"), "wrap 4");
        content.add(Theme.mutedLabel("Here's what's happening with your health."), "wrap 24");

        content.add(new PatientHomeStatsPanel(), "growx, wrap 32");

        // ── Next Appointment ──────────────────────────────────
        content.add(Theme.sectionLabel("Your Next Appointment"), "wrap 12");
        content.add(buildNextAppointmentCard(), "growx, wrap");

        // Scroll pane mein wrap karo (content zyada ho sakta hai)
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setBackground(Theme.BG);
        scroll.getViewport().setBackground(Theme.BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
    }

    // Next upcoming appointment card - ya "No appointment" message
    private JPanel buildNextAppointmentCard() {
        JPanel card = Theme.card();
        card.setLayout(new MigLayout("insets 24, fillx", "[grow][]", "[]8[]"));

        try {
            int patientId = Session.currentPatient != null ? Session.currentPatient.getId() : -1;
            List<Appointment> list = appointmentDAO.getForPatient(patientId);

            // Pehla Scheduled appointment dhundho (list already newest pehle sorted hai)
            Appointment next = null;
            for (int i = list.size() - 1; i >= 0; i--) {
                if ("Scheduled".equals(list.get(i).getStatus())) {
                    next = list.get(i);
                }
            }

            if (next != null) {
                // Appointment mili - details dikhao
                JLabel doctorLbl = new JLabel("Dr. " + next.getDoctorName());
                doctorLbl.setFont(new Font("Dialog", Font.BOLD, 16));
                doctorLbl.setForeground(Theme.TEXT);

                JLabel dateLbl = Theme.mutedLabel(next.getApptDate() + "  at  " + next.getApptTime());

                // Status pill
                JLabel statusLbl = new JLabel("  " + next.getStatus() + "  ");
                statusLbl.setFont(Theme.fontSmall());
                statusLbl.setForeground(Theme.statusColor(next.getStatus()));
                statusLbl.setBackground(Theme.statusBgColor(next.getStatus()));
                statusLbl.setOpaque(true);

                // Book another button
                JButton bookBtn = Theme.secondaryButton("Book Another");
                bookBtn.addActionListener(e -> onBookClicked());

                card.add(doctorLbl, "wrap 4");
                card.add(dateLbl, "");
                card.add(statusLbl, "right, wrap");
                card.add(bookBtn, "right, span 2");

            } else {
                // Koi appointment nahi
                JLabel noAppt = new JLabel("No upcoming appointments.");
                noAppt.setFont(Theme.fontBody());
                noAppt.setForeground(Theme.MUTED);

                JButton bookBtn = Theme.primaryButton("Book Now");
                bookBtn.addActionListener(e -> onBookClicked());

                card.add(noAppt, "wrap 16");
                card.add(bookBtn, "");
            }

        } catch (SQLException ex) {
            card.add(new JLabel("Data load nahi ho saka: " + ex.getMessage()));
        }

        return card;
    }

    // Booking screen par le jaata hai.
    private void onBookClicked() { dashboard.showPanel(PatientDashboard.BOOK); }
}

