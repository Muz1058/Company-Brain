package com.companybrain.database;

import com.companybrain.exception.DatabaseException;
import com.companybrain.util.Config;
import com.companybrain.util.PasswordHasher;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles SQLite database connections and schema initialization.
 */
public class DatabaseManager {

    static {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
    }

    /**
     * Obtains a Connection to the SQLite database and enables foreign key enforcement.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(Config.getDbUrl());
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    /**
     * Initializes the SQLite schema, creating necessary tables, indexes, and default data.
     */
    public static void initializeDatabase() {
        // 1. DDL Table Statements
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password_hash TEXT NOT NULL," +
                "role TEXT NOT NULL CHECK(role IN ('ADMIN', 'EDITOR', 'VIEWER'))" +
                ");";

        String createCategoriesTable = "CREATE TABLE IF NOT EXISTS categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE NOT NULL" +
                ");";

        String createKnowledgeTable = "CREATE TABLE IF NOT EXISTS knowledge_entries (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "description TEXT NOT NULL," +
                "category_id INTEGER NOT NULL," +
                "author_id INTEGER NOT NULL," +
                "created_at TEXT NOT NULL," +
                "updated_at TEXT NOT NULL," +
                "FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE RESTRICT ON UPDATE CASCADE," +
                "FOREIGN KEY(author_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE" +
                ");";

        // 2. DDL Index Statements
        String indexCategory = "CREATE INDEX IF NOT EXISTS idx_entries_category ON knowledge_entries (category_id);";
        String indexAuthor = "CREATE INDEX IF NOT EXISTS idx_entries_author ON knowledge_entries (author_id);";
        String indexUpdated = "CREATE INDEX IF NOT EXISTS idx_entries_updated ON knowledge_entries (updated_at DESC);";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create Tables
            stmt.execute(createUsersTable);
            stmt.execute(createCategoriesTable);
            stmt.execute(createKnowledgeTable);

            // Create Indexes
            stmt.execute(indexCategory);
            stmt.execute(indexAuthor);
            stmt.execute(indexUpdated);

            // Seed default admin user
            String insertAdmin = "INSERT OR IGNORE INTO users (id, username, password_hash, role) " +
                    "VALUES (1, 'admin', '" + PasswordHasher.hash("admin") + "', 'ADMIN');";
            stmt.execute(insertAdmin);

            // Seed default category
            String insertCategory = "INSERT OR IGNORE INTO categories (id, name) VALUES (1, 'General');";
            stmt.execute(insertCategory);

        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize database", e);
        }
    }
}
