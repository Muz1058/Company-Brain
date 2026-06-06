package com.companybrain.database;

import com.companybrain.exception.DatabaseException;
import com.companybrain.util.Config;
import com.companybrain.util.PasswordHasher;

import java.sql.*;

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
        try (Statement s = conn.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static void initializeDatabase() {

        // ── users ─────────────────────────────────────────────────────────────
        // Roles changed: ADMIN | MANAGER | EMPLOYEE  (was ADMIN | EDITOR | VIEWER)
        // New columns: employee_id (nullable, unique), is_active, created_at
        String ddlUsers =
            "CREATE TABLE IF NOT EXISTS users (" +
            "  id            INTEGER  PRIMARY KEY AUTOINCREMENT," +
            "  employee_id   TEXT     UNIQUE," +
            "  username      TEXT     UNIQUE NOT NULL," +
            "  password_hash TEXT     NOT NULL," +
            "  role          TEXT     NOT NULL CHECK(role IN ('ADMIN','MANAGER','EMPLOYEE'))," +
            "  is_active     INTEGER  NOT NULL DEFAULT 1," +
            "  created_at    DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ");";

        String ddlCategories =
            "CREATE TABLE IF NOT EXISTS categories (" +
            "  id   INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  name TEXT    UNIQUE NOT NULL" +
            ");";

        String ddlEntries =
            "CREATE TABLE IF NOT EXISTS knowledge_entries (" +
            "  id          INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  title       TEXT    NOT NULL," +
            "  description TEXT    NOT NULL," +
            "  category_id INTEGER NOT NULL," +
            "  author_id   INTEGER NOT NULL," +
            "  created_at  TEXT    NOT NULL," +
            "  updated_at  TEXT    NOT NULL," +
            "  entry_type  TEXT    NOT NULL DEFAULT 'TEXT' CHECK(entry_type IN ('TEXT','FILE'))," +
            "  file_path   TEXT," +
            "  FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE RESTRICT ON UPDATE CASCADE," +
            "  FOREIGN KEY(author_id)   REFERENCES users(id)      ON DELETE RESTRICT ON UPDATE CASCADE" +
            ");";

        String[] indexes = {
            "CREATE INDEX IF NOT EXISTS idx_entries_category ON knowledge_entries(category_id);",
            "CREATE INDEX IF NOT EXISTS idx_entries_author   ON knowledge_entries(author_id);",
            "CREATE INDEX IF NOT EXISTS idx_entries_updated  ON knowledge_entries(updated_at DESC);",
            "CREATE INDEX IF NOT EXISTS idx_users_employee   ON users(employee_id);"
        };

        // Default admin: password = admin123, employee_id = NULL
        String seedAdmin =
            "INSERT OR IGNORE INTO users (id, employee_id, username, password_hash, role, is_active) " +
            "VALUES (1, NULL, 'admin', '" + PasswordHasher.hash("admin123") + "', 'ADMIN', 1);";

        String seedCategory =
            "INSERT OR IGNORE INTO categories (id, name) VALUES (1, 'General');";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(ddlUsers);
            stmt.execute(ddlCategories);
            stmt.execute(ddlEntries);
            for (String idx : indexes) stmt.execute(idx);
            stmt.execute(seedAdmin);
            stmt.execute(seedCategory);

            // ── Safe migrations for existing databases ────────────────────────
            // These silently no-op if the column already exists
            addColumnIfMissing(stmt, "users", "employee_id", "TEXT");
            addColumnIfMissing(stmt, "users", "is_active",   "INTEGER NOT NULL DEFAULT 1");
            addColumnIfMissing(stmt, "users", "created_at",  "DATETIME DEFAULT CURRENT_TIMESTAMP");
            addColumnIfMissing(stmt, "knowledge_entries", "entry_type",
                "TEXT NOT NULL DEFAULT 'TEXT' CHECK(entry_type IN ('TEXT','FILE'))");
            addColumnIfMissing(stmt, "knowledge_entries", "file_path", "TEXT");

        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize database", e);
        }
    }

    /** Silently ignores "duplicate column name" — column already exists. */
    private static void addColumnIfMissing(Statement stmt, String table,
                                           String col, String definition) {
        try {
            stmt.execute("ALTER TABLE " + table + " ADD COLUMN " + col + " " + definition + ";");
        } catch (SQLException ignored) { /* already exists */ }
    }
}