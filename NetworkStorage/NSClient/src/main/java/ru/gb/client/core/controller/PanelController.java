package ru.gb.client.core.controller;

import domain.FileInfo;
import domain.FileType;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import domain.allertsandexeption.AlertMessage;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


public class PanelController {

    public final static String CLIENT_PANEL = "client";
    public final static String SERVER_PANEL = "server";

    public ComboBox<String> diskCatalogBox;
    public TableView<FileInfo> filesTable;
    public TableView<FileInfo> filesTableServer;
    public TextField clientPathField;
    public TextField serverPathField;
    public TableColumn<FileInfo, String> filenameColumn;
    public TableColumn<FileInfo, Long> fileSizeColumn;
    public TableColumn<FileInfo, Long> fileSizeColumnServer;
    public TableColumn<FileInfo, String> filenameColumnServer;
    public Button serverCatalogUpBtn;

    public void loadDiskCatalog() {
        diskCatalogBox.getItems().clear();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            diskCatalogBox.getItems().add(p.toString());
        }
        diskCatalogBox.getSelectionModel().select(0);
    }

    public void ClientFilesView() {
        updateListClientCatalog(Paths.get("."));
        setCellFileNameParam(filenameColumn);
        setCellFileSizeParam(fileSizeColumn);
        setCellFileTypeParam(fileSizeColumn);
    }

    public void serverFilesView() {
        setCellFileNameParam(filenameColumnServer);
        setCellFileSizeParam(fileSizeColumnServer);
        setCellFileTypeParam(fileSizeColumnServer);
        filesTableServer.getSortOrder();
    }

    private void setCellFileNameParam(TableColumn<FileInfo, String> fileNameColumn) {
        fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));
    }

    private void setCellFileSizeParam(TableColumn<FileInfo, Long> fileSizeColumn) {
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
    }

    private void setCellFileTypeParam(TableColumn<FileInfo, Long> fileTypeColumn) {
        fileTypeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {
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
    }

    public void updateListServerCatalog(List<FileInfo> serverList) {
        filesTableServer.getItems().clear();
        filesTableServer.getItems().addAll(serverList);
        serverFilesView();
    }

    private void updateListClientCatalog(Path path) {
        try {
            clientPathField.setText(path.normalize().toAbsolutePath().toString());
            filesTable.getItems().clear();
            filesTable.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            filesTable.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, AlertMessage.UPDATE_ERROR, ButtonType.OK);
            alert.showAndWait();
        }
    }

    public String getSelectedFilename(String selectedPanel) {
        switch (selectedPanel) {
            case CLIENT_PANEL -> {
                return getFilename(filesTable);
            }
            case SERVER_PANEL -> {
                return getFilename(filesTableServer);
            }
        }
        return null;
    }

    private String getFilename(TableView<FileInfo> filesTable) {
        if (!filesTable.isFocused() || filesTable.getSelectionModel().isEmpty()) {
            return null;
        }
        return filesTable.getSelectionModel().getSelectedItem().getFilename();
    }

    public long getSelectedFileSize(String panel) {
        switch (panel) {
            case (CLIENT_PANEL) -> {
                return filesTable.getSelectionModel().getSelectedItem().getSize();
            }
            case (SERVER_PANEL) -> {
                return filesTableServer.getSelectionModel().getSelectedItem().getSize();
            }
        }
        return -1L;
    }

    public boolean fileIsExists(String filename, String pathClientDirectory) {
        try {
            List<FileInfo> clientCatalog = Files.list(Paths.get(pathClientDirectory)).map(FileInfo::new).collect(Collectors.toList());
            for (FileInfo file : clientCatalog) {
                if (file.getFilename().equals(filename)) return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public String getCurrentPath() {
        return clientPathField.getText();
    }

    public void setServerDirectoryPath(String pathToServerCatalog) {
        serverPathField.setText(pathToServerCatalog);
    }

    public String getServerPathField() {
        return serverPathField.getText();
    }

    public void selectDiskAction(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
        updateListClientCatalog(Paths.get(element.getSelectionModel().getSelectedItem()));
    }

    public void selectedFileInDirectory(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2 && !filesTable.getSelectionModel().getSelectedItem().getType().equals(FileType.FILE)) {
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

    public void updateClientPanel() {
        updateListClientCatalog(Paths.get(getCurrentPath()));
    }
}
