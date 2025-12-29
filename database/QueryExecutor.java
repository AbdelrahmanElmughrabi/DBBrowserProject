package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

// Handles all SQL query execution
public class QueryExecutor {

    private Connection connection;

    public QueryExecutor() {
        // Get connection from singleton
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // Execute custom SELECT query
    public ResultSet executeQuery(String query) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Create Statement
            stmt = connection.createStatement();

            // Execute query
            rs = stmt.executeQuery(query);

            return rs;

        } catch (SQLException e) {
            // Close resources on error
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            throw e;
        }
    }

    // Execute custom INSERT/UPDATE/DELETE query
    public int executeUpdate(String query) throws SQLException {
        Statement stmt = null;

        try {
            // Create Statement
            stmt = connection.createStatement();

            // Execute update
            int rowsAffected = stmt.executeUpdate(query);

            return rowsAffected;

        } finally {
            // Close Statement
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    // Select all records from a table
    public ResultSet selectAll(String tableName) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Create Statement
            stmt = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
            );

            // Build and execute query
            String query = "SELECT * FROM " + tableName;
            rs = stmt.executeQuery(query);

            return rs;

        } catch (SQLException e) {
            // Close resources on error
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            throw e;
        }
    }

    // Insert a new record using PreparedStatement
    public boolean insertRecord(String tableName, Map<String, Object> columnValues) throws SQLException {
        PreparedStatement pstmt = null;

        try {
            // Build INSERT query with placeholders
            StringBuilder columns = new StringBuilder();
            StringBuilder placeholders = new StringBuilder();

            for (String columnName : columnValues.keySet()) {
                if (columns.length() > 0) {
                    columns.append(", ");
                    placeholders.append(", ");
                }
                columns.append(columnName);
                placeholders.append("?");
            }

            String query = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";

            // Create PreparedStatement
            pstmt = connection.prepareStatement(query);

            // Set parameter values
            int paramIndex = 1;
            for (Object value : columnValues.values()) {
                pstmt.setObject(paramIndex++, value);
            }

            // Execute update
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;

        } finally {
            // Close PreparedStatement
            if (pstmt != null) {
                pstmt.close();
            }
        }
    }

    // Update a record using PreparedStatement
    public boolean updateRecord(String tableName, Map<String, Object> columnValues, String whereClause) throws SQLException {
        PreparedStatement pstmt = null;

        try {
            // Build UPDATE query with placeholders
            StringBuilder setClause = new StringBuilder();

            for (String columnName : columnValues.keySet()) {
                if (setClause.length() > 0) {
                    setClause.append(", ");
                }
                setClause.append(columnName).append(" = ?");
            }

            String query = "UPDATE " + tableName + " SET " + setClause + " WHERE " + whereClause;

            // Create PreparedStatement
            pstmt = connection.prepareStatement(query);

            // Set parameter values
            int paramIndex = 1;
            for (Object value : columnValues.values()) {
                pstmt.setObject(paramIndex++, value);
            }

            // Execute update
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;

        } finally {
            // Close PreparedStatement
            if (pstmt != null) {
                pstmt.close();
            }
        }
    }

    // Delete a record using PreparedStatement
    public boolean deleteRecord(String tableName, String whereClause) throws SQLException {
        PreparedStatement pstmt = null;

        try {
            // Build DELETE query
            String query = "DELETE FROM " + tableName + " WHERE " + whereClause;

            // Create PreparedStatement
            pstmt = connection.prepareStatement(query);

            // Execute update
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;

        } finally {
            // Close PreparedStatement
            if (pstmt != null) {
                pstmt.close();
            }
        }
    }
}
