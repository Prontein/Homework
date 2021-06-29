package ru.gb.client.core.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.gb.client.core.networkservice.networkInterface.ClientNetworkService;
import ru.gb.client.factory.Factory;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;


public class MainController implements Initializable {

    public final static String CLIENT = "client";
    public final static String SERVER = "server";

    private ClientNetworkService networkService;
    private PanelController serverPanelController;
    private PanelController clientPanelController;
    public Button copyBtn;

    @FXML
    AnchorPane controlPanelClient;
    @FXML
    AnchorPane controlPanelServer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadClientInterface();
        networkService = Factory.getNetworkService(this);
        networkService.start();
    }

    private void loadClientInterface () {
        loadDiskCatalog();
        loadFilesCatalogView();
        loadServerCatalogView();
    }

    private void loadDiskCatalog () {
        clientPanelController = (PanelController) controlPanelClient.getProperties().get("ctrl");
        clientPanelController.loadDiskCatalog();
    }

    private void loadFilesCatalogView () {
        clientPanelController = (PanelController) controlPanelClient.getProperties().get("ctrl");
        clientPanelController.ClientFilesView();
    }

    private void loadServerCatalogView() {
        serverPanelController = (PanelController) controlPanelServer.getProperties().get("ctrl");
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
                if (event.getClickCount() == 2) {
                    String selectFilePathFromServer = serverPanelController.serverPathField.getText() + "/" +
                            serverPanelController.getSelectedFilename(SERVER);
                    networkService.showSelectedCatalog(selectFilePathFromServer);
                }
            }
        });
    }

    public void registrationAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/authWindow.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectAction(ActionEvent actionEvent) {
        networkService.connectWithServer();
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

    private boolean isFileSelected () {
        if (clientPanelController.getSelectedFilename(CLIENT) == null && serverPanelController.getSelectedFilename(SERVER) == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Файл не выбран", ButtonType.OK);
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
            Alert alert = new Alert(Alert.AlertType.ERROR, "Файл уже существует", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void startCopy () {
        Path path = Paths.get(clientPanelController.getCurrentPath(), clientPanelController.getSelectedFilename(CLIENT));
        networkService.startCopyFile(path.toString());
    }

    public PanelController getServerPanelController() {
        return serverPanelController;
    }

    public void shutdown() {
        networkService.closeConnection();
    }

    public void transferButton (ActionEvent event) { //TODO: Не сделано
    }

    public void deleteButton (ActionEvent event) {   //TODO: Не сделано
    }
}
