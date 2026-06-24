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
import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {

    private static final Color COLOR_SIDEBAR_BG = new Color(30, 41, 59);        
    private static final Color COLOR_CONTENT_BG = new Color(241, 245, 249);     
    private static final Color COLOR_PRIMARY_NAV = new Color(37, 99, 235);      
    private static final Color COLOR_TEXT_MUTED = new Color(148, 163, 184);     

    private CardLayout cardLayout;
    private JPanel routingPanel;
    private JPanel[] navButtons;
    private final String[] panelNames = {"Sales", "Inventory", "Orders"};

    private Connection conn;
    private ProductModel productModel;
    private OrdersModel ordersModel;

    public MainFrame() {
        setTitle("STOCK_PRO Engine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 880);
        setLocationRelativeTo(null);

        try {
            String url = "jdbc:postgresql://localhost:5432/shopms";
            String user = "postgres";
            String password = "123456";
            
            this.conn = DriverManager.getConnection(url, user, password);
            this.productModel = new ProductModel(conn);
            this.ordersModel = new OrdersModel(conn);
            System.out.println("Database tables successfully synchronized!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Database initialization crash! Model layer is running in fallback mock status.");
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_CONTENT_BG);

        mainPanel.add(createSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        routingPanel = new JPanel(cardLayout);
        routingPanel.setBackground(COLOR_CONTENT_BG);
        
        // This line now compiles perfectly!
        routingPanel.add(new ProductCatalogPanel(productModel, ordersModel), "Sales");
        
        routingPanel.add(new InventoryPanel(productModel), "Inventory");
        routingPanel.add(new OrdersPanel(ordersModel), "Orders"); 

        mainPanel.add(routingPanel, BorderLayout.CENTER);
        add(mainPanel);

        setTabActive(0);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, 850));
        sidebar.setBackground(COLOR_SIDEBAR_BG);
        sidebar.setBorder(new EmptyBorder(30, 0, 30, 0));

        JLabel logo = new JLabel("STOCK_PRO");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setBorder(new EmptyBorder(0, 24, 0, 24));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logo);

        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

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

            navItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    setTabActive(index);
                    cardLayout.show(routingPanel, panelNames[index]);
                }
            });

            navButtons[i] = navItem;
            sidebar.add(navItem);
        }

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            mainFrame.requestFocusInWindow();
        });
    }
}