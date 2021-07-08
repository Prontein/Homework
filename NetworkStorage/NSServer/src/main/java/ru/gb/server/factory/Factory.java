package ru.gb.server.factory;

import ru.gb.server.core.comanddictionary.CommandDictionaryServiceImpl;
import ru.gb.server.core.comanddictionary.dictionaryservice.CommandDictionaryService;
import ru.gb.server.core.commandhandler.ServerHandler;
import ru.gb.server.flyway.FlywayMigrationImpl;
import ru.gb.server.flyway.flywaymigration.FlywayMigration;
import ru.gb.server.core.networkservice.serverservice.ServerService;
import ru.gb.server.core.networkservice.NettyServerService;


public class Factory {

    public static ServerService getServerService() {
        return new NettyServerService(getCommandDictionaryService());
    }

    public static ServerHandler getServerHandler() {
        return new ServerHandler();
    }

    public static FlywayMigration getFlyway() {
        return new FlywayMigrationImpl();
    }

    public static CommandDictionaryService getCommandDictionaryService() {
        return new CommandDictionaryServiceImpl();
    }

}
