package sample;

import javafx.scene.control.Alert;

public class ErrorDisplay {
    static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public ErrorDisplay() {
    }
}
