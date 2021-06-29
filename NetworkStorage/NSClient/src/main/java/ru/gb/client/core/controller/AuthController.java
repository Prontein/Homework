package ru.gb.client.core.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class AuthController {

    public Button closeAuthFormBtn;

    public void cancelAuthAction(ActionEvent actionEvent) {
        Stage stage = (Stage)closeAuthFormBtn.getScene().getWindow();
        stage.close();
    }
}
