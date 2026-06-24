package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ProductModel {
    private Connection conn;

    public ProductModel(Connection conn) throws SQLException {
        this.conn = conn;
        String sql = "CREATE TABLE IF NOT EXISTS products ("
                + "id SERIAL PRIMARY KEY,"
                + "name VARCHAR(255) NOT NULL,"
                + "description TEXT,"
                + "price DECIMAL(10, 2) NOT NULL,"
                + "stock_quantity INTEGER NOT NULL DEFAULT 0"
                + ")";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    public boolean addProduct(String name, String description, String priceText, String stockText) {
        String sql = "INSERT INTO products (name, description, price, stock_quantity) VALUES (?, ?, ?, ?)";
        try {
            double price = Double.parseDouble(priceText);
            int stock = Integer.parseInt(stockText);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, description);
                ps.setDouble(3, price);
                ps.setInt(4, stock);
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<Object[]> findAllProductsForTable() {
        ArrayList<Object[]> records = new ArrayList<>();
        String sql = "SELECT id, name, description, price, stock_quantity FROM products ORDER BY id DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                records.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    "$" + String.format("%.2getF", rs.getDouble("price")),
                    rs.getInt("stock_quantity")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
}