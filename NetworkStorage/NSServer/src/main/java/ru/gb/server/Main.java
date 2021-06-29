package ru.gb.server;

import ru.gb.server.factory.Factory;

public class Main {
    public static void main(String[] args) {
        Factory.getServerService().startServer();
//        Factory.getDBConnection().getDbConnection();
    }
}
