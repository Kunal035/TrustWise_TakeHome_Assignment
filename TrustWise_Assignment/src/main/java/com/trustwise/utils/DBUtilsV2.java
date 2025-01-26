package com.trustwise.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtilsV2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBUtilsV2.class);
    private static final String CONFIG_FILE = "src/main/java/config.properties";
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        loadConfig();
    }

    private DBUtilsV2() {
        // Private constructor to prevent instantiation
    }

    private static void loadConfig() {
        try (FileInputStream input = new FileInputStream(CONFIG_FILE)) {
            Properties prop = new Properties();
            prop.load(input);
            URL = prop.getProperty("url");
            USER = prop.getProperty("user");
            PASSWORD = prop.getProperty("password");
        } catch (IOException e) {
            LOGGER.error("Error loading database configuration from file: {}", CONFIG_FILE, e);
            throw new RuntimeException("Error loading database configuration", e);
        }
    }

    /**
     * Establishes a connection to the database.
     *
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + URL, USER, PASSWORD);
        } catch (SQLException e) {
            LOGGER.error("Error establishing database connection", e);
            throw e;
        }
    }
}