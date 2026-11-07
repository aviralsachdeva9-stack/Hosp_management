package com.hospital.ui.doctor;

// DoctorDashboard.java - Doctor sidebar + card-based screens.

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.hospital.ui.MainFrame;
import com.hospital.ui.Theme;
import com.hospital.util.Session;

// Doctor dashboard shell: sidebar aur CardLayout se screen switch karta hai.
public class DoctorDashboard extends JPanel {

    private final MainFrame frame;
    private CardLayout contentLayout;
    private JPanel contentPanel;
    private JButton[] menuButtons;
    private static final String[] PAGES = {"HOME", "APPOINTMENTS", "DIAGNOSIS", "HISTORY", "PROFILE"};
    private static final String[] LABELS = {"Home", "Appointments", "Add Diagnosis", "Patient History", "My Profile"};

    // Sidebar aur doctor ke screen cards tayyar karta hai.
    public DoctorDashboard(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);
    }

    // Sidebar navigation aur logout button banata hai.
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        JLabel title = new JLabel("  HospitalMS");
        title.setFont(new Font("Dialog", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(28, 16, 28, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(title);
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(separator);
        menuButtons = new JButton[PAGES.length];
        for (int i = 0; i < PAGES.length; i++) {
            final String page = PAGES[i];
            menuButtons[i] = Theme.sidebarButton("  " + LABELS[i]);
            menuButtons[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            menuButtons[i].addActionListener(e -> showPanel(page));
            sidebar.add(menuButtons[i]);
        }
        sidebar.add(Box.createVerticalGlue());
        JButton logout = Theme.sidebarButton("  Logout");
        logout.setForeground(new Color(0xFF8A80));
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        logout.addActionListener(e -> onLogout());
        sidebar.add(logout);
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));
        return sidebar;
    }

    // Saare doctor screens CardLayout mein register karta hai.
    private JPanel buildContent() {
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(Theme.BG);
        contentPanel.add(new DoctorHomePanel(), PAGES[0]);
        contentPanel.add(new DoctorAppointmentsPanel(), PAGES[1]);
        contentPanel.add(new AddDiagnosisPanel(), PAGES[2]);
        contentPanel.add(new PatientHistoryPanel(), PAGES[3]);
        contentPanel.add(new DoctorProfilePanel(), PAGES[4]);
        showPanel(PAGES[0]);
        return contentPanel;
    }

    // Screen switch karke active menu highlight karta hai.
    private void showPanel(String page) {
        contentLayout.show(contentPanel, page);
        for (int i = 0; i < PAGES.length; i++) Theme.setSidebarActive(menuButtons[i], PAGES[i].equals(page));
    }

    // Logout confirm karke session clear karta hai.
    private void onLogout() {
        if (JOptionPane.showConfirmDialog(this, "Logout karna chahte ho?", "Logout",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Session.logout();
            frame.show(MainFrame.LOGIN);
        }
    }
}

