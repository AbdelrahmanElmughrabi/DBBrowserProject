# DBBrowserProject - MySQL Database Browser

A full-stack JavaFX application demonstrating comprehensive JDBC concepts through a functional database browser with CRUD operations, stored procedures, and RowSet implementations.

---

## Project Overview

This project is a complete MySQL database browser built with JavaFX and JDBC, designed to demonstrate all Week 10-11 JDBC concepts including:
- Connection management with Singleton pattern
- All statement types (Statement, PreparedStatement, CallableStatement)
- DatabaseMetaData and ResultSetMetaData
- RowSet implementations (JdbcRowSet, CachedRowSet)
- RowSetListener for data change notifications
- Stored procedures with IN, OUT, and INOUT parameters

---

## Features

### Core Functionality
- **Database Connection** - Connect to any MySQL database with validation
- **Table Browser** - View all tables in connected database
- **Dynamic CRUD Operations** - Add, Update, Delete records on any table
- **Custom Query Execution** - Run SELECT, INSERT, UPDATE, DELETE, and CALL statements
- **Metadata-Driven UI** - Forms and tables auto-generated from database schema
- **Stored Procedures** - Execute procedures with ResultSet and OUT parameter support
- **Primary Key Detection** - Automatic identification and use in UPDATE/DELETE

### UI Features
- Connection dialog with credential validation
- Table list with dynamic content area
- Auto-generated forms based on table columns
- Refresh button for manual data reload
- Confirmation dialogs for destructive operations
- Error messages displayed in user-friendly alerts
- Disconnect with return to connection screen

### Advanced JDBC Features
- **5 Stored Procedures Implemented**:
  - `GetMoviesByGenre` - IN + ResultSet
  - `CountMoviesByDirector` - IN + OUT
  - `AddMovieWithValidation` - IN (4) + OUT (2)
  - `GetAverageRatingByGenre` - INOUT + OUT (2)
  - `UpdateMovieRating` - IN (2) + OUT
- **RowSet Support** - JdbcRowSet (connected) and CachedRowSet (disconnected)
- **RowSetListener** - Monitors cursor, row, and structural changes
- **Generic Methods** - Varargs support for custom procedures

---

## Technologies Used

- **Java** - JDK 8 or higher
- **JavaFX** - UI framework for desktop application
- **JDBC** - Java Database Connectivity API
- **MySQL** - Database management system (5.7+ or 8.0+)
- **MySQL Connector/J** - JDBC driver for MySQL
- **MVC Architecture** - Model-View-Controller pattern

---

## Prerequisites

Before running this project, ensure you have:

1. **JDK 8 or higher** installed
2. **MySQL Server** (5.7+ or 8.0+) running
3. **MySQL Connector/J** JAR file added to project libraries
4. **JavaFX SDK** (if using JDK 11+)
5. A MySQL database with test data (see Database Setup below)

---

## How to Run

### 1. Clone/Open Project
```bash
# Open in NetBeans or your preferred IDE
cd DBBrowserProject
```

