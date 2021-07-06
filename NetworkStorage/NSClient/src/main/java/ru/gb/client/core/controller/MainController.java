package ru.gb.client.core.controller;

import domain.FileType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import domain.allertsandexeption.AlertMessage;
import ru.gb.client.core.commandhandler.CommandDictionary.dictionaryinterface.CommandDictionaryService;
import ru.gb.client.core.controller.callback.Callback;
import ru.gb.client.core.networkservice.networkInterface.ClientNetworkService;
import ru.gb.client.factory.Factory;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import domain.MessageDTO;


public class MainController implements Initializable, Callback {

    public final static String CLIENT = "client";
    public final static String SERVER = "server";
    public final static String POINTER = "ctrl";

    private CommandDictionaryService dictionaryService;
    private ClientNetworkService networkService;
    private PanelController serverPanelController;
    private PanelController clientPanelController;
    private Callback callback;
    public Button copyBtn;
    public Button registrationOnServerBtn;
    public TextField loginField;
    public PasswordField passwordField;

    @FXML
    AnchorPane controlPanelClient;
    @FXML
    AnchorPane controlPanelServer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadClientInterface();
        dictionaryService = Factory.getCommandDictionaryService();
        networkService = Factory.getNetworkService(this);
        networkService.start();

    }

    private void loadClientInterface () {
        loadDiskCatalog();
        loadFilesCatalogView();
        loadServerCatalogView();
    }

    private void loadDiskCatalog () {
        clientPanelController = (PanelController) controlPanelClient.getProperties().get(POINTER);
        clientPanelController.loadDiskCatalog();
    }

    private void loadFilesCatalogView () {
        clientPanelController = (PanelController) controlPanelClient.getProperties().get(POINTER);
        clientPanelController.ClientFilesView();
    }

    private void loadServerCatalogView() {
        serverPanelController = (PanelController) controlPanelServer.getProperties().get(POINTER);
        addListenerOnServerUpBtn(serverPanelController);
        addListenerOnServerCatalogView(serverPanelController);
    }

    private void addListenerOnServerUpBtn (PanelController serverPanelController) {
        serverPanelController.serverCatalogUpBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                networkService.upServerCatalog(serverPanelController.getServerPathField());
            }
        });
    }

    private void addListenerOnServerCatalogView (PanelController serverPanelController) {
        serverPanelController.filesTableServer.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2 && !serverPanelController.filesTableServer.getSelectionModel().
                        getSelectedItem().getType().equals(FileType.FILE)) {

                    String selectFilePathFromServer = serverPanelController.serverPathField.getText();
                    String selectFileNameFromServer = serverPanelController.getSelectedFilename(SERVER);
                    networkService.showSelectedCatalog(selectFilePathFromServer, selectFileNameFromServer);
                }
            }
        });
    }

    public void registrationAction(ActionEvent actionEvent) {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (!login.equals("") && !password.equals("")) {
            networkService.registrationOnServer(login,password);
        } else {
            typeAlert(AlertMessage.INPUT_ERROR);
        }
    }

    public void connectAction(ActionEvent actionEvent) {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (!login.equals("") && !password.equals("")) {
            networkService.connectWithServer(login,password);
        } else {
            typeAlert(AlertMessage.INPUT_ERROR);
        }
    }

    public void copyFileAction(ActionEvent actionEvent) {
        if (isFileSelected()) {
            if (clientPanelController.filesTable.isFocused()) {
                networkService.copyFile(clientPanelController.getSelectedFilename(CLIENT), serverPanelController.getServerPathField(),
                        clientPanelController.getSelectedFileSize(CLIENT));
            } else {
                copyFromServer(serverPanelController.getSelectedFilename(SERVER), clientPanelController.getCurrentPath(),
                        serverPanelController.getSelectedFileSize(SERVER));
            }
        }
    }

    public void refreshClientCatalog() { ;
        clientPanelController.updateClientPanel();
    }

    private boolean isFileSelected () {
        if (clientPanelController.getSelectedFilename(CLIENT) == null && serverPanelController.getSelectedFilename(SERVER) == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, AlertMessage.SELECT_ERROR, ButtonType.OK);
            alert.showAndWait();
            return false;
        }
        return true;
    }

    public void copyFromServer (String filename, String pathClientDirectory, Long fileSize) {
        if (clientPanelController.fileIsExists(filename, pathClientDirectory)) {
            String pathServerDirectory = serverPanelController.getServerPathField();
            networkService.copyFileFromServer(filename, pathClientDirectory, pathServerDirectory, fileSize);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, AlertMessage.COPY_ERROR, ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void startCopyFileToServer () {
        Path path = Paths.get(clientPanelController.getCurrentPath(), clientPanelController.getSelectedFilename(CLIENT));
        networkService.startCopyFile(path.toString());
    }

    public void typeAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR, alertMessage, ButtonType.OK);
        alert.showAndWait();
    }

    public PanelController getServerPanelController() {
        return serverPanelController;
    }

    public void shutdown() {
        networkService.closeConnection();
    }

    @Override
    public void callback (MessageDTO serverMessage) {
        dictionaryService.processCommand(serverMessage, this);
    }


}
