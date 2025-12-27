package models;

public class ColumnMetadata {
    private String columnName;
    private String dataType;
    private int columnSize;
    private boolean isNullable;
    private boolean isPrimaryKey;

    public ColumnMetadata() {

    }

    public ColumnMetadata(String columnName, String dataType, int columnSize, boolean isNullable, boolean isPrimaryKey) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.columnSize = columnSize;
        this.isNullable = isNullable;
        this.isPrimaryKey = isPrimaryKey;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean nullable) {
        isNullable = nullable;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }
}
