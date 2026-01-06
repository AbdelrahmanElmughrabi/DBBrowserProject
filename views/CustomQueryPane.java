package views;

import database.QueryExecutor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utils.AlertHelper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

public class CustomQueryPane extends BorderPane {

    private final TextArea queryArea = new TextArea();
    private final TableView<Map<String, Object>> tableView = new TableView<>();

    public CustomQueryPane() {
        setPadding(new Insets(10));

        Label title = new Label("Custom Query");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        queryArea.setPromptText("Write a SELECT query here (example: SELECT * FROM movies;)");
        queryArea.setPrefRowCount(6);

        Button executeBtn = new Button("Execute");
        executeBtn.setOnAction(e -> executeQuery());

        HBox topButtons = new HBox(10, executeBtn);

        VBox top = new VBox(10, title, queryArea, topButtons);
        top.setPadding(new Insets(0, 0, 10, 0));

        setTop(top);
        setCenter(tableView);
    }

    private void executeQuery() {
        String sql = queryArea.getText().trim();
        if (sql.isEmpty()) {
            AlertHelper.showWarning("Empty Query", "Write a query first.");
            return;
        }

        try {
            QueryExecutor executor = new QueryExecutor();

            // If user writes SELECT -> show table results
            if (sql.toLowerCase().startsWith("select")) {
                ResultSet rs = executor.executeQuery(sql);
                buildTableFromResultSet(rs);
                rs.close();
            } else {
                int affected = executor.executeUpdate(sql);
                AlertHelper.showInfo("Query Executed", "Rows affected: " + affected);
                tableView.getItems().clear();
                tableView.getColumns().clear();
            }

        } catch (Exception ex) {
            AlertHelper.showError("Query Error", ex.getMessage());
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
}
