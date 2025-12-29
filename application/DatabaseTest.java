package application;

import database.DatabaseConnection;
import database.DatabaseMetadataHelper;
import database.QueryExecutor;
import database.RowSetManager;
import database.RowSetOperations;
import listeners.TableDataListener;
import models.ColumnMetadata;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.JdbcRowSet;

// Standalone test class for database layer
public class DatabaseTest {

    public static void main(String[] args) {
        testDatabaseLayer();
        System.out.println("\n\n");
        testRowSetFunctionality();
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
}
