package com.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class InventoryPanel extends JPanel {

    private static final Color COLOR_BG = new Color(245, 247, 250);
    private static final Color COLOR_PRIMARY = new Color(11, 37, 69);
    private static final Color COLOR_TEXT_MAIN = new Color(17, 24, 39);
    private static final Color COLOR_TEXT_MUTED = new Color(107, 114, 128);
    private static final Color COLOR_BORDER = new Color(230, 233, 238);
    private static final Color COLOR_SECONDARY_BTN = new Color(212, 221, 247);

    private ProductModel productModel;
    private ArrayList<Product> products;

    // JTable UI Architecture Components
    private JTable inventoryTable;
    private DefaultTableModel tableModel;

    public InventoryPanel(ProductModel productModel) {
        this.productModel = productModel;
        this.products = new ArrayList<>();

        setLayout(new BorderLayout(0, 20));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(Color.WHITE);
        page.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));

        // --- TOP HEADER SECTION ---
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
        searchField.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
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

        // Titles Row
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Product Inventory Master");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(COLOR_TEXT_MAIN);

        JPanel titleGroup = new JPanel(new GridLayout(1, 1));
        titleGroup.setBackground(Color.WHITE);
        titleGroup.add(titleLabel);
        titleRow.add(titleGroup, BorderLayout.WEST);

        // Add Product Button
        JButton addProductBtn = new JButton("+ ADD PRODUCT");
        addProductBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addProductBtn.setForeground(Color.WHITE);
        addProductBtn.setBackground(COLOR_PRIMARY);
        addProductBtn.setOpaque(true);
        addProductBtn.setContentAreaFilled(true);
        addProductBtn.setBorderPainted(false);
        addProductBtn.setFocusPainted(false);
        addProductBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addProductBtn.setPreferredSize(new Dimension(140, 40));
        
        addProductBtn.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            AddProductDialog dialog = new AddProductDialog(topFrame, this.productModel);
            dialog.setVisible(true);
            
            if (dialog.isSucceeded()) {
                // Instantly call the structural framework pipeline data model refresh method hook
                refreshTable();
            }
        });

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnWrapper.setBackground(Color.WHITE);
        btnWrapper.add(addProductBtn);
        titleRow.add(btnWrapper, BorderLayout.EAST);

        headerWrapper.add(titleRow);
        page.add(headerWrapper, BorderLayout.NORTH);

        // --- MODERN RE-ENGINEERED JTABLE INITIALIZATION ---
        String[] columns = {"ITEM NAME", "CATEGORY", "UNIT PRICE", "STOCK LEVEL"};
        
        // Inline overrides to block manual cell-editing within standard programmatic grid selection grids
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        inventoryTable = new JTable(tableModel);
        inventoryTable.setRowHeight(48); // Spacious modern data density spacing layout
        inventoryTable.setGridColor(COLOR_BORDER);
        inventoryTable.setShowVerticalLines(false); // Clean modern web-style table striping design
        inventoryTable.setShowHorizontalLines(true);
        inventoryTable.setBackground(Color.WHITE);
        inventoryTable.setSelectionBackground(new Color(243, 246, 255));
        inventoryTable.setSelectionForeground(COLOR_TEXT_MAIN);

        // Styling the Native Table Column Group Headers
        JTableHeader tableHeader = inventoryTable.getTableHeader();
        tableHeader.setBackground(Color.WHITE);
        tableHeader.setOpaque(true);
        tableHeader.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_BORDER));
        
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                label.setForeground(COLOR_TEXT_MUTED);
                label.setBackground(Color.WHITE);
                label.setBorder(new EmptyBorder(0, 12, 0, 12));
                return label;
            }
        };
        tableHeader.setDefaultRenderer(headerRenderer);

        // Injecting Render Styles For Dynamic Cells
        configureCustomCellRenderers();

        // Putting the JTable Into a Styled Container Viewport Panel
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(new EmptyBorder(0, 25, 10, 25));
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        page.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomSpacer = new JPanel();
        bottomSpacer.setBackground(Color.WHITE);
        bottomSpacer.setPreferredSize(new Dimension(Integer.MAX_VALUE, 15));
        page.add(bottomSpacer, BorderLayout.SOUTH);

        add(page, BorderLayout.CENTER);

        // Load the database entities safely inside initial view bootstrap initialization pipeline
        refreshTable();
    }

    /**
     * Refreshes the dataset displaying inside the user interface by querying the data layer model context maps.
     */
    public void refreshTable() {
        // Purge current context items
        tableModel.setRowCount(0);
        
        // Fetch fresh instances using your active database tracking collection mapping
        products = productModel.findAllProductsForTable();
        
        if (products != null) {
            for (Product p : products) {
                tableModel.addRow(new Object[]{
                    p.getName(),
                    p.getCategory().toUpperCase(), 
                    String.format("$%.2f", p.getPrice()),
                    p.getStockQuantity() // Passed down as an Integer type to preserve render math evaluation checks below
                });
            }
        }
    }

    /**
     * Assembles render properties dynamically formatting the inner cell items.
     */
    private void configureCustomCellRenderers() {
        // Base Content Column Cells Alignment Styling Hook
        DefaultTableCellRenderer standardCellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBorder(new EmptyBorder(0, 12, 0, 12));
                c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                
                if (column == 0) {
                    c.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    c.setForeground(COLOR_TEXT_MAIN);
                } else if (column == 1) {
                    c.setForeground(COLOR_TEXT_MUTED);
                } else {
                    c.setForeground(COLOR_TEXT_MAIN);
                }
                return c;
            }
        };

        inventoryTable.getColumnModel().getColumn(0).setCellRenderer(standardCellRenderer);
        inventoryTable.getColumnModel().getColumn(1).setCellRenderer(standardCellRenderer);
        inventoryTable.getColumnModel().getColumn(2).setCellRenderer(standardCellRenderer);

        // Custom Visual Conditional Stock Level Column Renderer Cell Panel Matrix Block
        inventoryTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                
                if (value instanceof Integer) {
                    int stock = (Integer) value;
                    label.setText("  STOCK: " + stock + "  ");
                    label.setOpaque(true);
                    
                    if (stock <= 15) {
                        label.setBackground(new Color(254, 226, 226));
                        label.setForeground(new Color(220, 38, 38));
                    } else {
                        label.setBackground(new Color(240, 253, 244));
                        label.setForeground(new Color(22, 101, 52));
                    }
                }
                return label;
            }
        });
    }

    // Retained signature method alias referencing implementation pipeline structure
    public void loadProducts() {
        refreshTable();
    }
}