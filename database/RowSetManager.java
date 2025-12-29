package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetProvider;

// Manages RowSet operations - both connected and disconnected
public class RowSetManager {

    private Connection connection;

    public RowSetManager() {
        // Get connection from singleton
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // Create connected JdbcRowSet from custom query
    public JdbcRowSet createJdbcRowSet(String query, String url, String username, String password) throws SQLException {
        // Create JdbcRowSet using factory
        JdbcRowSet jdbcRowSet = RowSetProvider.newFactory().createJdbcRowSet();

        // Set connection credentials
        jdbcRowSet.setUrl(url);
        jdbcRowSet.setUsername(username);
        jdbcRowSet.setPassword(password);

        // Set command (query)
        jdbcRowSet.setCommand(query);

        // Execute query to populate RowSet
        jdbcRowSet.execute();

        return jdbcRowSet;
    }

    // Create disconnected CachedRowSet from custom query
    public CachedRowSet createCachedRowSet(String query) throws SQLException {
        // Create CachedRowSet using factory
        CachedRowSet cachedRowSet = RowSetProvider.newFactory().createCachedRowSet();

        // Populate from query using connection
        cachedRowSet.setCommand(query);
        cachedRowSet.execute(connection);

        return cachedRowSet;
    }

    // Create JdbcRowSet from a table
    public JdbcRowSet getTableRowSet(String tableName, String url, String username, String password) throws SQLException {
        String query = "SELECT * FROM " + tableName;
        return createJdbcRowSet(query, url, username, password);
    }

    // Create CachedRowSet from a table
    public CachedRowSet getTableCachedRowSet(String tableName) throws SQLException {
        String query = "SELECT * FROM " + tableName;
        return createCachedRowSet(query);
    }

    // Populate CachedRowSet from existing ResultSet
    public CachedRowSet createCachedRowSetFromResultSet(ResultSet rs) throws SQLException {
        // Create CachedRowSet using factory
        CachedRowSet cachedRowSet = RowSetProvider.newFactory().createCachedRowSet();

        // Populate from ResultSet
        cachedRowSet.populate(rs);

        return cachedRowSet;
    }

    // Sync CachedRowSet changes back to database
    public void syncCachedRowSet(CachedRowSet cachedRowSet) throws SQLException {
        // Accept changes - syncs modifications back to database
        cachedRowSet.acceptChanges(connection);
    }

    // Navigate to first row
    public boolean moveToFirst(RowSet rowSet) throws SQLException {
        return rowSet.first();
    }

    // Navigate to last row
    public boolean moveToLast(RowSet rowSet) throws SQLException {
        return rowSet.last();
    }

    // Navigate to next row
    public boolean moveToNext(RowSet rowSet) throws SQLException {
        return rowSet.next();
    }

    // Navigate to previous row
    public boolean moveToPrevious(RowSet rowSet) throws SQLException {
        return rowSet.previous();
    }

    // Navigate to specific row (1-indexed)
    public boolean moveToAbsolute(RowSet rowSet, int row) throws SQLException {
        return rowSet.absolute(row);
    }

    // Get current row number
    public int getCurrentRow(RowSet rowSet) throws SQLException {
        return rowSet.getRow();
    }

    // Close RowSet and free resources
    public void closeRowSet(RowSet rowSet) throws SQLException {
        if (rowSet != null) {
            rowSet.close();
        }
    }
}
