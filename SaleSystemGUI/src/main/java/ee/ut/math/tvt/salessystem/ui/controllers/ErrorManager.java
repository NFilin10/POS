package ee.ut.math.tvt.salessystem.ui.controllers;

import javafx.scene.control.Alert;

public class ErrorManager {
    public static void showError(String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }

    public static void showNegativePriceError() {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText("Negative price");
        errorAlert.setContentText("Entered price cannot be negative. Please enter correct price.");
        errorAlert.showAndWait();
    }
}
