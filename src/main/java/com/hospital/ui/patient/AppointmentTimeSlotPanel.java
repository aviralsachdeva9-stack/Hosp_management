package com.hospital.ui.patient;

// Appointment ki date aur available time slots select karwata hai.

import java.awt.Color;
import java.awt.Dimension;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.hospital.dao.AppointmentDAO;
import com.hospital.model.Doctor;
import com.hospital.ui.Theme;

import net.miginfocom.swing.MigLayout;

public class AppointmentTimeSlotPanel extends JPanel {
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final JPanel slots = new JPanel(new MigLayout("fillx, insets 0, gapy 8", "[][][][][]", "[][]"));
    private final DatePicker datePicker;
    private final JLabel selectedLabel = Theme.mutedLabel("No time selected");
    private Doctor doctor;
    private String selectedTime;

    // Date picker aur time-slot area set karta hai.
    public AppointmentTimeSlotPanel() {
        super(new MigLayout("fillx, insets 0, gapy 8", "[grow]", "[]"));
        setBackground(Theme.BG);
        DatePickerSettings settings = new DatePickerSettings();
        datePicker = new DatePicker(settings);
        settings.setDateRangeLimits(LocalDate.now(), null);
        datePicker.addDateChangeListener(event -> refreshTimeSlots());
        slots.setBackground(Theme.BG);
        add(datePicker, "growx, h 42!, wrap");
        add(slots, "growx, wrap");
        add(selectedLabel, "wrap");
    }

    // Doctor badalne par booked slots dobara load karta hai.
    public void setDoctor(Doctor doctor) { this.doctor = doctor; refreshTimeSlots(); }

    // Chune doctor aur date ke liye booked times disable karta hai.
    private void refreshTimeSlots() {
        slots.removeAll();
        selectedTime = null;
        selectedLabel.setText("No time selected");
        if (doctor == null || datePicker.getDate() == null) { repaintSlots(); return; }
        List<String> booked;
        try { booked = appointmentDAO.getBookedTimes(doctor.getId(), datePicker.getDate().toString()); }
        catch (SQLException ex) { showError("Booked times check nahi ho paye: " + ex.getMessage()); return; }
        for (int i = 0; i < 17; i++) addTimeButton(i, booked);
        repaintSlots();
    }

    // Ek booked ya available time ka button banata hai.
    private void addTimeButton(int index, List<String> booked) {
        int minutes = 9 * 60 + index * 30;
        String label = String.format("%02d:%02d", minutes / 60, minutes % 60);
        JButton button = new JButton(label);
        button.setFont(Theme.fontSmall());
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(80, 36));
        boolean isBooked = booked.stream().anyMatch(time -> time.startsWith(label));
        if (isBooked) {
            button.setEnabled(false);
            button.setBackground(new Color(0xEEEEEE));
            button.setForeground(Theme.MUTED);
            button.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(Theme.ACCENT);
            button.setBorder(BorderFactory.createLineBorder(Theme.ACCENT));
            button.addActionListener(event -> onTimeSlotClicked(label));
        }
        slots.add(button, (index + 1) % 5 == 0 ? "wrap" : "");
    }

    // Chuna hua slot label aur database time set karta hai.
    private void onTimeSlotClicked(String label) {
        selectedTime = label + ":00";
        selectedLabel.setText("Selected: " + label);
        selectedLabel.setForeground(Theme.ACCENT);
    }

    // Chuni hui appointment date deta hai.
    public LocalDate getDate() { return datePicker.getDate(); }

    // Chuna hua database time deta hai.
    public String getSelectedTime() { return selectedTime; }

    // Date/time selection ko reset karta hai.
    public void clearSelection() { selectedTime = null; selectedLabel.setText("No time selected"); refreshTimeSlots(); }

    // Slot area ko layout ke saath repaint karta hai.
    private void repaintSlots() { slots.revalidate(); slots.repaint(); }

    // Load error ko warning popup mein dikhata hai.
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Time Slots", JOptionPane.WARNING_MESSAGE);
    }
}