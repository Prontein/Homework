package ru.gb.client.core.networkservice.networkInterface;

public interface ClientNetworkService {

    void start();
    void copyFile(String filename, String pathTo, Long fileSize);
    void startCopyFile(String path);
    void connectWithServer();
    void upServerCatalog(String pathServerCatalog);
    void showSelectedCatalog(String path);
    void transferFile();
    void deleteFile();
    void readCommandResult();
    void closeConnection();
    void copyFileFromServer(String selectedFilename, String clientPathField, String serverPathField, Long fileSize);
}
