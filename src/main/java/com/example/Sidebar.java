package com.example;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Sidebar extends JPanel {

    private static final Color COLOR_SIDEBAR_BG = new Color(30, 41, 59);
    private static final Color COLOR_TEXT_MUTED = new Color(148, 163, 184);
    private static final Color COLOR_CONTENT_BG = new Color(241, 245, 249);
    private static final Color COLOR_PRIMARY_NAV = new Color(37, 99, 235);

    private CardLayout cardLayout;
    private JPanel routingPanel;
    private JPanel[] navButtons;
    private final String[] panelNames;

    public Sidebar() {
        this.panelNames = new String[]{"Sales", "Inventory", "Orders"};
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(240, 850));
        setBackground(COLOR_SIDEBAR_BG);
        setBorder(new EmptyBorder(30, 0, 30, 0));

        JLabel logo = new JLabel("STOCK_PRO");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setBorder(new EmptyBorder(0, 24, 0, 24));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(logo);

        add(Box.createRigidArea(new Dimension(0, 30)));

        navButtons = new JPanel[panelNames.length];
        for (int i = 0; i < panelNames.length; i++) {
            final int index = i;
            JPanel navItem = new JPanel(new BorderLayout());
            navItem.setMaximumSize(new Dimension(240, 48));
            navItem.setPreferredSize(new Dimension(240, 48));
            navItem.setBackground(COLOR_SIDEBAR_BG);
            navItem.setBorder(new EmptyBorder(0, 24, 0, 24));
            navItem.setAlignmentX(Component.LEFT_ALIGNMENT);
            navItem.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel label = new JLabel(panelNames[i]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setForeground(COLOR_TEXT_MUTED);
            navItem.add(label, BorderLayout.CENTER);

            cardLayout = new CardLayout();
            routingPanel = new JPanel(cardLayout);

            routingPanel.setBackground(COLOR_CONTENT_BG);

            // This line now compiles perfectly!
            // routingPanel.add(
            //         new ProductCatalogPanel(productModel, ordersModel), "Sales");
            // routingPanel.add(
            //         new InventoryPanel(productModel), "Inventory");
            // routingPanel.add(
            //         new OrdersPanel(ordersModel), "Orders");
            navItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    setTabActive(index);

                    cardLayout.show(routingPanel, panelNames[index]);
                }
            });

            navButtons[i] = navItem;
            add(navItem);
        }

        add(Box.createVerticalGlue());
    }



}
