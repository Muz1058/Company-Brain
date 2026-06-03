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
     * Obtains a Connection to the SQLite database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(Config.getDbUrl());
    }

    /**
     * Initializes the SQLite schema, creating necessary tables and default data.
     */
    public static void initializeDatabase() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password_hash TEXT NOT NULL," +
                "role TEXT NOT NULL" +
                ");";

        String createKnowledgeTable = "CREATE TABLE IF NOT EXISTS knowledge_entries (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "content TEXT NOT NULL," +
                "tags TEXT," +
                "author_id INTEGER," +
                "created_at TEXT NOT NULL," +
                "updated_at TEXT NOT NULL," +
                "FOREIGN KEY(author_id) REFERENCES users(id)" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create tables
            stmt.execute(createUsersTable);
            stmt.execute(createKnowledgeTable);

            // Insert default admin user if not exists
            String insertAdmin = "INSERT OR IGNORE INTO users (id, username, password_hash, role) " +
                    "VALUES (1, 'admin', '" + PasswordHasher.hash("admin") + "', 'ADMIN');";
            stmt.execute(insertAdmin);

        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize database", e);
        }
    }
}
