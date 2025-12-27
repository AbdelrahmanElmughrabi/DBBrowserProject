package database;

import java.sql.Connection;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {

    }

    public static DatabaseConnection getInstance() {
        return instance;
    }

    public void connect(String url, String username, String password) {

    }

    public void disconnect() {

    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isConnected() {
        return false;
    }
}
