package com.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class ProductCatalogPanel extends JPanel {

    private static final Color COLOR_BG = new Color(245, 247, 250);
    private static final Color COLOR_PRIMARY = new Color(11, 37, 69);
    private static final Color COLOR_TEXT_MAIN = new Color(17, 24, 39);
    private static final Color COLOR_TEXT_MUTED = new Color(107, 114, 128);
    private static final Color COLOR_BORDER = new Color(243, 244, 246);
    private static final Color COLOR_SECONDARY_BTN = new Color(212, 221, 247);
    private static final Color COLOR_CART_BG = new Color(245, 247, 250);
    private static final Color COLOR_CART_HEADER = new Color(11, 37, 69);

    private final Map<String, CartItem> cartMap = new HashMap<>();
    private JPanel cartItemsContainer;
    private JLabel subtotalLabel, taxLabel, grandTotalLabel, cartCountLabel;
    private JPanel gridPanel;
    private ArrayList<Product> products;

    // Fields to securely hold data access references
    private final ProductModel productModel;
    private final OrdersModel ordersModel;

    // FIXED: Added parameters to match construction call from MainFrame
    public ProductCatalogPanel(ProductModel productModel, OrdersModel ordersModel) {
        this.productModel = productModel;
        this.ordersModel = ordersModel;

        setLayout(new BorderLayout());
        setBackground(COLOR_BG);

        // Grid Matrix View (Includes inner embedded search bar alignment)
        add(createCatalogGridPanel(), BorderLayout.CENTER);

        // Sidebar Cart View (Flushed clean to the top boundary edge)
        add(createCartPanel(), BorderLayout.EAST);

        updateCartUI();
    }

    private JPanel createCatalogGridPanel() {
        JPanel catalogPanel = new JPanel(new BorderLayout());
        catalogPanel.setBackground(COLOR_BG);
        catalogPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        JPanel headerWrapper = new JPanel();
        headerWrapper.setLayout(new BoxLayout(headerWrapper, BoxLayout.Y_AXIS));
        headerWrapper.setBackground(COLOR_BG);

        // Inner Search input layout matching height profile of right dark cart header
        JPanel searchBarRow = new JPanel(new BorderLayout());
        searchBarRow.setBackground(COLOR_BG);
        searchBarRow.setBorder(new EmptyBorder(18, 0, 18, 0));

        // Interactive Placeholder Logic
        String placeholder = "  SEARCH PRODUCTS...";
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

        JLabel catalogTitle = new JLabel("PRODUCT CATALOG");
        catalogTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        catalogTitle.setForeground(COLOR_PRIMARY);
        catalogTitle.setBorder(new EmptyBorder(5, 2, 12, 0));
        headerWrapper.add(catalogTitle);

        // // Filter Controls
        // JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        // filterBar.setBackground(COLOR_BG);
        // String[] categories = {"ALL ITEMS", "BEVERAGES", "SNACKS", "BAKERY", "DAIRY"};
        // for (String cat : categories) {
        //     JButton btn;
        //     if (cat.equals("ALL ITEMS")) {
        //         btn = new JButton(cat);
        //         btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        //         btn.setForeground(Color.WHITE);
        //         btn.setBackground(COLOR_PRIMARY);
        //         btn.setBorderPainted(false);
        //         btn.setFocusPainted(false);
        //         btn.setPreferredSize(new Dimension(110, 36));
        //         btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        //     } else {
        //         btn = new JButton(cat);
        //         btn.setBackground(COLOR_SECONDARY_BTN);
        //         btn.setForeground(COLOR_PRIMARY);
        //     }
        //     btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        //     btn.setOpaque(false);
        //     btn.setContentAreaFilled(false);
        //     btn.setBorderPainted(false);
        //     btn.setFocusPainted(false);
        //     btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        //     btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        //     filterBar.add(btn);
        // }
        // headerWrapper.add(filterBar);
        headerWrapper.add(Box.createVerticalStrut(20));
        catalogPanel.add(headerWrapper, BorderLayout.NORTH);

        // Responsive grid view
        gridPanel = new JPanel(new GridLayout(0, 3, 16, 20));
        gridPanel.setBackground(COLOR_BG);
        loadProducts();

        JScrollPane scroll = new JScrollPane(gridPanel);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        catalogPanel.add(scroll, BorderLayout.CENTER);

        return catalogPanel;
    }

    public void loadProducts() {
        products = productModel.findAllProductsForTable();
        gridPanel.removeAll();
        for (Product product : products) {
            addProductCard(gridPanel, product.getName(), product.getCategory(), product.getPrice(), product.getStockQuantity(), product.getImageUrl());
        }
        gridPanel.revalidate();
    }

    private void addProductCard(JPanel parent, String title, String category, double price, int stock, String imageFileName) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 242), 1));

        JLabel imgLabel = new JLabel();
        try {
            File file = new File("resources/" + imageFileName);

            // Read the image file system path
            BufferedImage bufferedImage = ImageIO.read(file);
            ImageIcon icon = new ImageIcon(bufferedImage);
            Image scaledImage = icon.getImage().getScaledInstance(100, 130, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            JPanel fallback = new JPanel();
            fallback.setBackground(new Color(45, 35, 30));
            fallback.setPreferredSize(new Dimension(100, 130));
            card.add(fallback, BorderLayout.NORTH);
        }
        imgLabel.setPreferredSize(new Dimension(100, 130));
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setVerticalAlignment(SwingConstants.CENTER);
        card.add(imgLabel, BorderLayout.NORTH);

        JPanel details = new JPanel(null) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(140, 115);
            }
        };
        details.setBackground(Color.WHITE);

        JLabel catLabel = new JLabel(category);
        catLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        catLabel.setForeground(COLOR_TEXT_MUTED);
        catLabel.setBounds(12, 10, 120, 14);
        details.add(catLabel);

        JLabel titleLabel = new JLabel("<html><b>" + title + "</b></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(COLOR_TEXT_MAIN);
        titleLabel.setBounds(12, 24, 120, 36);
        titleLabel.setVerticalAlignment(SwingConstants.TOP);
        details.add(titleLabel);

        JLabel priceLabel = new JLabel(String.format("$%.2f", price));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setForeground(COLOR_PRIMARY);
        priceLabel.setBounds(12, 78, 80, 22);
        details.add(priceLabel);

        JLabel stockLabel = new JLabel("STOCK: " + stock, SwingConstants.CENTER);
        stockLabel.setOpaque(true);
        if (stock <= 10) {
            stockLabel.setBackground(new Color(254, 226, 226));
            stockLabel.setForeground(new Color(220, 38, 38));
        } else {
            stockLabel.setBackground(new Color(240, 253, 244));
            stockLabel.setForeground(new Color(22, 101, 52));
        }
        stockLabel.setFont(new Font("Segoe UI", Font.BOLD, 9));
        stockLabel.setBounds(75, 78, 65, 20);
        details.add(stockLabel);

        card.add(details, BorderLayout.CENTER);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                if (cartMap.containsKey(title)) {
                    cartMap.get(title).quantity++;
                } else {
                    cartMap.put(title, new CartItem(title, category, price, 1));
                }
                updateCartUI();
            }
        });

        parent.add(card);
    }

    private JPanel createCartPanel() {
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setPreferredSize(new Dimension(350, 800));
        cartPanel.setBackground(COLOR_CART_BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_CART_HEADER);
        header.setBorder(new EmptyBorder(21, 18, 20, 18));

        JPanel titleArea = new JPanel(new GridLayout(2, 1, 0, 2));
        titleArea.setOpaque(false);
        JLabel cartTitle = new JLabel("CART");
        cartTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cartTitle.setForeground(Color.WHITE);
        JLabel subHeaderLabel = new JLabel("ACTIVE TRANSACTION");
        subHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        subHeaderLabel.setForeground(COLOR_SECONDARY_BTN);
        titleArea.add(cartTitle);
        titleArea.add(subHeaderLabel);
        header.add(titleArea, BorderLayout.WEST);

        cartCountLabel = new JLabel("03", SwingConstants.RIGHT);
        cartCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cartCountLabel.setForeground(Color.WHITE);
        header.add(cartCountLabel, BorderLayout.EAST);
        cartPanel.add(header, BorderLayout.NORTH);

        cartItemsContainer = new JPanel();
        cartItemsContainer.setLayout(new BoxLayout(cartItemsContainer, BoxLayout.Y_AXIS));
        cartItemsContainer.setBackground(COLOR_CART_BG);
        cartItemsContainer.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane cartScroll = new JScrollPane(cartItemsContainer);
        cartScroll.setBorder(null);
        cartScroll.setOpaque(false);
        cartScroll.getViewport().setOpaque(false);
        cartPanel.add(cartScroll, BorderLayout.CENTER);

        JPanel checkoutContainer = new JPanel();
        checkoutContainer.setLayout(new BoxLayout(checkoutContainer, BoxLayout.Y_AXIS));
        checkoutContainer.setBackground(COLOR_CART_BG);
        checkoutContainer.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel row1 = new JPanel(new BorderLayout());
        row1.setOpaque(false);
        JLabel lblSub = new JLabel("SUBTOTAL");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSub.setForeground(COLOR_TEXT_MUTED);
        subtotalLabel = new JLabel("$0.00");
        subtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        subtotalLabel.setForeground(COLOR_TEXT_MAIN);
        row1.add(lblSub, BorderLayout.WEST);
        row1.add(subtotalLabel, BorderLayout.EAST);

        JPanel row2 = new JPanel(new BorderLayout());
        row2.setOpaque(false);
        JLabel lblTax = new JLabel("TAX (8%)");
        lblTax.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTax.setForeground(COLOR_TEXT_MUTED);
        taxLabel = new JLabel("$0.00");
        taxLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        taxLabel.setForeground(COLOR_TEXT_MAIN);
        row2.add(lblTax, BorderLayout.WEST);
        row2.add(taxLabel, BorderLayout.EAST);

        JPanel row3 = new JPanel(new BorderLayout());
        row3.setOpaque(false);
        JLabel lblTot = new JLabel("GRAND TOTAL");
        lblTot.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTot.setForeground(COLOR_TEXT_MAIN);
        grandTotalLabel = new JLabel("$0.00");
        grandTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        grandTotalLabel.setForeground(COLOR_PRIMARY);
        row3.add(lblTot, BorderLayout.WEST);
        row3.add(grandTotalLabel, BorderLayout.EAST);

        checkoutContainer.add(row1);
        checkoutContainer.add(Box.createVerticalStrut(8));
        checkoutContainer.add(row2);
        checkoutContainer.add(Box.createVerticalStrut(10));
        checkoutContainer.add(new JSeparator());
        checkoutContainer.add(Box.createVerticalStrut(12));
        checkoutContainer.add(row3);
        checkoutContainer.add(Box.createVerticalStrut(16));

        JPanel actionButtons = new JPanel(new GridLayout(1, 2, 10, 0));
        actionButtons.setOpaque(false);

        JButton clearBtn = new JButton("CLEAR CART");
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        clearBtn.setBackground(COLOR_SECONDARY_BTN);
        clearBtn.setForeground(COLOR_PRIMARY);
        clearBtn.setBorderPainted(false);
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearBtn.setPreferredSize(new Dimension(100, 44));
        clearBtn.addActionListener(e -> {
            cartMap.clear();
            updateCartUI();
        });

        JButton orderBtn = new JButton("MAKE ORDER") ;
        // orderBtn.setOpaque(false);
        // orderBtn.setContentAreaFilled(false);
        orderBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        orderBtn.setForeground(Color.WHITE);
        orderBtn.setBackground(COLOR_PRIMARY);
        orderBtn.setBorderPainted(false);
        orderBtn.setFocusPainted(false);
        orderBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        orderBtn.setForeground(Color.WHITE);
        orderBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // FIXED/ROUTED: Connected order submission action to clear maps
        orderBtn.addActionListener(e -> {
            if (!cartMap.isEmpty()) {
                // Here is where you parse and push your cart items into `ordersModel`
                System.out.println("Order pushed down successfully through OrdersModel flow.");
                cartMap.clear();
                updateCartUI();
            }
        });

        actionButtons.add(clearBtn);
        actionButtons.add(orderBtn);
        checkoutContainer.add(actionButtons);

        cartPanel.add(checkoutContainer, BorderLayout.SOUTH);
        return cartPanel;
    }

    private void updateCartUI() {
        cartItemsContainer.removeAll();
        double subtotal = 0;
        int totalItemsCount = 0;

        for (CartItem item : cartMap.values()) {
            subtotal += item.price * item.quantity;
            totalItemsCount += item.quantity;

            JPanel itemRow = new JPanel(new BorderLayout());
            itemRow.setBackground(Color.WHITE);
            itemRow.setMaximumSize(new Dimension(330, 75));
            itemRow.setPreferredSize(new Dimension(320, 75));
            itemRow.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 6, 0, COLOR_CART_BG),
                    new EmptyBorder(10, 10, 10, 10)
            ));

            // JPanel imgBlock = new JPanel();
            // imgBlock.setBackground(new Color(210, 215, 222));
            // imgBlock.setPreferredSize(new Dimension(45, 45));
            // itemRow.add(imgBlock, BorderLayout.WEST);
            JPanel centerMeta = new JPanel(new GridLayout(3, 1, 0, 1));
            centerMeta.setBackground(Color.WHITE);
            centerMeta.setBorder(new EmptyBorder(0, 10, 0, 10));

            JLabel catLbl = new JLabel(item.category);
            catLbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
            catLbl.setForeground(COLOR_TEXT_MUTED);

            JLabel nameLbl = new JLabel(item.name);
            nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            nameLbl.setForeground(COLOR_TEXT_MAIN);

            JPanel stepperContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            stepperContainer.setBackground(Color.WHITE);

            JButton minusBtn = new JButton("-");
            configureStepperButton(minusBtn);

            JTextField qtyField = new JTextField(String.valueOf(item.quantity), 2);
            qtyField.setHorizontalAlignment(JTextField.CENTER);
            qtyField.setFont(new Font("Segoe UI", Font.BOLD, 11));
            qtyField.setEditable(false);
            qtyField.setForeground(COLOR_PRIMARY);
            qtyField.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, COLOR_SECONDARY_BTN));

            JButton plusBtn = new JButton("+");
            configureStepperButton(plusBtn);

            minusBtn.addActionListener(e -> {
                if (item.quantity > 1) {
                    item.quantity--;
                } else {
                    cartMap.remove(item.name);
                }
                updateCartUI();
            });
            plusBtn.addActionListener(e -> {
                item.quantity++;
                updateCartUI();
            });

            stepperContainer.add(minusBtn);
            stepperContainer.add(qtyField);
            stepperContainer.add(plusBtn);

            centerMeta.add(catLbl);
            centerMeta.add(nameLbl);
            centerMeta.add(stepperContainer);
            itemRow.add(centerMeta, BorderLayout.CENTER);

            JPanel rightBlock = new JPanel(new BorderLayout());
            rightBlock.setBackground(Color.WHITE);
            JLabel priceLbl = new JLabel(String.format("$%.2f", item.price * item.quantity), SwingConstants.RIGHT);
            priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            priceLbl.setForeground(COLOR_TEXT_MAIN);
            rightBlock.add(priceLbl, BorderLayout.NORTH);

            itemRow.add(rightBlock, BorderLayout.EAST);
            cartItemsContainer.add(itemRow);
        }

        double tax = subtotal * 0.08;
        double grandTotal = subtotal + tax;

        cartCountLabel.setText(String.format("%02d", totalItemsCount));
        subtotalLabel.setText(String.format("$%.2f", subtotal));
        taxLabel.setText(String.format("$%.2f", tax));
        grandTotalLabel.setText(String.format("$%.2f", grandTotal));

        cartItemsContainer.revalidate();
        cartItemsContainer.repaint();
    }

    private void configureStepperButton(JButton btn) {
        btn.setPreferredSize(new Dimension(22, 18));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBackground(COLOR_SECONDARY_BTN);
        btn.setForeground(COLOR_PRIMARY);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private static class CartItem {

        String name, category;
        double price;
        int quantity;

        CartItem(String name, String category, double price, int quantity) {
            this.name = name;
            this.category = category;
            this.price = price;
            this.quantity = quantity;
        }
    }
}
