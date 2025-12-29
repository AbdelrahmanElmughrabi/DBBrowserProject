package application;

import javafx.application.Application;
import javafx.stage.Stage;
import database.DatabaseConnection;
import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Test database connection
        testDatabaseConnection();
    }

    // Test method to verify database connection works
    private void testDatabaseConnection() {
        try {
            // Get singleton instance
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();

            // Connect to database  
            String url = "jdbc:mysql://localhost:3306/moviedb";
            String username = "root";
            String password = "";

            dbConnection.connect(url, username, password);

            // Check if connected
            if (dbConnection.isConnected()) {
                System.out.println("Database connection test successful!");
            }

            // Disconnect when done
            dbConnection.disconnect();

        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
