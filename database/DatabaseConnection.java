package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Singleton class to manage database connection
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {}

    // Get singleton instance
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Establish connection to MySQL database
    public void connect(String url, String username, String password) throws SQLException {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create connection
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to database successfully!");

        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }

    // Close database connection
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Disconnected from database");
        }
    }

    // Get the active connection
    public Connection getConnection() {
        return connection;
    }

    // Check if connected to database
    public boolean isConnected() throws SQLException {
        return connection != null && !connection.isClosed();
    }
}
