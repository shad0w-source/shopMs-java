package com.example;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainLayout extends JPanel {

    private static final Color COLOR_CONTENT_BG = new Color(241, 245, 249);
    // private static final Color COLOR_PRIMARY_NAV = new Color(37, 99, 235);

    private CardLayout cardLayout;
    private JPanel routingPanel;
    private JPanel[] navButtons;
    // private final String[] panelNames;

    public MainLayout() {
        setBackground(COLOR_CONTENT_BG);

        add(new Sidebar(), BorderLayout.WEST);

        add(routingPanel, BorderLayout.CENTER);
    }

    private void setTabActive(int index) {
        for (int i = 0; i < navButtons.length; i++) {
            JLabel lbl = (JLabel) navButtons[i].getComponent(0);
            if (i == index) {
                navButtons[i].setBackground(COLOR_PRIMARY_NAV);
                lbl.setForeground(Color.WHITE);
            } else {
                navButtons[i].setBackground(COLOR_SIDEBAR_BG);
                lbl.setForeground(COLOR_TEXT_MUTED);
            }
        }
        repaint();
    }

}
