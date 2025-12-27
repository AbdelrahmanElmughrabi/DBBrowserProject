package models;

import java.util.Map;

public class TableRecord {
    private Map<String, Object> columnData;

    public TableRecord() {

    }

    public TableRecord(Map<String, Object> columnData) {
        this.columnData = columnData;
    }

    public Object getValue(String columnName) {
        return null;
    }

    public void setValue(String columnName, Object value) {

    }

    public Map<String, Object> getColumnData() {
        return columnData;
    }

    public void setColumnData(Map<String, Object> columnData) {
        this.columnData = columnData;
    }
}
