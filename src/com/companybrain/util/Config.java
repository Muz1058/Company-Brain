package com.companybrain.util;

import java.io.File;

public class Config {

    private static final String APP_FOLDER = "CompanyBrain";
    private static String dbName = "company_brain.db";

    public static String getDbUrl() {
        String appDataPath = System.getenv("APPDATA") + File.separator + APP_FOLDER;
        File directory = new File(appDataPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String dbPath = appDataPath + File.separator + dbName;
        return "jdbc:sqlite:" + dbPath;
    }

    public static void setDbName(String name) {
        dbName = name;
    }

    public static boolean dbFileExists() {
        String appDataPath = System.getenv("APPDATA") + File.separator + APP_FOLDER;
        String dbPath = appDataPath + File.separator + dbName;
        return new File(dbPath).exists();
    }

    // ── ADD THIS METHOD ───────────────────────────────────────────────────────
    public static String resolveDbPath() {
        String appDataPath = System.getenv("APPDATA") + File.separator + APP_FOLDER;
        return appDataPath + File.separator + dbName;
    }
}