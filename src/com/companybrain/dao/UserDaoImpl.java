package com.companybrain.dao;

import com.companybrain.database.DatabaseManager;
import com.companybrain.exception.DatabaseException;
import com.companybrain.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    private static final String SELECT_ALL =
        "SELECT id, employee_id, username, password_hash, role, is_active, created_at FROM users";

    // ── Queries ───────────────────────────────────────────────────────────────

    @Override
    public User findByUsername(String username) {
        return queryOne(SELECT_ALL + " WHERE username = ?",
                        ps -> ps.setString(1, username));
    }

    @Override
    public User findByEmployeeId(String employeeId) {
        return queryOne(SELECT_ALL + " WHERE employee_id = ?",
                        ps -> ps.setString(1, employeeId));
    }

    @Override
    public User findById(int id) {
        return queryOne(SELECT_ALL + " WHERE id = ?",
                        ps -> ps.setInt(1, id));
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(SELECT_ALL + " ORDER BY id ASC")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving users", e);
        }
        return list;
    }

    @Override
    public List<User> search(String keyword) {
        List<User> list = new ArrayList<>();
        String sql = SELECT_ALL +
            " WHERE username LIKE ? OR employee_id LIKE ? OR role LIKE ? ORDER BY id ASC";
        String p = "%" + keyword + "%";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p); ps.setString(2, p); ps.setString(3, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error searching users", e);
        }
        return list;
    }

    // ── Mutations ─────────────────────────────────────────────────────────────

    @Override
    public void save(User user) {
        String sql =
            "INSERT INTO users (employee_id, username, password_hash, role, is_active) " +
            "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String empId = user.getEmployeeId();
            if (empId == null || empId.isBlank()) ps.setNull(1, Types.VARCHAR);
            else                                   ps.setString(1, empId.trim().toUpperCase());

            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole());
            ps.setInt(5, user.isActive() ? 1 : 0);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) user.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error saving user: " + user.getUsername(), e);
        }
    }

    @Override
    public void updateStatus(int userId, boolean active) {
        execute("UPDATE users SET is_active = ? WHERE id = ?",
                ps -> { ps.setInt(1, active ? 1 : 0); ps.setInt(2, userId); });
    }

    @Override
    public void updatePassword(int userId, String newPasswordHash) {
        execute("UPDATE users SET password_hash = ? WHERE id = ?",
                ps -> { ps.setString(1, newPasswordHash); ps.setInt(2, userId); });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    @FunctionalInterface
    private interface PsSetter { void set(PreparedStatement ps) throws SQLException; }

    private User queryOne(String sql, PsSetter setter) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error querying user", e);
        }
        return null;
    }

    private void execute(String sql, PsSetter setter) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.set(ps);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error executing user update", e);
        }
    }

    private User map(ResultSet rs) throws SQLException {
        String ts = rs.getString("created_at");
        LocalDateTime createdAt = null;
        if (ts != null && !ts.isBlank()) {
            try { createdAt = LocalDateTime.parse(ts.replace(' ', 'T')); }
            catch (Exception ignored) {}
        }
        return new User(
            rs.getInt("id"),
            rs.getString("employee_id"),
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("role"),
            rs.getInt("is_active") == 1,
            createdAt
        );
    }
}