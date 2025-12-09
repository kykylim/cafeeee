/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package catcafe.dao;


import catcafe.model.Order;
import catcafe.util.DbUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {

    public Order insertPending(int customerId) throws SQLException {
        String sql = "INSERT INTO orders (customer_id, status, total) VALUES (?, 'Pending', 0.00)";
        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, customerId);
            ps.executeUpdate();
            Order o = new Order();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setId(keys.getInt(1));
            }
            o.setCustomerId(customerId);
            o.setStatus("Pending");
            o.setTotal(0.0);
            o.setOrderDate(LocalDateTime.now());
            return o;
        }
    }

    public boolean updateStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean updateTotal(int orderId, double total) throws SQLException {
        String sql = "UPDATE orders SET total = ? WHERE id = ?";
        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, total);
            ps.setInt(2, orderId);
            return ps.executeUpdate() == 1;
        }
    }

    public List<Order> findByCustomer(int customerId) throws SQLException {
        String sql = "SELECT id, customer_id, order_date, status, total FROM orders WHERE customer_id = ? ORDER BY order_date DESC";
        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Order> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new Order(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getTimestamp("order_date").toLocalDateTime(),
                        rs.getString("status"),
                        rs.getDouble("total")
                    ));
                }
                return list;
            }
        }
    }
}

