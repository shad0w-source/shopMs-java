package com.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class AddProductDialog extends JDialog {

    private static final Color COLOR_PRIMARY = new Color(11, 37, 69);
    private static final Color COLOR_TEXT_MAIN = new Color(17, 24, 39);
    private static final Color COLOR_TEXT_MUTED = new Color(107, 114, 128);
    private static final Color COLOR_BORDER = new Color(230, 233, 238);
    private static final Color COLOR_CANCEL_BG = new Color(243, 244, 246);

    private JTextField nameField;
    private JTextField categoryField;
    private JTextField priceField;
    private JTextField stockField;
    
    // Kept track of the uploaded file's name here
    private String uploadedFileName = ""; 
    private boolean succeeded = false;

    private ProductModel productModel;
    private JLabel statusLabel;

    public AddProductDialog(JFrame parent, ProductModel productModel) {
        super(parent, "Add New Product", true);
        this.productModel = productModel;

        // Increased height slightly to 540 to comfortably fit the upload element
        setSize(420, 540); 
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

        // Input Fields Grid Panel (Changed row count to 5 to accommodate the upload row)
        JPanel formPanel = new JPanel(new GridLayout(5, 1, 0, 16));
        formPanel.setBackground(Color.WHITE);

        nameField = createFormField(formPanel, "Product Name *");
        categoryField = createFormField(formPanel, "category");
        priceField = createFormField(formPanel, "Unit Price ($) *");
        stockField = createFormField(formPanel, "Initial Stock Quantity *");
        
        // Appended the upload row element to the form matrix
        createUploadField(formPanel, "Product Image/File File");

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Interactive Footer Button Controls
        // Using a wrapper panel to contain both status label and final actions cleanly
        JPanel southWrapper = new JPanel(new BorderLayout());
        southWrapper.setBackground(Color.WHITE);

        statusLabel = new JLabel("No file selected.");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setForeground(COLOR_TEXT_MUTED);
        statusLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        southWrapper.add(statusLabel, BorderLayout.NORTH);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

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
        JButton saveBtn = new JButton("Save Record");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setBackground(COLOR_PRIMARY);
        // saveBtn.setOpaque(false);
        // saveBtn.setContentAreaFilled(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setFocusPainted(false);
        saveBtn.setPreferredSize(new Dimension(110, 36));
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> handleSaveSubmit());
        footerPanel.add(saveBtn);

        southWrapper.add(footerPanel, BorderLayout.SOUTH);
        mainPanel.add(southWrapper, BorderLayout.SOUTH);
        add(mainPanel);
    }

    public void createUploadField(JPanel container, String labelText) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(COLOR_TEXT_MAIN);
        wrapper.add(label, BorderLayout.NORTH);

        JButton uploadBtn = new JButton("Select & Upload File");
        uploadBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        uploadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        uploadBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        
        // Attached Action Listener to open file dialog picker
        uploadBtn.addActionListener(e -> handleFileUpload());
        wrapper.add(uploadBtn, BorderLayout.CENTER);

        container.add(wrapper);
    }

    private File handleFileUpload() {
        JFileChooser fileChooser = new JFileChooser();

        // Open the dialog window
        int response = fileChooser.showOpenDialog(this);

        if (response == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            statusLabel.setText("Uploading: " + selectedFile.getName());

            // Define where you want to save/upload the file
            File destinationDir = new File("./resources");
            if (!destinationDir.exists()) {
                destinationDir.mkdir();
            }

            File destFile = new File(destinationDir, selectedFile.getName());

            // Run file operations in a background thread so the GUI does not freeze
            new Thread(() -> {
                try {
                    copyFile(selectedFile, destFile);
                    SwingUtilities.invokeLater(() -> {
                        // Successfully copied target file, track local filename state string
                        uploadedFileName = destFile.getName();
                        statusLabel.setText("Upload successful: " + uploadedFileName);
                    });
                } catch (IOException ex) {
                    SwingUtilities.invokeLater(()
                            -> statusLabel.setText("Upload failed: " + ex.getMessage())
                    );
                }
            }).start();

            return destFile;
        } else {
            statusLabel.setText("Upload canceled by user.");
        }
        return null;
    }

    private void copyFile(File source, File dest) throws IOException {
        try (FileInputStream fis = new FileInputStream(source); 
             FileOutputStream fos = new FileOutputStream(dest)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
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
        String category = categoryField.getText().trim();
        String priceText = priceField.getText().trim();
        String stockText = stockField.getText().trim();

        // Basic validation check
        if (name.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all mandatory (*) fields.", "Validation Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println(uploadedFileName);
        // Pass along uploadedFileName state down to your model integration logic layer block
        boolean saved = productModel.addProduct(name, category, priceText, stockText, uploadedFileName);
        dispose();
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