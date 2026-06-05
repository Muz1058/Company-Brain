package com.companybrain.database;

import com.companybrain.exception.DatabaseException;
import com.companybrain.util.Config;
import com.companybrain.util.PasswordHasher;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(Config.getDbUrl());
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static void initializeDatabase() {
        String createUsersTable =
            "CREATE TABLE IF NOT EXISTS users (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  username TEXT UNIQUE NOT NULL," +
            "  password_hash TEXT NOT NULL," +
            "  role TEXT NOT NULL CHECK(role IN ('ADMIN', 'EDITOR', 'VIEWER'))" +
            ");";

        String createCategoriesTable =
            "CREATE TABLE IF NOT EXISTS categories (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  name TEXT UNIQUE NOT NULL" +
            ");";

        // entry_type: 'TEXT' or 'FILE'
        // file_path: absolute path to uploaded file, NULL for TEXT entries
        String createKnowledgeTable =
            "CREATE TABLE IF NOT EXISTS knowledge_entries (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  title TEXT NOT NULL," +
            "  description TEXT NOT NULL," +
            "  category_id INTEGER NOT NULL," +
            "  author_id INTEGER NOT NULL," +
            "  created_at TEXT NOT NULL," +
            "  updated_at TEXT NOT NULL," +
            "  entry_type TEXT NOT NULL DEFAULT 'TEXT' CHECK(entry_type IN ('TEXT','FILE'))," +
            "  file_path TEXT," +
            "  FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE RESTRICT ON UPDATE CASCADE," +
            "  FOREIGN KEY(author_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE" +
            ");";

        String indexCategory = "CREATE INDEX IF NOT EXISTS idx_entries_category ON knowledge_entries (category_id);";
        String indexAuthor   = "CREATE INDEX IF NOT EXISTS idx_entries_author   ON knowledge_entries (author_id);";
        String indexUpdated  = "CREATE INDEX IF NOT EXISTS idx_entries_updated  ON knowledge_entries (updated_at DESC);";

        String insertAdmin    = "INSERT OR IGNORE INTO users (id, username, password_hash, role) " +
                                "VALUES (1, 'admin', '" + PasswordHasher.hash("admin") + "', 'ADMIN');";
        String insertCategory = "INSERT OR IGNORE INTO categories (id, name) VALUES (1, 'General');";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createCategoriesTable);
            stmt.execute(createKnowledgeTable);
            stmt.execute(indexCategory);
            stmt.execute(indexAuthor);
            stmt.execute(indexUpdated);
            stmt.execute(insertAdmin);
            stmt.execute(insertCategory);

            // Safe migration: add new columns to existing databases without failing
            migrateAddColumnIfMissing(stmt, "knowledge_entries", "entry_type",
                    "TEXT NOT NULL DEFAULT 'TEXT' CHECK(entry_type IN ('TEXT','FILE'))");
            migrateAddColumnIfMissing(stmt, "knowledge_entries", "file_path", "TEXT");

        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize database", e);
        }
    }

    /**
     * Attempts to add a column to a table. Silently ignores the error if the
     * column already exists (SQLite returns "duplicate column name" in that case).
     */
    private static void migrateAddColumnIfMissing(Statement stmt, String table,
                                                   String column, String definition) {
        try {
            stmt.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition + ";");
        } catch (SQLException ignored) {
            // Column already exists — this is expected on second+ run
        }
    }
}
