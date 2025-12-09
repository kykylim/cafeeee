package catcafe.dao;

import catcafe.model.MenuItem;
import catcafe.util.DbUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDao {

    public List<MenuItem> findAll() throws SQLException {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT * FROM menu_items";
        try (Connection con = DbUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new MenuItem(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getBoolean("available")
                ));
            }
        }
        return list;
    }

    public MenuItem insert(MenuItem m) throws SQLException {
        String sql = "INSERT INTO menu_items (name, category, price, available) VALUES (?, ?, ?, ?)";
        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getCategory());
            ps.setDouble(3, m.getPrice());
            ps.setBoolean(4, m.isAvailable());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) m.setId(keys.getInt(1));
            }
        }
        return m;
    }

    public boolean update(MenuItem m) throws SQLException {
        String sql = "UPDATE menu_items SET name=?, category=?, price=?, available=? WHERE id=?";
        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getCategory());
            ps.setDouble(3, m.getPrice());
            ps.setBoolean(4, m.isAvailable());
            ps.setInt(5, m.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM menu_items WHERE id=?";
        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ✅ NEW METHOD
    public MenuItem findById(int id) throws SQLException {
        String sql = "SELECT id, name, category, price, available FROM menu_items WHERE id=?";
        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new MenuItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getBoolean("available")
                    );
                }
            }
        }
        return null;
    }
}
