package ru.gb.client.core.controller;

import domain.FileInfoClient;
import domain.FileInfoServer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


public class PanelController {

    public final static String CLIENT = "client";
    public final static String SERVER = "server";

    public ComboBox<String> diskCatalogBox;
    public TableView<FileInfoClient> filesTable;
    public TableView<FileInfoServer> filesTableServer;
    public TextField clientPathField;
    public TextField serverPathField;
    public TableColumn<FileInfoClient,String> filenameColumn;
    public TableColumn<FileInfoClient,Long> fileSizeColumn;
    public TableColumn<FileInfoServer,Long> fileSizeColumnServer;
    public TableColumn<FileInfoServer,String> filenameColumnServer;
    public TableColumn<FileInfoServer,String> fileTypeColumnServer;
    public Button serverCatalogUpBtn;

    public void loadDiskCatalog () {
        diskCatalogBox.getItems().clear();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            diskCatalogBox.getItems().add(p.toString());
        }
        diskCatalogBox.getSelectionModel().select(0);

    }

    public void ClientFilesView() {
        updateListClientCatalog(Paths.get("."));
        filenameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfoClient, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%,d bytes", item);
                        if (item == -1L) {
                            text = "[DIR]";
                        }
                        setText(text);
                    }
                }

            };
        });
        TableColumn<FileInfoClient,String> fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        filesTable.getColumns().addAll(fileTypeColumn);
        filesTable.getSortOrder().add(fileTypeColumn);
    }

    public void serverFilesView () {
        filenameColumnServer.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileName()));
        fileSizeColumnServer.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getFileSize()));
        fileTypeColumnServer.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileType()));
        for (FileInfoServer item : filesTableServer.getItems()) {
            if (item.getFileType().equals("[DIR]")) {
                item.setFileSize(null);
            }
        }
        filesTableServer.getSortOrder();
    }

    public void updateListServerCatalog(List<FileInfoServer> serverList) {
        filesTableServer.getItems().clear();
        filesTableServer.getItems().addAll(serverList);
        serverFilesView();
    }

    private void updateListClientCatalog(Path path) {
        try {
             clientPathField.setText(path.normalize().toAbsolutePath().toString());
             filesTable.getItems().clear();
             filesTable.getItems().addAll(Files.list(path).map(FileInfoClient::new).collect(Collectors.toList()));
             filesTable.sort();
        } catch (IOException e) {
            Alert alert = new Alert (Alert.AlertType.WARNING, "Возникла ошибка", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public String getSelectedFilename(String selectedPanel) {
        switch (selectedPanel) {
            case CLIENT -> {
                if (!filesTable.isFocused() || filesTable.getSelectionModel().isEmpty()) {
                    return null;
                }
                return filesTable.getSelectionModel().getSelectedItem().getFilename();
            }
            case SERVER -> {
                if (!filesTableServer.isFocused() || filesTableServer.getSelectionModel().isEmpty()) {
                    return null;
                }
                return filesTableServer.getSelectionModel().getSelectedItem().getFileName();
            }
        }
        return null;
    }

    public long getSelectedFileSize (String panel) {
        switch (panel) {
            case (CLIENT) -> {
                return filesTable.getSelectionModel().getSelectedItem().getSize();
            }
            case (SERVER) -> {
                return filesTableServer.getSelectionModel().getSelectedItem().getFileSize();
            }
        }
        return -1L;
    }

    public boolean fileIsExists (String filename, String pathClientDirectory) {
        try {
            List<FileInfoClient> clientCatalog = Files.list(Paths.get(pathClientDirectory)).map(FileInfoClient::new).collect(Collectors.toList());
            for (FileInfoClient file : clientCatalog) {
                if (file.getFilename().equals(filename)) return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public String getCurrentPath () {
        return clientPathField.getText();
    }

    public void setServerDirectoryPath (String pathToServerCatalog) {
        serverPathField.setText(pathToServerCatalog);
    }

    public String getServerPathField() {
        return serverPathField.getText();
    }

    public void selectDiskAction(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>)actionEvent.getSource();
        updateListClientCatalog(Paths.get(element.getSelectionModel().getSelectedItem()));
    }

    public void selectedFileInDirectory(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            Path path = Paths.get(clientPathField.getText()).resolve(filesTable.getSelectionModel().getSelectedItem().getFilename());
            updateListClientCatalog(path);
        }
    }

    public void clientCatalogUpAction(ActionEvent actionEvent) {
        Path upperPath = Paths.get(clientPathField.getText()).getParent();
        if (upperPath != null) {
            updateListClientCatalog(upperPath);
        }
    }
}
