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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class AddProductDialog extends JDialog {

    private static final Color COLOR_PRIMARY = new Color(11, 37, 69);
    private static final Color COLOR_TEXT_MAIN = new Color(17, 24, 39);
    private static final Color COLOR_TEXT_MUTED = new Color(107, 114, 128);
    private static final Color COLOR_BORDER = new Color(230, 233, 238);
    private static final Color COLOR_CANCEL_BG = new Color(243, 244, 246);

    private JTextField nameField;
    private JTextField descField;
    private JTextField priceField;
    private JTextField stockField;
    private boolean succeeded = false;

    private ProductModel productModel;

    public AddProductDialog(JFrame parent, ProductModel productModel) {
        super(parent, "Add New Product", true);
        this.productModel = productModel;

        setSize(420, 480);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // Main Container Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header Section
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Create Product Record");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(COLOR_PRIMARY);

        JLabel subLabel = new JLabel("Fill out the structural inventory specifications metadata.");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(COLOR_TEXT_MUTED);

        headerPanel.add(titleLabel);
        headerPanel.add(subLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Input Fields Grid Panel
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 0, 16));
        formPanel.setBackground(Color.WHITE);

        nameField = createFormField(formPanel, "Product Name *");
        descField = createFormField(formPanel, "Description");
        priceField = createFormField(formPanel, "Unit Price ($) *");
        stockField = createFormField(formPanel, "Initial Stock Quantity *");

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Interactive Footer Button Controls
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Cancel Button
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cancelBtn.setForeground(COLOR_TEXT_MAIN);
        cancelBtn.setBackground(COLOR_CANCEL_BG);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> dispose());
        footerPanel.add(cancelBtn);

        // Save Custom Rounded Button
        JButton saveBtn = new JButton("Save Record") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setOpaque(false);
        saveBtn.setContentAreaFilled(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setFocusPainted(false);
        saveBtn.setPreferredSize(new Dimension(110, 36));
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> handleSaveSubmit());
        footerPanel.add(saveBtn);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JTextField createFormField(JPanel container, String labelText) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(COLOR_TEXT_MAIN);
        wrapper.add(label, BorderLayout.NORTH);

        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        wrapper.add(field, BorderLayout.CENTER);

        container.add(wrapper);
        return field;
    }

    private void handleSaveSubmit() {
        String name = nameField.getText().trim();
        String desc = descField.getText().trim();
        String priceText = priceField.getText().trim();
        String stockText = stockField.getText().trim();

        // Basic validation check
        if (name.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all mandatory (*) fields.", "Validation Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Execute background database insertion operation
        boolean saved = productModel.addProduct(name, desc, priceText, stockText);

        if (saved) {
            succeeded = true;
            JOptionPane.showMessageDialog(this, "Product successfully saved to index catalog database!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to insert record. Ensure numbers are formatted accurately.", "Database System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}