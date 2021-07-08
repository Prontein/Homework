package ru.gb.client.core.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertWindow {
    private static final Alert alertWindowError = new Alert(Alert.AlertType.ERROR);

    public static void getAlertWindowError (String alertMessage) {
        alertWindowError.setContentText(alertMessage);
        alertWindowError.setResult(ButtonType.OK);
        alertWindowError.showAndWait();
    }
}
