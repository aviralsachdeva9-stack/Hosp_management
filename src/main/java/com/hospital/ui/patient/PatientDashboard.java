package com.hospital.ui.patient;

// PatientDashboard.java - Patient ka poora dashboard container.
// Do hisse hain: left = green sidebar (menu), right = content area (CardLayout).
// Jab patient koi menu button dabata hai, right side ka panel switch ho jaata hai.

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

import com.hospital.ui.MainFrame;
import com.hospital.ui.Theme;
import com.hospital.util.Session;

public class PatientDashboard extends JPanel {

    private final MainFrame frame;

    // Right side ka CardLayout - panels switch karne ke liye
    private CardLayout contentLayout;
    private JPanel     contentPanel;

    // Screen names - typo se bachne ke liye constants
    public static final String HOME       = "HOME";
    public static final String BOOK       = "BOOK";
    public static final String APPTS      = "APPTS";
    public static final String HISTORY    = "HISTORY";
    public static final String PROFILE    = "PROFILE";

    private JButton[] menuButtons;

    // Dashboard ko sidebar aur screen cards ke saath tayyar karta hai.
    public PatientDashboard(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());

        add(buildSidebar(),      BorderLayout.WEST);   // Left: green sidebar
        add(buildContentArea(),  BorderLayout.CENTER); // Right: switching panels
    }

    // Sidebar ke navigation aur logout buttons banata hai.
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));

        // App name at top
        JLabel appName = new JLabel("  HospitalMS");
        appName.setFont(new Font("Dialog", Font.BOLD, 16));
        appName.setForeground(Color.WHITE);
        appName.setBorder(BorderFactory.createEmptyBorder(28, 16, 28, 16));
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(appName);

        menuButtons = new JButton[]{Theme.sidebarButton("  Home"), Theme.sidebarButton("  Book Appointment"),
                Theme.sidebarButton("  My Appointments"), Theme.sidebarButton("  Medical History"),
                Theme.sidebarButton("  My Profile")};
        String[] pages = {HOME, BOOK, APPTS, HISTORY, PROFILE};
        for (int i = 0; i < menuButtons.length; i++) {
            final String page = pages[i];
            menuButtons[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            menuButtons[i].addActionListener(e -> showPanel(page));
            sidebar.add(menuButtons[i]);
        }

        // Spacer - pushes logout to bottom
        sidebar.add(Box.createVerticalGlue());

        JButton btnLogout = Theme.sidebarButton("  Logout");
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnLogout.setForeground(new Color(0xFF8A80)); // Reddish warning color
        btnLogout.addActionListener(e -> onLogoutClicked());
        sidebar.add(btnLogout);
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));

        return sidebar;
    }

    // CardLayout mein patient ke paanch screens rakhta hai.
    private JPanel buildContentArea() {
        contentLayout = new CardLayout();
        contentPanel  = new JPanel(contentLayout);
        contentPanel.setBackground(Theme.BG);

        // Sab panels add karo
        contentPanel.add(new PatientHomePanel(this),        HOME);
        contentPanel.add(new BookAppointmentPanel(this),    BOOK);
        contentPanel.add(new MyAppointmentsPanel(), APPTS);
        contentPanel.add(new MedicalHistoryPanel(), HISTORY);
        contentPanel.add(new PatientProfilePanel(), PROFILE);

        // Home default
        showPanel(HOME);

        return contentPanel;
    }

    // Screen switch karke active menu ko highlight karta hai.
    public void showPanel(String name) {
        contentLayout.show(contentPanel, name);
        updateSidebarHighlight(name);
    }

    // Sirf current page ke button ko active rang deta hai.
    private void updateSidebarHighlight(String active) {
        String[] pages = {HOME, BOOK, APPTS, HISTORY, PROFILE};
        for (int i = 0; i < menuButtons.length; i++) Theme.setSidebarActive(menuButtons[i], pages[i].equals(active));
    }

    // Logout confirm karke session clear karta hai.
    private void onLogoutClicked() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Logout karna chahte ho?", "Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            Session.logout();
            frame.show(MainFrame.LOGIN);
        }
    }
}
