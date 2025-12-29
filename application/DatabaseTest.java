package application;

import database.DatabaseConnection;
import database.DatabaseMetadataHelper;
import database.QueryExecutor;
import models.ColumnMetadata;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

// Standalone test class for database layer
public class DatabaseTest {

    public static void main(String[] args) {
        testDatabaseLayer();
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
}
