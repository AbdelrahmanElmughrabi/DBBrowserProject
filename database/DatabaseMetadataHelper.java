package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.ColumnMetadata;

// Helper class to retrieve database schema information
public class DatabaseMetadataHelper {

    private Connection connection;

    public DatabaseMetadataHelper() {
        // Get connection from singleton
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // Get all table names from the database
    public List<String> getTableNames() throws SQLException {
        List<String> tableNames = new ArrayList<>();
        ResultSet rs = null;

        try {
            // Get DatabaseMetaData from connection
            DatabaseMetaData metaData = connection.getMetaData();

            // Get tables of type "TABLE" (excludes views, system tables)
            rs = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            // Process ResultSet
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                tableNames.add(tableName);
            }

        } finally {
            // Close ResultSet
            if (rs != null) {
                rs.close();
            }
        }

        return tableNames;
    }

    // Get column metadata for a specific table
    public List<ColumnMetadata> getColumnMetadata(String tableName) throws SQLException {
        List<ColumnMetadata> columns = new ArrayList<>();
        ResultSet rs = null;
        ResultSet pkRs = null;

        try {
            // Get DatabaseMetaData
            DatabaseMetaData metaData = connection.getMetaData();

            // Get primary key columns for this table
            List<String> primaryKeys = new ArrayList<>();
            pkRs = metaData.getPrimaryKeys(null, null, tableName);
            while (pkRs.next()) {
                primaryKeys.add(pkRs.getString("COLUMN_NAME"));
            }

            // Get all columns for this table
            rs = metaData.getColumns(null, null, tableName, "%");

            // Process each column
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String dataType = rs.getString("TYPE_NAME");
                int columnSize = rs.getInt("COLUMN_SIZE");
                boolean isNullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
                boolean isPrimaryKey = primaryKeys.contains(columnName);

                // Create ColumnMetadata object
                ColumnMetadata column = new ColumnMetadata(columnName, dataType, columnSize, isNullable, isPrimaryKey);
                columns.add(column);
            }

        } finally {
            // Close ResultSets in reverse order
            if (pkRs != null) {
                pkRs.close();
            }
            if (rs != null) {
                rs.close();
            }
        }

        return columns;
    }

    // Get the primary key column name for a table
    public String getPrimaryKeyColumn(String tableName) throws SQLException {
        ResultSet rs = null;

        try {
            // Get DatabaseMetaData
            DatabaseMetaData metaData = connection.getMetaData();

            // Get primary keys
            rs = metaData.getPrimaryKeys(null, null, tableName);

            // Return first primary key found
            if (rs.next()) {
                return rs.getString("COLUMN_NAME");
            }

        } finally {
            // Close ResultSet
            if (rs != null) {
                rs.close();
            }
        }

        return null;
    }
}
