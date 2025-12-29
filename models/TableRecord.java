package models;

import java.util.HashMap;
import java.util.Map;

// Represents a single row from a database table
public class TableRecord {
    private Map<String, Object> columnData;

    public TableRecord() {
        this.columnData = new HashMap<>();
    }

    public TableRecord(Map<String, Object> columnData) {
        this.columnData = columnData;
    }

    // Get value for a specific column
    public Object getValue(String columnName) {
        return columnData.get(columnName);
    }

    // Set value for a specific column
    public void setValue(String columnName, Object value) {
        columnData.put(columnName, value);
    }

    // Get all column data
    public Map<String, Object> getColumnData() {
        return columnData;
    }

    // Set all column data
    public void setColumnData(Map<String, Object> columnData) {
        this.columnData = columnData;
    }
}
