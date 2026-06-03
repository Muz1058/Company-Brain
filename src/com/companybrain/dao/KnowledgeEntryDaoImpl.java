package com.companybrain.dao;

import com.companybrain.database.DatabaseManager;
import com.companybrain.exception.DatabaseException;
import com.companybrain.model.KnowledgeEntry;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of KnowledgeEntryDao interfacing with the SQLite database.
 */
public class KnowledgeEntryDaoImpl implements KnowledgeEntryDao {

    @Override
    public List<KnowledgeEntry> findAll() {
        List<KnowledgeEntry> entries = new ArrayList<>();
        String sql = "SELECT id, title, content, tags, author_id, created_at, updated_at " +
                     "FROM knowledge_entries ORDER BY updated_at DESC";

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
        String sql = "SELECT id, title, content, tags, author_id, created_at, updated_at " +
                     "FROM knowledge_entries WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntry(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding knowledge entry by ID: " + id, e);
        }
        return null;
    }

    @Override
    public void save(KnowledgeEntry entry) {
        String sql = "INSERT INTO knowledge_entries (title, content, tags, author_id, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            entry.setCreatedAt(LocalDateTime.now());
            entry.setUpdatedAt(LocalDateTime.now());

            pstmt.setString(1, entry.getTitle());
            pstmt.setString(2, entry.getContent());
            pstmt.setString(3, entry.getTags());
            pstmt.setInt(4, entry.getAuthorId());
            pstmt.setString(5, entry.getCreatedAt().toString());
            pstmt.setString(6, entry.getUpdatedAt().toString());
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
        String sql = "UPDATE knowledge_entries SET title = ?, content = ?, tags = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            entry.setUpdatedAt(LocalDateTime.now());

            pstmt.setString(1, entry.getTitle());
            pstmt.setString(2, entry.getContent());
            pstmt.setString(3, entry.getTags());
            pstmt.setString(4, entry.getUpdatedAt().toString());
            pstmt.setInt(5, entry.getId());
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
        String sql = "SELECT id, title, content, tags, author_id, created_at, updated_at " +
                     "FROM knowledge_entries WHERE title LIKE ? OR content LIKE ? OR tags LIKE ? " +
                     "ORDER BY updated_at DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String queryParam = "%" + keyword + "%";
            pstmt.setString(1, queryParam);
            pstmt.setString(2, queryParam);
            pstmt.setString(3, queryParam);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapResultSetToEntry(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error searching knowledge entries for keyword: " + keyword, e);
        }
        return entries;
    }

    private KnowledgeEntry mapResultSetToEntry(ResultSet rs) throws SQLException {
        return new KnowledgeEntry(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("tags"),
                rs.getInt("author_id"),
                LocalDateTime.parse(rs.getString("created_at")),
                LocalDateTime.parse(rs.getString("updated_at"))
        );
    }
}
