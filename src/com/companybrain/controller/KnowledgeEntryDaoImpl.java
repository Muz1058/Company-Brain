package com.companybrain.controller;

import com.companybrain.dao.KnowledgeEntryDao;
import com.companybrain.database.DatabaseManager;
import com.companybrain.exception.DatabaseException;
import com.companybrain.model.KnowledgeEntry;
import com.companybrain.model.KnowledgeEntry.EntryType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class KnowledgeEntryDaoImpl implements KnowledgeEntryDao {

    private static final String SELECT_COLS =
        "e.id, e.title, e.description, e.category_id, e.author_id, " +
        "e.created_at, e.updated_at, e.entry_type, e.file_path ";

    @Override
    public List<KnowledgeEntry> findAll() {
        List<KnowledgeEntry> entries = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLS +
                     "FROM knowledge_entries e ORDER BY e.updated_at DESC";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                entries.add(mapResultSetToEntry(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving all knowledge entries", e);
        }
        return entries;
    }

    @Override
    public KnowledgeEntry findById(int id) {
        String sql = "SELECT " + SELECT_COLS +
                     "FROM knowledge_entries e WHERE e.id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToEntry(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding knowledge entry by ID: " + id, e);
        }
        return null;
    }

    @Override
    public void save(KnowledgeEntry entry) {
        String sql =
            "INSERT INTO knowledge_entries " +
            "(title, description, category_id, author_id, created_at, updated_at, entry_type, file_path) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            entry.setCreatedAt(LocalDateTime.now());
            entry.setUpdatedAt(LocalDateTime.now());

            pstmt.setString(1, entry.getTitle());
            pstmt.setString(2, entry.getDescription());
            pstmt.setInt(3, entry.getCategoryId());
            pstmt.setInt(4, entry.getAuthorId());
            pstmt.setString(5, entry.getCreatedAt().toString());
            pstmt.setString(6, entry.getUpdatedAt().toString());
            pstmt.setString(7, entry.getEntryType().name());
            pstmt.setString(8, entry.getFilePath());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entry.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error saving knowledge entry: " + entry.getTitle(), e);
        }
    }

    @Override
    public void update(KnowledgeEntry entry) {
        String sql =
            "UPDATE knowledge_entries " +
            "SET title = ?, description = ?, category_id = ?, updated_at = ?, entry_type = ?, file_path = ? " +
            "WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            entry.setUpdatedAt(LocalDateTime.now());

            pstmt.setString(1, entry.getTitle());
            pstmt.setString(2, entry.getDescription());
            pstmt.setInt(3, entry.getCategoryId());
            pstmt.setString(4, entry.getUpdatedAt().toString());
            pstmt.setString(5, entry.getEntryType().name());
            pstmt.setString(6, entry.getFilePath());
            pstmt.setInt(7, entry.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating knowledge entry ID: " + entry.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM knowledge_entries WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting knowledge entry ID: " + id, e);
        }
    }

    @Override
    public List<KnowledgeEntry> search(String keyword) {
        List<KnowledgeEntry> entries = new ArrayList<>();
        String sql =
            "SELECT " + SELECT_COLS +
            "FROM knowledge_entries e JOIN categories c ON e.category_id = c.id " +
            "WHERE e.title LIKE ? OR c.name LIKE ? " +
            "ORDER BY e.updated_at DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String param = "%" + keyword + "%";
            pstmt.setString(1, param);
            pstmt.setString(2, param);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapResultSetToEntry(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error searching knowledge entries for: " + keyword, e);
        }
        return entries;
    }

    private KnowledgeEntry mapResultSetToEntry(ResultSet rs) throws SQLException {
        String typeStr = rs.getString("entry_type");
        EntryType entryType = EntryType.TEXT;
        if (typeStr != null) {
            try {
                entryType = EntryType.valueOf(typeStr);
            } catch (IllegalArgumentException ignored) {
                // Fallback to TEXT for unknown values
            }
        }

        return new KnowledgeEntry(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getInt("category_id"),
            rs.getInt("author_id"),
            LocalDateTime.parse(rs.getString("created_at")),
            LocalDateTime.parse(rs.getString("updated_at")),
            entryType,
            rs.getString("file_path")
        );
    }
}
