package com.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class InventoryPanel extends JPanel {

    private static final Color COLOR_BG = new Color(245, 247, 250);
    private static final Color COLOR_PRIMARY = new Color(11, 37, 69);
    private static final Color COLOR_TEXT_MAIN = new Color(17, 24, 39);
    private static final Color COLOR_TEXT_MUTED = new Color(107, 114, 128);
    private static final Color COLOR_BORDER = new Color(243, 244, 246);
    private static final Color COLOR_SECONDARY_BTN = new Color(212, 221, 247);

    // 1. DECLARE THE MODEL FIELD HERE
    private ProductModel productModel;

    // 2. PASS THE MODEL INTO THE CONSTRUCTOR HERE
    public InventoryPanel(ProductModel productModel) {
        this.productModel = productModel; // Save it to use inside the button click below

        setLayout(new BorderLayout(0, 20));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(Color.WHITE);
        page.setBorder(BorderFactory.createLineBorder(new Color(230, 233, 238), 1));

        // Top Header Section
        JPanel headerWrapper = new JPanel();
        headerWrapper.setLayout(new BoxLayout(headerWrapper, BoxLayout.Y_AXIS));
        headerWrapper.setBackground(Color.WHITE);
        headerWrapper.setBorder(new EmptyBorder(25, 25, 15, 25));

        // Search Bar with Active Placeholder Logic
        JPanel searchBarRow = new JPanel(new BorderLayout());
        searchBarRow.setBackground(Color.WHITE);
        searchBarRow.setBorder(new EmptyBorder(0, 0, 18, 0));

        String placeholder = "  SEARCH INVENTORY...";
        JTextField searchField = new JTextField(placeholder);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setForeground(COLOR_TEXT_MUTED);
        searchField.setBackground(Color.WHITE);
        searchField.setBorder(BorderFactory.createLineBorder(new Color(230, 233, 238), 1));
        searchField.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(placeholder)) {
                    searchField.setText("");
                    searchField.setForeground(COLOR_TEXT_MAIN);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText(placeholder);
                    searchField.setForeground(COLOR_TEXT_MUTED);
                }
            }
        });
        searchBarRow.add(searchField, BorderLayout.CENTER);
        headerWrapper.add(searchBarRow);

        // Titles Row (Holds text on the left, Add Product Button on the right)
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Product Inventory Master");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(COLOR_TEXT_MAIN);

        JPanel titleGroup = new JPanel(new GridLayout(1, 1));
        titleGroup.setBackground(Color.WHITE);
        titleGroup.add(titleLabel);
        titleRow.add(titleGroup, BorderLayout.WEST);

        // --- ADD PRODUCT BUTTON (ROUNDED DESIGN) ---
        JButton addProductBtn = new JButton("+ ADD PRODUCT") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        addProductBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addProductBtn.setForeground(Color.WHITE);
        addProductBtn.setOpaque(false);
        addProductBtn.setContentAreaFilled(false);
        addProductBtn.setBorderPainted(false);
        addProductBtn.setFocusPainted(false);
        addProductBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addProductBtn.setPreferredSize(new Dimension(140, 40));
        
        addProductBtn.addActionListener(e -> {
            // Obtain the master application JFrame housing this context panel
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            
            // Pass down your operational initialization database mapping model connection instance
            AddProductDialog dialog = new AddProductDialog(topFrame, this.productModel);
            dialog.setVisible(true);
            
            if (dialog.isSucceeded()) {
                // Option context hook: refresh your list layout components dynamically here!
                System.out.println("Product was added! Reloading dynamic UI list panels...");
            }
        });

        // Wrap button to maintain clean placement alignment
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnWrapper.setBackground(Color.WHITE);
        btnWrapper.add(addProductBtn);
        titleRow.add(btnWrapper, BorderLayout.EAST);
        // ------------------------------------------

        headerWrapper.add(titleRow);
        page.add(headerWrapper, BorderLayout.NORTH);

        // Table Rows Wrapper
        JPanel rowsWrapper = new JPanel();
        rowsWrapper.setLayout(new BoxLayout(rowsWrapper, BoxLayout.Y_AXIS));
        rowsWrapper.setBackground(Color.WHITE);

        // Grid Columns Header Bar
        rowsWrapper.add(createTableHeaderBar());
        rowsWrapper.add(Box.createVerticalStrut(4));

        // Populating mock inventory display rows
        addInventoryRow(rowsWrapper, "Ice Cream Tub", "FROZEN", "$12.50", 450);
        addInventoryRow(rowsWrapper, "Sparkling Water", "BEVERAGES", "$2.99", 1240);
        addInventoryRow(rowsWrapper, "Organic Whole Milk", "DAIRY", "$5.45", 12);
        addInventoryRow(rowsWrapper, "Greek Yogurt 0%", "DAIRY", "$4.20", 88);

        JScrollPane scrollPane = new JScrollPane(rowsWrapper);
        scrollPane.setBorder(new EmptyBorder(0, 10, 10, 10));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        page.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomSpacer = new JPanel();
        bottomSpacer.setBackground(Color.WHITE);
        bottomSpacer.setPreferredSize(new Dimension(Integer.MAX_VALUE, 15));
        page.add(bottomSpacer, BorderLayout.SOUTH);

        add(page, BorderLayout.CENTER);
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

        String[] headers = {"ITEM NAME", "CATEGORY", "UNIT PRICE", "STOCK LEVEL", "ACTIONS"};
        int[] boundsX = {25, 225, 385, 545, 745};
        int[] widths = {180, 140, 140, 160, 140};

        for (int i = 0; i < headers.length; i++) {
            JLabel label = new JLabel(headers[i]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setForeground(COLOR_TEXT_MUTED);
            label.setBounds(boundsX[i], 0, widths[i], 40);
            headerBar.add(label);
        }

        return headerBar;
    }

    private void addInventoryRow(JPanel parent, String itemName, String category, String unitPrice, int stock) {
        JPanel row = new JPanel(null) {
            @Override
            public Dimension getPreferredSize() { return new Dimension(Integer.MAX_VALUE, 65); }
            @Override
            public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, 65); }
        };
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        JLabel nameLabel = new JLabel(itemName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(COLOR_TEXT_MAIN);
        nameLabel.setBounds(25, 22, 180, 20);
        row.add(nameLabel);

        JLabel catLabel = new JLabel(category);
        catLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        catLabel.setForeground(COLOR_TEXT_MUTED);
        catLabel.setBounds(225, 22, 140, 20);
        row.add(catLabel);

        JLabel priceLabel = new JLabel(unitPrice);
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        priceLabel.setForeground(COLOR_TEXT_MAIN);
        priceLabel.setBounds(385, 22, 140, 20);
        row.add(priceLabel);

        JLabel stockLabel = new JLabel("STOCK: " + stock, SwingConstants.CENTER);
        stockLabel.setOpaque(true);
        if (stock <= 15) {
            stockLabel.setBackground(new Color(254, 226, 226));
            stockLabel.setForeground(new Color(220, 38, 38));
        } else {
            stockLabel.setBackground(new Color(240, 253, 244));
            stockLabel.setForeground(new Color(22, 101, 52));
        }
        stockLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        stockLabel.setBounds(545, 18, 100, 26);
        row.add(stockLabel);

        JButton editBtn = new JButton("Update Stock");
        editBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        editBtn.setForeground(COLOR_PRIMARY);
        editBtn.setBackground(COLOR_SECONDARY_BTN);
        editBtn.setFocusPainted(false);
        editBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        editBtn.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        editBtn.setBounds(745, 18, 110, 28);
        row.add(editBtn);

        parent.add(row);
    }
}