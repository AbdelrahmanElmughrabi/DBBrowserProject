# Class Guidelines (Week 10-11: Java Database Programming)

## Key Constraint:
- Minimal comments about these concepts in code
- Apply principles, don't over-document them

## Week 10 - Must Use:
- **Driver**: Load `com.mysql.cj.jdbc.Driver`
- **Connection**: `DriverManager.getConnection()`
- **Statement**: Execute static SQL
- **ResultSet**: Process query results
- **PreparedStatement**: Parameterized queries with `?` placeholders
- **CallableStatement**: Stored procedures (if needed)

## Week 11 - Must Use:
- **DatabaseMetaData**: Get table names, schema info
- **ResultSetMetaData**: Get column names, types, count
- **Scrollable ResultSets**: `TYPE_SCROLL_SENSITIVE`, cursor methods
- **Updatable ResultSets**: `CONCUR_UPDATABLE`
- **RowSet**: JdbcRowSet (connected) or CachedRowSet (disconnected)
- **RowSetListener**: Sync UI with data changes
- **JavaFX TableView**: Display data in tables

## Where to Apply:
- `DatabaseConnection`: Driver, Connection
- `QueryExecutor`: Statement, PreparedStatement, ResultSets
- `DatabaseMetadataHelper`: DatabaseMetaData, ResultSetMetaData
- `Controllers + Views`: RowSet, RowSetListener, TableView
