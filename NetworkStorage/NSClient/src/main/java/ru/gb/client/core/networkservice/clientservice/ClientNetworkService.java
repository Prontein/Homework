package ru.gb.client.core.networkservice.clientservice;

public interface ClientNetworkService {
    void start();

    void connectWithServer(String login, String password);

    void registrationOnServer(String login, String password);

    void closeConnection();
}
