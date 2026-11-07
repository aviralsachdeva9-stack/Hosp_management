package com.hospital.ui.doctor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.hospital.dao.PatientDAO;
import com.hospital.dao.RecordDAO;
import com.hospital.model.MedicalRecord;
import com.hospital.model.Patient;
import com.hospital.ui.Theme;

import net.miginfocom.swing.MigLayout;

// Doctor ko patient search karke uski medical history dikhata hai.
public class PatientHistoryPanel extends JPanel {
    private final PatientDAO patientDAO = new PatientDAO();
    private final RecordDAO recordDAO = new RecordDAO();
    private final DefaultComboBoxModel<Patient> patients = new DefaultComboBoxModel<>();
    private final JComboBox<Patient> patientCombo = new JComboBox<>(patients);
    private final DefaultTableModel records = new DefaultTableModel(
            new String[]{"Date", "Doctor", "Diagnosis", "Prescription"}, 0) {
        // Medical records table ko read-only rakhta hai.
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    // Search fields aur history table arrange karta hai.
    public PatientHistoryPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(36, 40, 32, 40));
        JPanel top = new JPanel(new MigLayout("insets 0", "[grow][180!]", "[]"));
        top.setBackground(Theme.BG);
        top.add(Theme.pageTitle("Patient History"));
        JTextField search = Theme.styledField();
        search.setToolTipText("Search patients by name");
        top.add(search, "growx, h 42!");
        patientCombo.setRenderer(new PatientRenderer());
        patientCombo.addActionListener(e -> loadRecords());
        JPanel controls = new JPanel(new BorderLayout(8, 0));
        controls.setOpaque(false);
        controls.add(patientCombo, BorderLayout.CENTER);
        JButton find = Theme.secondaryButton("Search");
        find.addActionListener(e -> onSearchClicked(search));
        search.addActionListener(e -> onSearchClicked(search));
        controls.add(find, BorderLayout.EAST);
        JPanel north = new JPanel(new BorderLayout(0, 12));
        north.setOpaque(false);
        north.add(top, BorderLayout.NORTH);
        north.add(controls, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);
        JTable table = new JTable(records);
        Theme.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    // Search action se patient name aur matching records leta hai.
    private void onSearchClicked(JTextField search) { searchPatients(search.getText().trim()); }

    // Matching patients ko selector mein load karta hai.
    private void searchPatients(String name) {
        patients.removeAllElements();
        records.setRowCount(0);
        if (name.isBlank()) return;
        try {
            List<Patient> matches = patientDAO.searchByName(name);
            for (Patient patient : matches) patients.addElement(patient);
            if (matches.isEmpty()) JOptionPane.showMessageDialog(this, "Patient nahi mila.");
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Search nahi hui: " + ex.getMessage()); }
    }

    // Chune patient ke medical records table mein dikhata hai.
    private void loadRecords() {
        Patient patient = (Patient) patientCombo.getSelectedItem();
        records.setRowCount(0);
        if (patient == null) return;
        try {
            List<MedicalRecord> list = recordDAO.getForPatient(patient.getId());
            for (MedicalRecord record : list) records.addRow(new Object[]{record.getRecordDate(),
                    record.getDoctorName(), record.getDiagnosis(), record.getPrescription()});
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "History load nahi hui: " + ex.getMessage()); }
    }

    // Patient selector mein naam dikhata hai.
    private static class PatientRenderer extends DefaultListCellRenderer {
        // Patient object ko full name ke roop mein render karta hai.
        @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean selected, boolean focus) {
            super.getListCellRendererComponent(list, value, index, selected, focus);
            if (value instanceof Patient patient) setText(patient.getFullName());
            return this;
        }
    }
}