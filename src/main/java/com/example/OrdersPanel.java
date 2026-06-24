package com.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class OrdersPanel extends JPanel {

    private static final Color COLOR_BG = new Color(245, 247, 250);
    private static final Color COLOR_PRIMARY = new Color(11, 37, 69);
    private static final Color COLOR_TEXT_MAIN = new Color(17, 24, 39);
    private static final Color COLOR_TEXT_MUTED = new Color(107, 114, 128);
    private static final Color COLOR_BORDER = new Color(243, 244, 246);
    private static final Color COLOR_SECONDARY_BTN = new Color(212, 221, 247);
    private static final Color COLOR_BLUE_LINK = new Color(11, 91, 184);

    private OrdersModel ordersModel;
    private JPanel rowsWrapper;

    // Modified Constructor to accept your operational relational tracking model
    public OrdersPanel(OrdersModel ordersModel) {
        this.ordersModel = ordersModel;

        setLayout(new BorderLayout(0, 20));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(Color.WHITE);
        page.setBorder(BorderFactory.createLineBorder(new Color(230, 233, 238), 1));

        // Top Header Titles
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(Color.WHITE);
        topHeader.setBorder(new EmptyBorder(25, 25, 15, 25));

        JLabel titleLabel = new JLabel("Recent Orders");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(COLOR_PRIMARY);
        
        JLabel subLabel = new JLabel("Live monitoring distribution engine log metrics.");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(COLOR_TEXT_MUTED);

        JPanel titleGroup = new JPanel(new java.awt.GridLayout(2, 1, 0, 4));
        titleGroup.setBackground(Color.WHITE);
        titleGroup.add(titleLabel);
        titleGroup.add(subLabel);
        topHeader.add(titleGroup, BorderLayout.WEST);
        page.add(topHeader, BorderLayout.NORTH);

        // Table Rows Wrapper
        rowsWrapper = new JPanel();
        rowsWrapper.setLayout(new BoxLayout(rowsWrapper, BoxLayout.Y_AXIS));
        rowsWrapper.setBackground(Color.WHITE);

        // Grid Columns Header Bar
        rowsWrapper.add(createTableHeaderBar());
        rowsWrapper.add(Box.createVerticalStrut(4));

        // Pull active entries out of the database connection layer
        refreshOrdersTable();

        JScrollPane scrollPane = new JScrollPane(rowsWrapper);
        scrollPane.setBorder(new EmptyBorder(0, 10, 10, 10));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        page.add(scrollPane, BorderLayout.CENTER);

        // Extra padding area at bottom of page to ground the presentation cleanly
        JPanel bottomSpacer = new JPanel();
        bottomSpacer.setBackground(Color.WHITE);
        bottomSpacer.setPreferredSize(new Dimension(Integer.MAX_VALUE, 15));
        page.add(bottomSpacer, BorderLayout.SOUTH);

        add(page, BorderLayout.CENTER);
    }

    // Queries live database collections to populate UI rows
    public void refreshOrdersTable() {
        // Keeps header line item bar intact while sweeping row list children arrays
        if (rowsWrapper.getComponentCount() > 1) {
            for (int i = rowsWrapper.getComponentCount() - 1; i >= 1; i--) {
                rowsWrapper.remove(i);
            }
        }

        ArrayList<Object[]> ordersList = ordersModel.findAllOrdersForDisplayTable();
        for (Object[] order : ordersList) {
            String orderId = (String) order[0];
            String customerName = (String) order[1];
            String dateTime = (String) order[2];
            String totalAmount = (String) order[3];
            
            // Auto-compute avatar text strings from name tokens
            String avatarText = getAvatarInitials(customerName);

            addOrderRow(rowsWrapper, orderId, dateTime, avatarText, customerName, totalAmount);
        }

        rowsWrapper.revalidate();
        rowsWrapper.repaint();
    }

    // Helper utility to turn "Sarah Chen" -> "SC"
    private String getAvatarInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "??" ;
        String[] tokens = name.trim().split("\\s+");
        if (tokens.length == 1) return tokens[0].substring(0, Math.min(2, tokens[0].length())).toUpperCase();
        return (tokens[0].substring(0, 1) + tokens[1].substring(0, 1)).toUpperCase();
    }

    private JPanel createTableHeaderBar() {
        JPanel headerBar = new JPanel(null) {
            @Override
            public Dimension getPreferredSize() { return new Dimension(Integer.MAX_VALUE, 40); }
            @Override
            public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, 40); }
        };
        headerBar.setBackground(Color.WHITE);
        headerBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        String[] headers = {"ORDER ID", "DATE & TIME", "CUSTOMER", "ITEMS", "TOTAL"};
        int[] boundsX = {25, 185, 365, 585, 745};
        int[] widths = {140, 160, 200, 140, 140};

        for (int i = 0; i < headers.length; i++) {
            JLabel label = new JLabel(headers[i]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setForeground(COLOR_TEXT_MUTED);
            label.setBounds(boundsX[i], 0, widths[i], 40);
            headerBar.add(label);
        }

        return headerBar;
    }

    private void addOrderRow(JPanel parent, String orderId, String dateTime, String avatar, String customerName, String total) {
        JPanel row = new JPanel(null) {
            @Override
            public Dimension getPreferredSize() { return new Dimension(Integer.MAX_VALUE, 65); }
            @Override
            public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, 65); }
        };
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        JLabel idLabel = new JLabel(orderId);
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        idLabel.setForeground(COLOR_BLUE_LINK);
        idLabel.setBounds(25, 22, 140, 20);
        row.add(idLabel);

        JLabel dateLabel = new JLabel(dateTime);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(COLOR_TEXT_MUTED);
        dateLabel.setBounds(185, 22, 160, 20);
        row.add(dateLabel);

        JLabel avatarBadge = new JLabel(avatar, SwingConstants.CENTER);
        avatarBadge.setOpaque(true);
        avatarBadge.setBackground(new Color(219, 234, 254));
        avatarBadge.setForeground(COLOR_BLUE_LINK);
        avatarBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        avatarBadge.setBounds(365, 20, 24, 24);
        row.add(avatarBadge);

        JLabel nameLabel = new JLabel(customerName);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameLabel.setForeground(COLOR_TEXT_MAIN);
        nameLabel.setBounds(397, 22, 170, 20);
        row.add(nameLabel);

        JButton viewItemsBtn = new JButton("View Items");
        viewItemsBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        viewItemsBtn.setForeground(COLOR_PRIMARY);
        viewItemsBtn.setBackground(COLOR_SECONDARY_BTN);
        viewItemsBtn.setFocusPainted(false);
        viewItemsBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewItemsBtn.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        viewItemsBtn.setBounds(585, 18, 110, 28);
        row.add(viewItemsBtn);

        JLabel totalLabel = new JLabel(total);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        totalLabel.setForeground(COLOR_TEXT_MAIN);
        totalLabel.setBounds(745, 22, 120, 20);
        row.add(totalLabel);

        parent.add(row);
    }
}