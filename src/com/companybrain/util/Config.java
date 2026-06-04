package com.companybrain.util;

import java.io.File;


public class Config {
    private static String dbName = "company_brain.db";

    public static String getDbUrl() {
        return "jdbc:sqlite:" + dbName;
    }

    public static void setDbName(String name) {
        dbName = name;
    }

    public static boolean dbFileExists() {
        File file = new File(dbName);
        return file.exists();
    }
}
