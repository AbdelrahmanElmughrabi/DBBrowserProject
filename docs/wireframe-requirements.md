# Wireframe Requirements (P4_wireframe.pdf)

## Screen Flow:
1. **Initial**: "Connect" button only
2. **Connected**: Table list + "Display Contents" + "Custom Query" buttons
3. **Custom Query**: Text area + Execute → Show results
4. **Display Contents**: TableView with Add/Update/Delete buttons
5. **Add New**: Form below table → Insert record
6. **Update Selected**: Pre-filled form → Update record
7. **Delete Selected**: Remove selected row

## Key Features:
- **Connection**: Dialog for URL, username, password
- **Table List**: Left panel, populated from DatabaseMetaData
- **Display Contents**: SELECT * FROM table, dynamic TableView
- **Custom Query**: Execute any SQL, show results
- **Add New**: Dynamic form from column metadata
- **Update Selected**: Pre-fill form with selected row data
- **Delete Selected**: Confirmation + DELETE query

## Technical Notes:
- Dynamic UI based on table schema (ColumnMetadata)
- All CRUD operations use PreparedStatement
- TableView columns generated from ResultSetMetaData
- Need primary key for UPDATE/DELETE WHERE clause
- Refresh TableView after each operation

## Flexibility:
- Wireframe shows basics, free to add enhancements
- Can improve styling, validation, error handling
