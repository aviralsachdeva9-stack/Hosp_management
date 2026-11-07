package com.hospital.ui;

// MainFrame.java - poori app ki ek akeli JFrame.
// Andar CardLayout hai jo screens switch karta hai.
// Login -> Register -> Patient Dashboard ya Doctor Dashboard.
// Koi bhi screen: mainFrame.show("screenName") se switch ho jati hai.

import com.hospital.ui.auth.LoginPanel;
import com.hospital.ui.auth.RegisterPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    // CardLayout screens ke beech switch karta hai bina nayi window banaye
    private CardLayout cardLayout;
    private JPanel     cardPanel;  // Yahi andar sab screens hain

    // Screen ke naam - typos avoid karne ke liye constants
    public static final String LOGIN    = "LOGIN";
    public static final String REGISTER = "REGISTER";
    public static final String PATIENT  = "PATIENT_DASH";
    public static final String DOCTOR   = "DOCTOR_DASH";

    public MainFrame() {
        setupFrame();
        setupCards();
    }

    // JFrame ki basic properties set karna
    private void setupFrame() {
        setTitle("Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 680));
        setPreferredSize(new Dimension(1200, 720));
        getContentPane().setBackground(Theme.BG);
    }

    // Sab screens ko CardLayout mein add karna
    private void setupCards() {
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(Theme.BG);

        // Login aur Register pehle add karo
        // Dashboard baad mein add hoga jab user login kare
        cardPanel.add(new LoginPanel(this),    LOGIN);
        cardPanel.add(new RegisterPanel(this), REGISTER);

        add(cardPanel, BorderLayout.CENTER);

        // Pehle Login screen dikhao
        cardLayout.show(cardPanel, LOGIN);

        pack();
        setLocationRelativeTo(null); // Screen ke center mein open ho
    }

    // Koi bhi screen yeh method call karke dusri screen pe ja sakti hai
    public void show(String screenName) {
        cardLayout.show(cardPanel, screenName);
    }

    // Dashboard dynamically add karna - login ke baad call hota hai.
    // Pehle purana dashboard remove karo (agar tha), phir naya add karo.
    public void addCard(JPanel panel, String name) {
        // Pehle agar same name ka card hai to remove karo
        for (Component c : cardPanel.getComponents()) {
            if (c.getName() != null && c.getName().equals(name)) {
                cardPanel.remove(c);
                break;
            }
        }
        panel.setName(name);
        cardPanel.add(panel, name);
    }
}

