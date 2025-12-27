package database;

import java.sql.ResultSet;
import java.util.Map;

public class QueryExecutor {

    public ResultSet executeQuery(String query) {
        return null;
    }

    public int executeUpdate(String query) {
        return 0;
    }

    public ResultSet selectAll(String tableName) {
        return null;
    }

    public boolean insertRecord(String tableName, Map<String, Object> columnValues) {
        return false;
    }

    public boolean updateRecord(String tableName, Map<String, Object> columnValues, String whereClause) {
        return false;
    }

    public boolean deleteRecord(String tableName, String whereClause) {
        return false;
    }
}
