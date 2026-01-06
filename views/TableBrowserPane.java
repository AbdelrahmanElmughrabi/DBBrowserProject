package views;

import database.DatabaseMetadataHelper;
import database.QueryExecutor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.ColumnMetadata;
import utils.AlertHelper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

public class TableBrowserPane extends BorderPane {

    private final String tableName;

    private final TableView<Map<String, Object>> tableView = new TableView<>();
    private final VBox formBox = new VBox(8);

    private List<ColumnMetadata> columns = new ArrayList<>();
    private String primaryKey = null;

    // keep input fields by column name
    private final Map<String, TextField> inputs = new LinkedHashMap<>();

    public TableBrowserPane(String tableName) {
        this.tableName = tableName;

        setPadding(new Insets(10));

        Label title = new Label("Table: " + tableName);
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Button refreshBtn = new Button("Refresh");
        Button addBtn = new Button("Add New");
        Button updateBtn = new Button("Update Selected");
        Button deleteBtn = new Button("Delete Selected");

        refreshBtn.setOnAction(e -> refreshTable());
        addBtn.setOnAction(e -> insertRecord());
        updateBtn.setOnAction(e -> updateSelected());
        deleteBtn.setOnAction(e -> deleteSelected());

        HBox buttons = new HBox(10, refreshBtn, addBtn, updateBtn, deleteBtn);

        VBox top = new VBox(10, title, buttons);
        top.setPadding(new Insets(0, 0, 10, 0));
        setTop(top);

        setCenter(tableView);

        formBox.setPadding(new Insets(10));
        formBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 6; -fx-background-radius: 6;");
        setBottom(formBox);

        loadMetadata();
        refreshTable();
        buildForm();

        // if user clicks a row -> populate form for update
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, o, row) -> {
            if (row != null) populateForm(row);
        });
    }

    private void loadMetadata() {
        try {
            DatabaseMetadataHelper meta = new DatabaseMetadataHelper();
            this.columns = meta.getColumnMetadata(tableName);
            this.primaryKey = meta.getPrimaryKeyColumn(tableName); // may be null
        } catch (Exception ex) {
            AlertHelper.showError("Metadata Error", ex.getMessage());
        }
    }

    private void refreshTable() {
        try {
            QueryExecutor executor = new QueryExecutor();
            ResultSet rs = executor.selectAll(tableName);
            buildTableFromResultSet(rs);
            rs.close();
        } catch (Exception ex) {
            AlertHelper.showError("Load Table Error", ex.getMessage());
        }
    }

    private void buildTableFromResultSet(ResultSet rs) throws Exception {
        tableView.getItems().clear();
        tableView.getColumns().clear();

        ResultSetMetaData md = rs.getMetaData();
        int colCount = md.getColumnCount();

        for (int i = 1; i <= colCount; i++) {
            String colName = md.getColumnLabel(i);
            TableColumn<Map<String, Object>, Object> col = new TableColumn<>(colName);
            col.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().get(colName)));
            col.setPrefWidth(140);
            tableView.getColumns().add(col);
        }

        ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= colCount; i++) {
                String colName = md.getColumnLabel(i);
                row.put(colName, rs.getObject(i));
            }
            data.add(row);
        }

        tableView.setItems(data);
    }

    private void buildForm() {
        formBox.getChildren().clear();
        inputs.clear();

        Label formTitle = new Label("Form (used for Add / Update)");
        formTitle.setStyle("-fx-font-weight: bold;");

        VBox fields = new VBox(6);

        for (ColumnMetadata c : columns) {
            Label lbl = new Label(c.getColumnName() + " (" + c.getDataType() + ")" + (c.isPrimaryKey() ? " [PK]" : ""));
            TextField tf = new TextField();
            tf.setPromptText(c.isNullable() ? "nullable" : "required");

            // if PK is auto-increment in your DB, you can choose to disable it manually
            // tf.setDisable(c.isPrimaryKey());

            inputs.put(c.getColumnName(), tf);

            VBox one = new VBox(3, lbl, tf);
            fields.getChildren().add(one);
        }

        formBox.getChildren().addAll(formTitle, new Separator(), fields);
    }

    private void populateForm(Map<String, Object> row) {
        for (Map.Entry<String, TextField> e : inputs.entrySet()) {
            Object v = row.get(e.getKey());
            e.getValue().setText(v == null ? "" : String.valueOf(v));
        }
    }

    private Map<String, Object> collectFormValues(boolean includePK) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (ColumnMetadata c : columns) {
            String name = c.getColumnName();
            if (!includePK && c.isPrimaryKey()) continue;

            String raw = inputs.get(name).getText().trim();
            if (raw.isEmpty()) {
                // allow empty only if nullable
                if (!c.isNullable()) {
                    throw new IllegalArgumentException("Field required: " + name);
                }
                values.put(name, null);
            } else {
                values.put(name, raw); // simple: keep as String, JDBC will coerce often
            }
        }
        return values;
    }

    private void insertRecord() {
        try {
            QueryExecutor executor = new QueryExecutor();
            Map<String, Object> values = collectFormValues(false); // skip PK
            boolean ok = executor.insertRecord(tableName, values);
            if (ok) {
                AlertHelper.showInfo("Insert", "Record inserted.");
                refreshTable();
            } else {
                AlertHelper.showWarning("Insert", "No row inserted.");
            }
        } catch (Exception ex) {
            AlertHelper.showError("Insert Error", ex.getMessage());
        }
    }

    private void updateSelected() {
        Map<String, Object> selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Update", "Select a row first.");
            return;
        }
        if (primaryKey == null || primaryKey.trim().isEmpty()) {
            AlertHelper.showError("Update", "No primary key detected. UPDATE needs a primary key.");
            return;
        }

        try {
            Object pkValue = selected.get(primaryKey);
            if (pkValue == null) {
                AlertHelper.showError("Update", "Selected row has null PK value.");
                return;
            }

            QueryExecutor executor = new QueryExecutor();
            Map<String, Object> values = collectFormValues(false); // update non-PK fields
            String where = primaryKey + " = '" + String.valueOf(pkValue).replace("'", "''") + "'";
            boolean ok = executor.updateRecord(tableName, values, where);

            if (ok) {
                AlertHelper.showInfo("Update", "Record updated.");
                refreshTable();
            } else {
                AlertHelper.showWarning("Update", "No row updated.");
            }
        } catch (Exception ex) {
            AlertHelper.showError("Update Error", ex.getMessage());
        }
    }

    private void deleteSelected() {
        Map<String, Object> selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Delete", "Select a row first.");
            return;
        }
        if (primaryKey == null || primaryKey.trim().isEmpty()) {
            AlertHelper.showError("Delete", "No primary key detected. DELETE needs a primary key.");
            return;
        }

        boolean confirm = AlertHelper.showConfirmation("Delete", "Delete selected row?");
        if (!confirm) return;

        try {
            Object pkValue = selected.get(primaryKey);
            if (pkValue == null) {
                AlertHelper.showError("Delete", "Selected row has null PK value.");
                return;
            }

            QueryExecutor executor = new QueryExecutor();
            String where = primaryKey + " = '" + String.valueOf(pkValue).replace("'", "''") + "'";
            boolean ok = executor.deleteRecord(tableName, where);

            if (ok) {
                AlertHelper.showInfo("Delete", "Record deleted.");
                refreshTable();
            } else {
                AlertHelper.showWarning("Delete", "No row deleted.");
            }
        } catch (Exception ex) {
            AlertHelper.showError("Delete Error", ex.getMessage());
        }
    }
}
