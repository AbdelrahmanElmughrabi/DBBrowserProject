package application;

import database.DatabaseConnection;
import database.DatabaseMetadataHelper;
import database.QueryExecutor;
import database.RowSetManager;
import database.RowSetOperations;
import database.StoredProcedureExecutor;
import listeners.TableDataListener;
import models.ColumnMetadata;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.JdbcRowSet;

// Standalone test class for database layer
public class DatabaseTest {

    public static void main(String[] args) {
        testDatabaseLayer();
        System.out.println("\n\n");
        testRowSetFunctionality();
        System.out.println("\n\n");
        testStoredProcedures();
        System.out.println("\n\n");
        testAdvancedStoredProcedures();
    }

    // Comprehensive test of database layer
    private static void testDatabaseLayer() {
        try {
            // Step 1: Connect to database
            System.out.println("=== TESTING DATABASE CONNECTION ===");
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            dbConnection.connect("jdbc:mysql://localhost:3306/moviedb", "root", "");
            System.out.println("✓ Connected to database\n");

            // Step 2: Test DatabaseMetadataHelper
            System.out.println("=== TESTING METADATA HELPER ===");
            DatabaseMetadataHelper metadataHelper = new DatabaseMetadataHelper();

            // Get all table names
            List<String> tables = metadataHelper.getTableNames();
            System.out.println("Tables found: " + tables.size());
            for (String table : tables) {
                System.out.println("  - " + table);
            }

            // Get column metadata for first table
            if (!tables.isEmpty()) {
                String firstTable = tables.get(0);
                System.out.println("\nColumn metadata for '" + firstTable + "':");
                List<ColumnMetadata> columns = metadataHelper.getColumnMetadata(firstTable);
                for (ColumnMetadata col : columns) {
                    System.out.println("  - " + col.getColumnName() + " (" + col.getDataType() +
                                     ") PK: " + col.isPrimaryKey());
                }

                // Get primary key
                String pk = metadataHelper.getPrimaryKeyColumn(firstTable);
                System.out.println("Primary key: " + pk + "\n");
            }

            // Step 3: Test QueryExecutor
            System.out.println("=== TESTING QUERY EXECUTOR ===");
            QueryExecutor queryExecutor = new QueryExecutor();

            // Test SELECT query
            if (!tables.isEmpty()) {
                String tableName = tables.get(0);
                System.out.println("Selecting all from '" + tableName + "':");
                ResultSet rs = queryExecutor.selectAll(tableName);

                // Get column count
                ResultSetMetaData rsMetaData = rs.getMetaData();
                int columnCount = rsMetaData.getColumnCount();

                // Print column names
                System.out.print("Columns: ");
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rsMetaData.getColumnName(i));
                    if (i < columnCount) System.out.print(", ");
                }
                System.out.println();

                // Print first 3 rows
                int rowCount = 0;
                while (rs.next() && rowCount < 3) {
                    System.out.print("Row " + (rowCount + 1) + ": ");
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(rs.getString(i));
                        if (i < columnCount) System.out.print(" | ");
                    }
                    System.out.println();
                    rowCount++;
                }
                rs.close();
                System.out.println("Total rows displayed: " + rowCount + "\n");
            }

            System.out.println("=== ALL TESTS COMPLETED SUCCESSFULLY ===");

            // Disconnect
            dbConnection.disconnect();

        } catch (SQLException e) {
            System.err.println("Database test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Test RowSet functionality
    private static void testRowSetFunctionality() {
        JdbcRowSet jdbcRowSet = null;
        CachedRowSet cachedRowSet = null;

        try {
            System.out.println("=== TESTING ROWSET FUNCTIONALITY ===");

            // Connect to database
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            dbConnection.connect("jdbc:mysql://localhost:3306/moviedb", "root", "");

            // Get first table
            DatabaseMetadataHelper metadataHelper = new DatabaseMetadataHelper();
            List<String> tables = metadataHelper.getTableNames();

            if (tables.isEmpty()) {
                System.out.println("No tables found for RowSet testing");
                return;
            }

            String tableName = tables.get(0);
            System.out.println("Testing with table: " + tableName + "\n");

            // Step 1: Test JdbcRowSet (connected)
            System.out.println("=== TESTING JDBCROWSET ===");
            RowSetManager rowSetManager = new RowSetManager();
            jdbcRowSet = rowSetManager.getTableRowSet(tableName,
                "jdbc:mysql://localhost:3306/moviedb", "root", "");

            // Add listener
            TableDataListener listener = new TableDataListener("JdbcRowSetListener");
            jdbcRowSet.addRowSetListener(listener);

            // Test navigation
            System.out.println("Navigation test:");
            if (jdbcRowSet.first()) {
                System.out.println("  - First row: Row " + jdbcRowSet.getRow());
            }
            if (jdbcRowSet.last()) {
                System.out.println("  - Last row: Row " + jdbcRowSet.getRow());
            }
            if (jdbcRowSet.absolute(2)) {
                System.out.println("  - Absolute(2): Row " + jdbcRowSet.getRow());
            }

            // Get row count
            int rowCount = RowSetOperations.getRowCount(jdbcRowSet);
            System.out.println("  - Total rows: " + rowCount + "\n");

            // Step 2: Test CachedRowSet (disconnected)
            System.out.println("=== TESTING CACHEDROWSET ===");
            cachedRowSet = rowSetManager.getTableCachedRowSet(tableName);

            // Add listener
            TableDataListener cachedListener = new TableDataListener("CachedRowSetListener");
            cachedRowSet.addRowSetListener(cachedListener);

            // Test navigation on CachedRowSet
            System.out.println("Navigation test:");
            if (cachedRowSet.first()) {
                System.out.println("  - First row: Row " + cachedRowSet.getRow());
            }
            if (cachedRowSet.next()) {
                System.out.println("  - Next row: Row " + cachedRowSet.getRow());
            }

            // CachedRowSet can work offline now
            System.out.println("  - CachedRowSet is disconnected and can work offline\n");

            System.out.println("=== ROWSET TESTS COMPLETED ===");

            // Disconnect
            dbConnection.disconnect();

        } catch (SQLException e) {
            System.err.println("RowSet test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close RowSets
            try {
                if (jdbcRowSet != null) jdbcRowSet.close();
                if (cachedRowSet != null) cachedRowSet.close();
            } catch (SQLException e) {
                System.err.println("Error closing RowSets: " + e.getMessage());
            }
        }
    }

    // Test stored procedures with CallableStatement
    private static void testStoredProcedures() {
        ResultSet rs = null;

        try {
            System.out.println("=== TESTING STORED PROCEDURES ===");

            // Connect to database
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            dbConnection.connect("jdbc:mysql://localhost:3306/moviedb", "root", "");

            StoredProcedureExecutor spExecutor = new StoredProcedureExecutor();

            // Test 1: GetMoviesByGenre (IN parameter, returns ResultSet)
            System.out.println("=== TEST 1: GetMoviesByGenre ===");
            System.out.println("Calling procedure: GetMoviesByGenre('Action')");

            rs = spExecutor.getMoviesByGenre("Action");

            // Display results
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columnCount = rsMetaData.getColumnCount();

            System.out.print("Columns: ");
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rsMetaData.getColumnName(i));
                if (i < columnCount) System.out.print(", ");
            }
            System.out.println();

            int rowCount = 0;
            while (rs.next()) {
                System.out.print("  Movie " + (rowCount + 1) + ": ");
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i));
                    if (i < columnCount) System.out.print(" | ");
                }
                System.out.println();
                rowCount++;
            }
            System.out.println("Total Action movies: " + rowCount + "\n");
            rs.close();

            // Test 2: CountMoviesByDirector (IN + OUT parameters)
            System.out.println("=== TEST 2: CountMoviesByDirector ===");
            System.out.println("Calling procedure: CountMoviesByDirector('Nolan')");

            int movieCount = spExecutor.countMoviesByDirector("Nolan");

            System.out.println("Movies directed by Nolan: " + movieCount + "\n");

            System.out.println("=== STORED PROCEDURE TESTS COMPLETED ===");

            // Disconnect
            dbConnection.disconnect();

        } catch (SQLException e) {
            System.err.println("Stored procedure test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close ResultSet
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }
    }

    // Test advanced stored procedures (multiple OUT parameters)
    private static void testAdvancedStoredProcedures() {
        try {
            System.out.println("=== TESTING ADVANCED STORED PROCEDURES ===" );

            // Connect to database
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            dbConnection.connect("jdbc:mysql://localhost:3306/moviedb", "root", "");

            StoredProcedureExecutor spExecutor = new StoredProcedureExecutor();

            // Test 1: AddMovieWithValidation (IN + multiple OUT)
            System.out.println("=== TEST 1: AddMovieWithValidation ===");
            System.out.println("Calling procedure: AddMovieWithValidation('Inception 2', 2025, 1, 1)");

            Map<String, Object> validationResult = spExecutor.addMovieWithValidation(
                "Inception 2", 2025, 1, 1
            );

            System.out.println("Result:");
            System.out.println("  - Success: " + validationResult.get("success"));
            System.out.println("  - Message: " + validationResult.get("message"));
            System.out.println();

            // Test 2: GetAverageRatingByGenre (INOUT + multiple OUT)
            System.out.println("=== TEST 2: GetAverageRatingByGenre ===");
            System.out.println("Calling procedure: GetAverageRatingByGenre(1)");

            Map<String, Object> ratingResult = spExecutor.getAverageRatingByGenre(1);

            System.out.println("Result:");
            System.out.println("  - Genre ID: " + ratingResult.get("genreId"));
            System.out.println("  - Average Rating: " + ratingResult.get("avgRating"));
            System.out.println("  - Movie Count: " + ratingResult.get("movieCount"));
            System.out.println();

            // Test 3: UpdateMovieRating (IN + OUT)
            System.out.println("=== TEST 3: UpdateMovieRating ===");
            System.out.println("Calling procedure: UpdateMovieRating(1, 9.5)");

            double oldRating = spExecutor.updateMovieRating(1, 9.5);

            System.out.println("Result:");
            System.out.println("  - Old Rating: " + oldRating);
            System.out.println("  - New Rating: 9.5");
            System.out.println();

            System.out.println("=== ADVANCED STORED PROCEDURE TESTS COMPLETED ===");

            // Disconnect
            dbConnection.disconnect();

        } catch (SQLException e) {
            System.err.println("Advanced stored procedure test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
