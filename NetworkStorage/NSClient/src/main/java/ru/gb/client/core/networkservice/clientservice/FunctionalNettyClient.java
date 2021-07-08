package ru.gb.client.core.networkservice.clientservice;

public interface FunctionalNettyClient extends ClientNetworkService{
    void copyFile(String filename, String pathTo, Long fileSize);

    void startCopyFile(String path);

    void upServerCatalog(String pathServerCatalog);

    void showSelectedCatalog(String path, String filename);

    void copyFileFromServer(String selectedFilename, String clientPathField, String serverPathField, Long fileSize);
}
