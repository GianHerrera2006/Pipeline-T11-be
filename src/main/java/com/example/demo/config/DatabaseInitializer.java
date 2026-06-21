package com.example.demo.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final int MAX_CONNECTION_ATTEMPTS = 10;
    private static final long CONNECTION_RETRY_DELAY_MS = 5000;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Environment env = applicationContext.getEnvironment();
        String databaseUrl = env.getProperty("spring.datasource.url");
        String username = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");

        if (databaseUrl == null || username == null || password == null) {
            return;
        }

        DatabaseSettings settings = parseSqlServerUrl(databaseUrl);
        if (settings == null) {
            return;
        }

        String masterUrl = settings.toMasterUrl();
        createDatabaseIfMissing(masterUrl, username, password, settings.databaseName);
    }

    private DatabaseSettings parseSqlServerUrl(String url) {
        String normalized = url.trim();
        if (!normalized.toLowerCase().startsWith("jdbc:sqlserver://")) {
            return null;
        }

        String[] segments = normalized.split(";");
        String hostSegment = segments[0];
        String hostPart = hostSegment.substring("jdbc:sqlserver://".length());
        String host = hostPart;
        String port = "1433";

        if (hostPart.contains(":")) {
            String[] hostParts = hostPart.split(":", 2);
            host = hostParts[0];
            port = hostParts[1];
        }

        String databaseName = null;
        StringBuilder otherOptions = new StringBuilder();
        for (int i = 1; i < segments.length; i++) {
            String segment = segments[i].trim();
            if (segment.toLowerCase().startsWith("databasename=")) {
                databaseName = segment.substring("databasename=".length());
            } else {
                if (otherOptions.length() > 0) {
                    otherOptions.append(";");
                }
                otherOptions.append(segment);
            }
        }

        if (databaseName == null || databaseName.isBlank()) {
            return null;
        }

        return new DatabaseSettings(host, port, databaseName, otherOptions.toString());
    }

    private void createDatabaseIfMissing(String masterUrl, String username, String password, String databaseName) {
        try (Connection connection = connectWithRetry(masterUrl, username, password)) {
            boolean exists = false;
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT name FROM sys.databases WHERE name = ?")) {
                stmt.setString(1, databaseName);
                try (ResultSet rs = stmt.executeQuery()) {
                    exists = rs.next();
                }
            }

            if (!exists) {
                try (PreparedStatement create = connection.prepareStatement(
                        "CREATE DATABASE [" + databaseName + "]")) {
                    create.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to create or verify database: " + databaseName, e);
        }
    }

    private Connection connectWithRetry(String url, String username, String password) throws SQLException {
        SQLException lastException = null;
        for (int attempt = 1; attempt <= MAX_CONNECTION_ATTEMPTS; attempt++) {
            try {
                return DriverManager.getConnection(url, username, password);
            } catch (SQLException e) {
                lastException = e;
                if (attempt == MAX_CONNECTION_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(CONNECTION_RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new SQLException("Interrupted while waiting for SQL Server availability", ie);
                }
            }
        }
        throw lastException;
    }

    private static class DatabaseSettings {
        private final String host;
        private final String port;
        private final String databaseName;
        private final String options;

        private DatabaseSettings(String host, String port, String databaseName, String options) {
            this.host = host;
            this.port = port;
            this.databaseName = databaseName;
            this.options = options;
        }

        String toMasterUrl() {
            StringBuilder builder = new StringBuilder();
            builder.append("jdbc:sqlserver://").append(host).append(":").append(port).append(";databaseName=master");
            if (options != null && !options.isBlank()) {
                builder.append(";").append(options);
            }
            return builder.toString();
        }
    }
}
