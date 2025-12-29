package database;

import java.sql.SQLException;
import java.util.Map;
import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;

// Utility class for RowSet update operations
public class RowSetOperations {

    // Update current row with new values
    public static void updateCurrentRow(RowSet rowSet, Map<String, Object> columnValues) throws SQLException {
        // Set new values for specified columns
        for (Map.Entry<String, Object> entry : columnValues.entrySet()) {
            String columnName = entry.getKey();
            Object value = entry.getValue();

            // Update column value in current row
            rowSet.updateObject(columnName, value);
        }

        // Commit the update to the row
        rowSet.updateRow();
    }

    // Insert new row with specified values
    public static void insertRow(RowSet rowSet, Map<String, Object> columnValues) throws SQLException {
        // Move to insert row position
        rowSet.moveToInsertRow();

        // Set values for new row
        for (Map.Entry<String, Object> entry : columnValues.entrySet()) {
            String columnName = entry.getKey();
            Object value = entry.getValue();

            // Set column value for new row
            rowSet.updateObject(columnName, value);
        }

        // Insert the new row
        rowSet.insertRow();

        // Move back to current row position
        rowSet.moveToCurrentRow();
    }

    // Delete current row
    public static void deleteCurrentRow(RowSet rowSet) throws SQLException {
        // Delete the current row
        rowSet.deleteRow();
    }

    // Cancel row updates (before calling updateRow)
    public static void cancelRowUpdates(RowSet rowSet) throws SQLException {
        rowSet.cancelRowUpdates();
    }

    // Refresh current row from database
    public static void refreshRow(RowSet rowSet) throws SQLException {
        rowSet.refreshRow();
    }

    // Check if row was inserted
    public static boolean isRowInserted(RowSet rowSet) throws SQLException {
        return rowSet.rowInserted();
    }

    // Check if row was updated
    public static boolean isRowUpdated(RowSet rowSet) throws SQLException {
        return rowSet.rowUpdated();
    }

    // Check if row was deleted
    public static boolean isRowDeleted(RowSet rowSet) throws SQLException {
        return rowSet.rowDeleted();
    }

    // Get value from current row
    public static Object getValue(RowSet rowSet, String columnName) throws SQLException {
        return rowSet.getObject(columnName);
    }

    // Get value from current row by column index
    public static Object getValue(RowSet rowSet, int columnIndex) throws SQLException {
        return rowSet.getObject(columnIndex);
    }

    // Sync CachedRowSet changes back to database
    public static void syncToDatabase(CachedRowSet cachedRowSet) throws SQLException {
        // Get connection from singleton
        cachedRowSet.acceptChanges(DatabaseConnection.getInstance().getConnection());
    }

    // Check if cursor is on insert row
    public static boolean isOnInsertRow(RowSet rowSet) throws SQLException {
        return rowSet.isAfterLast() || rowSet.isBeforeFirst();
    }

    // Get row count (for CachedRowSet - must iterate)
    public static int getRowCount(RowSet rowSet) throws SQLException {
        int currentRow = rowSet.getRow();
        int count = 0;

        // Move to last row
        if (rowSet.last()) {
            count = rowSet.getRow();
        }

        // Move back to original position
        if (currentRow > 0) {
            rowSet.absolute(currentRow);
        }

        return count;
    }
}
