package ru.gb.server.factory;

import ru.gb.server.core.commandhandler.ServerHandler;
import ru.gb.server.core.networkservice.networkInterface.ServerService;
import ru.gb.server.core.networkservice.NettyServerService;
import ru.gb.server.database.DatabaseHandler;


public class Factory {

    public static ServerService getServerService() {
        return new NettyServerService();
    }

    public static DatabaseHandler getDBConnection() {
        return new DatabaseHandler();
    }

    public static ServerHandler getServerHandler() {
        return new ServerHandler();
    }
}
