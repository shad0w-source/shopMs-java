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
                + "category VARCHAR(255) NOT NULL,"
                + "price DECIMAL(10, 2) NOT NULL,"
                + "stock_quantity INTEGER NOT NULL DEFAULT 0,"
                + "image_url VARCHAR(255)"
                + ")";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    public boolean addProduct(String name, String category, String priceText, String stockText, String uploadedFileName) {
        String sql = "INSERT INTO products (name, category, price, stock_quantity, image_url) VALUES (?, ?, ?, ?, ?)";
        try {
            double price = Double.parseDouble(priceText);
            int stock = Integer.parseInt(stockText);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, category);
                ps.setDouble(3, price);
                ps.setInt(4, stock);
                ps.setString(5, uploadedFileName);
                boolean result = ps.executeUpdate() > 0;
                if (result) {
                    ProductEventBus.notifyProductChanged();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<Product> findAllProductsForTable() {
        ArrayList<Product> records = new ArrayList<>();
        String sql = "SELECT id, name, category, price, stock_quantity, image_url"
                + " FROM products ORDER BY id DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                records.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("stock_quantity"),
                        rs.getString("image_url")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
}
