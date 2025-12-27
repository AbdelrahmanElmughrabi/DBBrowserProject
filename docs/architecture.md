# Architecture Overview

## Design Patterns:
- **Singleton**: DatabaseConnection (one connection instance)
- **MVC**: Controllers handle UI logic, Models hold data, Views define UI
- **Builder**: ComponentBuilder for dynamic UI generation

## Layer Separation:
```
UI Layer (JavaFX)
    ↓
Controller Layer (Event handlers)
    ↓
Database Layer (JDBC operations)
    ↓
MySQL Database
```

## Data Flow:
1. User action → Controller
2. Controller → Database layer (QueryExecutor/MetadataHelper)
3. Database executes JDBC operations
4. Results → Controller
5. Controller updates UI (TableView, forms)

## Key Classes:
- **DatabaseConnection**: Manages single connection (singleton)
- **QueryExecutor**: All SQL operations (CRUD)
- **DatabaseMetadataHelper**: Schema information
- **ComponentBuilder**: Dynamic UI from metadata
- **TableRecord**: Generic row representation
- **ColumnMetadata**: Column information storage

## Generic Design:
- No hardcoded table names or schemas
- Everything driven by metadata
- Works with any MySQL database structure