### 2. Add MySQL Connector/J
- Download MySQL Connector/J from [MySQL website](https://dev.mysql.com/downloads/connector/j/)
- Add JAR to project libraries in your IDE

### 3. Setup Test Database (Optional)
Create a test database with the 5 stored procedures:
```sql
-- See src/docs/stored-procedures.sql for full script
CREATE DATABASE moviedb;
USE moviedb;
-- Create tables and stored procedures...
```

### 4. Run Application
- **Main Class**: `application.Main`
- Or run backend tests: `application.DatabaseTest`

### 5. Connect to Database
- URL: `jdbc:mysql://localhost:3306/your_database`
- Username: `root` (or your MySQL user)
- Password: (your MySQL password)

---

## Project Structure

```
DBBrowserProject/
├── src/
│   ├── application/
│   │   ├── Main.java                    # JavaFX entry point
│   │   └── DatabaseTest.java            # Backend testing (no UI)
│   ├── controllers/
│   │   ├── ConnectionController.java    # Connection dialog logic
│   │   └── MainViewController.java      # Main window controller
│   ├── views/
│   │   ├── ConnectionView.fxml          # Connection dialog UI
│   │   ├── MainView.fxml                # Main layout UI
│   │   ├── TableBrowserPane.java        # Table CRUD component
│   │   └── CustomQueryPane.java         # SQL query component
│   ├── database/
│   │   ├── DatabaseConnection.java      # Singleton connection manager
│   │   ├── DatabaseMetadataHelper.java  # Schema metadata operations
│   │   ├── QueryExecutor.java           # CRUD operations
│   │   ├── StoredProcedureExecutor.java # CallableStatement handler
│   │   ├── RowSetManager.java           # RowSet factory
│   │   └── RowSetOperations.java        # RowSet utilities
│   ├── models/
│   │   ├── ColumnMetadata.java          # Column information model
│   │   └── TableRecord.java             # Row data model
│   ├── listeners/
│   │   └── TableDataListener.java       # RowSetListener implementation
│   ├── utils/
│   │   ├── AlertHelper.java             # Dialog utilities
│   │   ├── ValidationHelper.java        # Input validation (stub)
│   │   └── ConfigLoader.java            # Configuration (stub)
│   └── docs/
│       ├── implementation-log.md        # Development history
│       ├── testing-report.md            # Comprehensive test results
│       └── session-2-backend-implementation.md
├── lib/
│   └── mysql-connector-j-x.x.x.jar     # MySQL JDBC driver
└── README.md                            # This file
```

---

## JDBC Concepts Demonstrated

### Week 10 - Core JDBC
- **Driver Loading** - `Class.forName("com.mysql.cj.jdbc.Driver")`
- **Connection** - `DriverManager.getConnection(url, user, pass)`
- **Statement** - Static SQL execution for custom queries
- **PreparedStatement** - Parameterized queries for CRUD operations
- **CallableStatement** - Stored procedure execution with IN/OUT/INOUT parameters
- **ResultSet** - Query results processing and traversal

### Week 11 - Advanced JDBC
- **DatabaseMetaData** - Schema inspection (tables, columns, primary keys)
- **ResultSetMetaData** - Column information from ResultSet
- **Scrollable ResultSets** - `TYPE_SCROLL_INSENSITIVE` with `CONCUR_UPDATABLE`
- **Updatable ResultSets** - Direct row updates via ResultSet
- **JdbcRowSet** - Connected RowSet with live database connection
- **CachedRowSet** - Disconnected RowSet for offline operations
- **RowSetListener** - Event notifications for data changes

### Design Patterns
- **Singleton** - DatabaseConnection ensures single instance
- **MVC** - Model-View-Controller architecture throughout
- **Factory** - RowSetProvider for creating RowSets

---

## Usage Examples

### Basic Operations

**1. Connect to Database**
```
Launch app → Enter credentials → Click "Connect"
```

**2. Browse Table Data**
```
Select table from list → Click "Display Contents" → View all rows
```

**3. Add New Record**
```
Display table → Fill form at bottom → Click "Add New"
```

**4. Update Existing Record**
```
Display table → Click row → Edit form → Click "Update Selected"
```

**5. Delete Record**
```
Display table → Click row → Click "Delete Selected" → Confirm
```

**6. Run Custom Query**
```
Click "Custom Query" → Type SQL → Click "Execute"
```

### Stored Procedure Examples

**Execute procedure with ResultSet:**
```sql
CALL GetMoviesByGenre('Action');
```
→ Displays movie rows in table

**Execute procedure with OUT parameters:**
```sql
CALL AddMovieWithValidation('Inception 2', 2025, 1, 1, @success, @msg);
```
→ Shows completion message

**Query OUT parameter values:**
```sql
SELECT @success, @msg;
```
→ Displays OUT parameter values

---

## Known Limitations

1. **SQL Injection Risk** - UPDATE/DELETE WHERE clauses use string concatenation (should use PreparedStatement)
2. **Type Conversion** - Form inputs sent as String (should parse to INT/DATE/DOUBLE based on column type)
3. **Composite Primary Keys** - Only single-column PKs supported
4. **No Pagination** - Large tables load all rows (may freeze UI)
5. **Reserved Keywords** - SQL keywords in table/column names require manual escaping
6. **Stubs** - ValidationHelper and ConfigLoader not implemented

See [testing-report.md](src/docs/testing-report.md) for comprehensive test results and edge cases.

---

## Test Results

- **Total Tests**: 45+
- **Passed**: 40
- **Known Limitations**: 5
- **Failed**: 0

All Week 10-11 JDBC requirements successfully demonstrated. See full testing report in `src/docs/testing-report.md`.

---

## Documentation

Detailed documentation available in `src/docs/`:
- **implementation-log.md** - Development history across all sessions
- **testing-report.md** - Comprehensive testing results with 8 sections
- **session-2-backend-implementation.md** - Backend architecture details

---

## Learning Objectives Achieved

This project demonstrates proficiency in:
- Establishing and managing database connections
- Executing all types of SQL statements via JDBC
- Handling stored procedures with complex parameter types
- Extracting and using database metadata dynamically
- Implementing RowSet for both connected and disconnected scenarios
- Creating event-driven data change notifications
- Building metadata-driven dynamic UIs
- Proper resource management and error handling
- MVC architecture with clean separation of concerns

---

## Future Enhancements

Optional improvements for extended learning:
- [ ] Fix SQL injection vulnerability with parameterized WHERE clauses
- [ ] Implement type conversion for form inputs
- [ ] Add pagination for large tables
- [ ] Create dedicated Stored Procedure UI panel
- [ ] Support composite primary keys
- [ ] Add transaction management UI (commit/rollback)
- [ ] Implement batch operations for bulk updates
- [ ] Add export functionality (CSV/JSON)
- [ ] Implement ValidationHelper for input validation
- [ ] Add loading indicators for long operations

---

## Contributing

This is a learning project for demonstrating JDBC concepts. Feel free to:
- Fork and experiment
- Report issues or bugs
- Suggest improvements
- Use as reference for JDBC learning

---

## License

This project is created for educational purposes

---

## Development Context

**Purpose**: Learning project for Java Database Connectivity (JDBC)
**Duration**: December 2025 - January 2026
**Sessions**: 3 implementation sessions
**Lines of Code**: ~3500+ (backend + frontend)
**Comment Style**: Simple `//` comments throughout

---

## Support

For questions about JDBC concepts demonstrated in this project:
1. Review documentation in `src/docs/`
2. Check testing report for specific examples
3. Examine code comments for inline explanations
4. Refer to [Oracle JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/)

---

**Built with dedication for mastering JDBC concepts**
