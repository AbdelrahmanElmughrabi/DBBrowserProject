package controllers;

import database.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import utils.AlertHelper;

public class ConnectionController {

    @FXML private Label statusLabel;

    @FXML
    public void handleConnect(ActionEvent event) {
        try {
            ConnectionInfo info = showConnectionDialog();
            if (info == null) return; // cancelled

            DatabaseConnection.getInstance().connect(info.url, info.username, info.password);

            // Load main view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainView.fxml"));
            Scene scene = new Scene(loader.load(), 1100, 650);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("DB Browser - Connected");
            stage.setScene(scene);

        } catch (Exception ex) {
            AlertHelper.showError("Connection Failed", ex.getMessage());
            if (statusLabel != null) statusLabel.setText("Connection failed.");
        }
    }

    public ConnectionInfo showConnectionDialog() {
        Dialog<ConnectionInfo> dialog = new Dialog<>();
        dialog.setTitle("Connect to Database");
        dialog.setHeaderText("Enter connection info (MySQL)");

        ButtonType connectType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(connectType, ButtonType.CANCEL);

        TextField urlField = new TextField("jdbc:mysql://localhost:3306/your_db");
        TextField userField = new TextField("root");
        PasswordField passField = new PasswordField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.addRow(0, new Label("JDBC URL:"), urlField);
        grid.addRow(1, new Label("Username:"), userField);
        grid.addRow(2, new Label("Password:"), passField);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == connectType) {
                String url = urlField.getText().trim();
                String user = userField.getText().trim();
                String pass = passField.getText();
                if (url.isEmpty() || user.isEmpty()) {
                    AlertHelper.showWarning("Missing Fields", "URL and Username are required.");
                    return null;
                }
                return new ConnectionInfo(url, user, pass);
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    public static class ConnectionInfo {
        public final String url;
        public final String username;
        public final String password;

        public ConnectionInfo(String url, String username, String password) {
            this.url = url;
            this.username = username;
            this.password = password;
        }
    }
}
