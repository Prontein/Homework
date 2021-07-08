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
import ru.gb.client.core.commandhandler.commanddictionary.dictionaryservice.CommandDictionaryService;
import ru.gb.client.core.controller.callback.Callback;
import ru.gb.client.core.networkservice.clientservice.FunctionalNettyClient;
import ru.gb.client.core.util.AlertWindow;
import ru.gb.client.factory.Factory;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import domain.MessageDTO;


public class MainController implements Initializable, Callback {

    public final static String CLIENT_PANEL = "client";
    public final static String SERVER_PANEL = "server";
    public final static String POINTER = "ctrl";

    private CommandDictionaryService dictionaryService;
    private FunctionalNettyClient networkService;
    private PanelController serverPanelController;
    private PanelController clientPanelController;
    public Button copyBtn;
    public Button registrationOnServerBtn;
    public TextField loginField;
    public PasswordField passwordField;

    @FXML
    private AnchorPane controlPanelClient;
    @FXML
    private AnchorPane controlPanelServer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadClientInterface();
        dictionaryService = Factory.getCommandDictionaryService();
        networkService = Factory.getNetworkService(this);
        networkService.start();
    }

    private void loadClientInterface() {
        loadDiskCatalog();
        loadFilesCatalogView();
        loadServerCatalogView();
    }

    private void loadDiskCatalog() {
        clientPanelController = (PanelController) controlPanelClient.getProperties().get(POINTER);
        clientPanelController.loadDiskCatalog();
    }

    private void loadFilesCatalogView() {
        clientPanelController = (PanelController) controlPanelClient.getProperties().get(POINTER);
        clientPanelController.ClientFilesView();
    }

    private void loadServerCatalogView() {
        serverPanelController = (PanelController) controlPanelServer.getProperties().get(POINTER);
        addListenerOnServerUpBtn(serverPanelController);
        addListenerOnServerCatalogView(serverPanelController);
    }

    private void addListenerOnServerUpBtn(PanelController serverPanelController) {
        serverPanelController.serverCatalogUpBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                networkService.upServerCatalog(serverPanelController.getServerPathField());
            }
        });
    }

    private void addListenerOnServerCatalogView(PanelController serverPanelController) {
        serverPanelController.filesTableServer.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2 && !serverPanelController.filesTableServer.getSelectionModel().
                        getSelectedItem().getType().equals(FileType.FILE)) {

                    String selectFilePathFromServer = serverPanelController.serverPathField.getText();
                    String selectFileNameFromServer = serverPanelController.getSelectedFilename(SERVER_PANEL);
                    networkService.showSelectedCatalog(selectFilePathFromServer, selectFileNameFromServer);
                }
            }
        });
    }

    public void registrationAction(ActionEvent actionEvent) {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (!login.equals("") && !password.equals("")) {
            networkService.registrationOnServer(login, password);
        } else {
            typeAlert(AlertMessage.INPUT_ERROR);
        }
    }

    public void connectAction(ActionEvent actionEvent) {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (!login.equals("") && !password.equals("")) {
            networkService.connectWithServer(login, password);
        } else {
            typeAlert(AlertMessage.INPUT_ERROR);
        }
    }

    public void copyFileAction(ActionEvent actionEvent) {
        if (isFileSelected()) {
            if (clientPanelController.filesTable.isFocused()) {
                networkService.copyFile(clientPanelController.getSelectedFilename(CLIENT_PANEL), serverPanelController.getServerPathField(),
                        clientPanelController.getSelectedFileSize(CLIENT_PANEL));
            } else {
                copyFromServer(serverPanelController.getSelectedFilename(SERVER_PANEL), clientPanelController.getCurrentPath(),
                        serverPanelController.getSelectedFileSize(SERVER_PANEL));
            }
        }
    }

    public void refreshClientCatalog() {
        clientPanelController.updateClientPanel();
    }

    private boolean isFileSelected() {
        if (clientPanelController.getSelectedFilename(CLIENT_PANEL) == null && serverPanelController.getSelectedFilename(SERVER_PANEL) == null) {
            typeAlert(AlertMessage.SELECT_ERROR);
            return false;
        }
        return true;
    }

    public void copyFromServer(String filename, String pathClientDirectory, Long fileSize) {
        if (clientPanelController.fileIsExists(filename, pathClientDirectory)) {
            String pathServerDirectory = serverPanelController.getServerPathField();
            networkService.copyFileFromServer(filename, pathClientDirectory, pathServerDirectory, fileSize);
        } else {
            typeAlert(AlertMessage.COPY_ERROR);
        }
    }

    public void startCopyFileToServer() {
        Path path = Paths.get(clientPanelController.getCurrentPath(), clientPanelController.getSelectedFilename(CLIENT_PANEL));
        networkService.startCopyFile(path.toString());
    }

    public void typeAlert(String alertMessage) {
        AlertWindow.getAlertWindowError(alertMessage);
    }

    public PanelController getServerPanelController() {
        return serverPanelController;
    }

    public void shutdown() {
        networkService.closeConnection();
    }

    @Override
    public void callback(MessageDTO serverMessage) {
        dictionaryService.processCommand(serverMessage, this);
    }


}
