package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class OrdersModel {
    private Connection conn;

    public OrdersModel(Connection conn) throws SQLException {
        this.conn = conn;
        
        // 1. Create Main Orders Tracking Architecture Table
        String ordersSql = "CREATE TABLE IF NOT EXISTS orders ("
                + "id SERIAL PRIMARY KEY,"
                + "customer_name VARCHAR(255) NOT NULL,"
                + "order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00"
                + ")";
                
        // 2. Create Dynamic Sub-Item Relational Mapping Table
        String orderItemsSql = "CREATE TABLE IF NOT EXISTS orders_items ("
                + "id SERIAL PRIMARY KEY,"
                + "order_id INTEGER NOT NULL,"
                + "product_id INTEGER NOT NULL,"
                + "quantity INTEGER NOT NULL,"
                + "FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,"
                + "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT"
                + ")";

        try (PreparedStatement ps1 = conn.prepareStatement(ordersSql);
             PreparedStatement ps2 = conn.prepareStatement(orderItemsSql)) {
            ps1.executeUpdate();
            ps2.executeUpdate();
        }
    }

    private boolean productHasStock(int productId, int requestedQty) throws SQLException {
        String sql = "SELECT stock_quantity FROM products WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stock_quantity") >= requestedQty;
                }
            }
        }
        return false;
    }

    public boolean placeOrder(String customerName, ArrayList<int[]> cartItems, double totalAmount) {
        String insertOrderSql = "INSERT INTO orders (customer_name, total_amount) VALUES (?, ?)";
        String insertItemSql = "INSERT INTO orders_items (order_id, product_id, quantity) VALUES (?, ?, ?)";
        String updateStockSql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?";

        try {
            conn.setAutoCommit(false);

            // Step 1: Pre-verify inventory availability metrics across whole processing matrix
            for (int[] item : cartItems) {
                int productId = item[0];
                int quantity = item[1];
                if (!productHasStock(productId, quantity)) {
                    conn.rollback();
                    return false; 
                }
            }

            // Step 2: Write Main Order and obtain generated sequence tracking primary key
            int orderId = -1;
            try (PreparedStatement psOrder = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setString(1, customerName);
                psOrder.setDouble(2, totalAmount);
                psOrder.executeUpdate();
                
                try (ResultSet generatedKeys = psOrder.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);
                    }
                }
            }

            if (orderId == -1) {
                conn.rollback();
                return false;
            }

            // Step 3: Loop items array payload stack to insert connections and drain stock values securely
            try (PreparedStatement psItem = conn.prepareStatement(insertItemSql);
                 PreparedStatement psStock = conn.prepareStatement(updateStockSql)) {
                
                for (int[] item : cartItems) {
                    int productId = item[0];
                    int quantity = item[1];

                    // Insert relational structural rows 
                    psItem.setInt(1, orderId);
                    psItem.setInt(2, productId);
                    psItem.setInt(3, quantity);
                    psItem.executeUpdate();

                    // Safely update product row stock metrics
                    psStock.setInt(1, quantity);
                    psStock.setInt(2, productId);
                    psStock.setInt(3, quantity);
                    int updatedRows = psStock.executeUpdate();
                    
                    if (updatedRows == 0) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            try { conn.rollback(); } catch (SQLException se) { se.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    public ArrayList<Object[]> findAllOrdersForDisplayTable() {
        ArrayList<Object[]> records = new ArrayList<>();
        String sql = "SELECT id, customer_name, order_date, total_amount FROM orders ORDER BY id DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                records.add(new Object[]{
                    "#ORD-" + rs.getInt("id"),
                    rs.getString("customer_name"),
                    rs.getTimestamp("order_date").toString(),
                    "$" + String.format("%.2f", rs.getDouble("total_amount"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
}