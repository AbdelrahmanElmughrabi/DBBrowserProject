package controllers;

import database.DatabaseConnection;
import database.DatabaseMetadataHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import utils.AlertHelper;
import views.CustomQueryPane;
import views.TableBrowserPane;

import java.sql.SQLException;
import java.util.List;

public class MainViewController {

    @FXML private ListView<String> tableListView;
    @FXML private StackPane contentPane;

    private String selectedTable;

    @FXML
    public void initialize() {
        loadTableNames();

        tableListView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            selectedTable = newV;
        });
    }

    public void loadTableNames() {
        try {
            DatabaseMetadataHelper meta = new DatabaseMetadataHelper();
            List<String> tables = meta.getTableNames();
            tableListView.getItems().setAll(tables);
        } catch (SQLException e) {
            AlertHelper.showError("Error Loading Tables", e.getMessage());
        }
    }

    @FXML
    public void handleDisplayContents(ActionEvent event) {
        if (selectedTable == null || selectedTable.trim().isEmpty()) {
            AlertHelper.showWarning("No Table Selected", "Pick a table from the left list first.");
            return;
        }

        TableBrowserPane pane = new TableBrowserPane(selectedTable);
        contentPane.getChildren().setAll(pane);
    }

    @FXML
    public void handleCustomQuery(ActionEvent event) {
        CustomQueryPane pane = new CustomQueryPane();
        contentPane.getChildren().setAll(pane);
    }

    @FXML
    public void handleDisconnect(ActionEvent event) {
        try {
            DatabaseConnection.getInstance().disconnect();

            // Load connection view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ConnectionView.fxml"));
            Scene scene = new Scene(loader.load(), 520, 220);
            Stage stage = (Stage) tableListView.getScene().getWindow();
            stage.setTitle("DB Browser - Project 4");
            stage.setScene(scene);

        } catch (SQLException e) {
            AlertHelper.showError("Disconnect Error", e.getMessage());
        } catch (Exception e) {
            AlertHelper.showError("Navigation Error", e.getMessage());
        }
    }
}
