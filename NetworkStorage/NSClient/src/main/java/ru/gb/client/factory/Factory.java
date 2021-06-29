package ru.gb.client.factory;

import ru.gb.client.core.controller.MainController;
import ru.gb.client.core.networkservice.NettyClient;
import ru.gb.client.core.networkservice.networkInterface.ClientNetworkService;


public class Factory {
    public static ClientNetworkService getNetworkService(MainController mainController) {
        return new NettyClient(mainController);
    }
}
