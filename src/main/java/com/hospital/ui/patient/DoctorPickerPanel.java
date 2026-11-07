package com.hospital.ui.patient;

// Doctor picker specialization ke hisaab se doctor cards dikhata hai.

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.hospital.dao.DoctorDAO;
import com.hospital.model.Doctor;
import com.hospital.ui.Theme;

import net.miginfocom.swing.MigLayout;

public class DoctorPickerPanel extends JPanel {
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final JPanel cards = new JPanel(new MigLayout("fillx, insets 0, gapy 8", "[grow][grow]", "[]"));
    private final JLabel selectedLabel = Theme.mutedLabel("No doctor selected");
    private Doctor selectedDoctor;
    private Consumer<Doctor> selectionListener = doctor -> { };

    // Specialization dropdown aur doctor cards banata hai.
    public DoctorPickerPanel() {
        super(new MigLayout("fillx, insets 0, gapy 8", "[grow]", "[]"));
        setBackground(Theme.BG);
        add(Theme.sectionLabel("Step 1 - Specialization"), "wrap 8");
        add(buildSpecializationCombo(), "growx, h 42!, wrap");
        cards.setBackground(Theme.BG);
        add(Theme.sectionLabel("Step 2 - Choose Doctor"), "wrap 8");
        add(cards, "growx, wrap");
        add(selectedLabel, "wrap");
    }

    // Specialization list load karke doctor cards refresh karta hai.
    private JComboBox<String> buildSpecializationCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(Theme.fontBody());
        combo.addItem("-- Select Specialization --");
        try {
            for (String specialization : doctorDAO.getAllSpecializations()) combo.addItem(specialization);
        } catch (SQLException ex) { showError("Specializations load nahi hui: " + ex.getMessage()); }
        combo.addActionListener(event -> onSpecializationSelected(combo));
        return combo;
    }

    // Chuni specialization ke doctors cards mein load karta hai.
    private void onSpecializationSelected(JComboBox<String> combo) {
        String specialization = (String) combo.getSelectedItem();
        if (specialization != null && !specialization.startsWith("--")) loadDoctors(specialization);
    }

    // DAO se doctor cards banata hai.
    private void loadDoctors(String specialization) {
        cards.removeAll();
        clearSelection();
        try {
            List<Doctor> doctors = doctorDAO.getBySpecialization(specialization);
            if (doctors.isEmpty()) cards.add(Theme.mutedLabel("Is specialization mein koi doctor nahi."));
            for (int i = 0; i < doctors.size(); i++) cards.add(buildDoctorCard(doctors.get(i)), i % 2 == 1 ? "wrap" : "");
        } catch (SQLException ex) { showError("Doctors load nahi hue: " + ex.getMessage()); }
        cards.revalidate();
        cards.repaint();
    }

    // Ek doctor ka selectable card banata hai.
    private JPanel buildDoctorCard(Doctor doctor) {
        JPanel card = Theme.card();
        card.setLayout(new MigLayout("insets 16, fillx", "[grow]", "[]4[]4[]"));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel name = new JLabel("Dr. " + doctor.getFullName());
        name.setFont(new Font("Dialog", Font.BOLD, 14));
        name.setForeground(Theme.TEXT);
        card.add(name, "wrap");
        card.add(Theme.mutedLabel(doctor.getSpecialization()), "wrap");
        card.add(Theme.mutedLabel(doctor.getExperienceYears() + " yrs experience"));
        card.addMouseListener(new MouseAdapter() {
            // Click se doctor select karke time panel ko update karta hai.
            @Override public void mouseClicked(MouseEvent event) { selectDoctor(doctor); }
            // Hover par card ka background badalta hai.
            @Override public void mouseEntered(MouseEvent event) { card.setBackground(Theme.ROW_HOVER); }
            // Hover chhodne par card ka rang wapas karta hai.
            @Override public void mouseExited(MouseEvent event) { card.setBackground(Color.WHITE); }
        });
        return card;
    }

    // Doctor chune jaane par callback register karta hai.
    public void setDoctorSelectionListener(Consumer<Doctor> listener) { selectionListener = listener; }

    // Chune hue doctor ko wapas deta hai.
    public Doctor getSelectedDoctor() { return selectedDoctor; }

    // Doctor selection reset karta hai.
    public void clearSelection() {
        selectedDoctor = null;
        selectedLabel.setText("No doctor selected");
        selectedLabel.setForeground(Theme.MUTED);
        selectionListener.accept(null);
    }

    // Chune doctor ke baad callback ko notify karta hai.
    private void selectDoctor(Doctor doctor) {
        selectedDoctor = doctor;
        selectedLabel.setText("Selected: Dr. " + doctor.getFullName());
        selectedLabel.setForeground(Theme.ACCENT);
        selectionListener.accept(doctor);
    }

    // Load error ko warning popup mein dikhata hai.
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Doctors", JOptionPane.WARNING_MESSAGE);
    }
}