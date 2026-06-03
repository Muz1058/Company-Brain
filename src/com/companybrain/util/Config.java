package com.companybrain.util;

import java.io.File;

/**
 * Global configuration properties helper.
 */
public class Config {
    private static final String DEFAULT_DB_NAME = "company_brain.db";

    /**
     * Returns the SQLite JDBC Connection URL.
     */
    public static String getDbUrl() {
        return "jdbc:sqlite:" + DEFAULT_DB_NAME;
    }

    /**
     * Checks if the database file exists.
     */
    public static boolean dbFileExists() {
        File file = new File(DEFAULT_DB_NAME);
        return file.exists();
    }
}
