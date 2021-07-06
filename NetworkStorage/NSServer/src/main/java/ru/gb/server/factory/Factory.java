package ru.gb.server.factory;

import ru.gb.server.core.comanddictionary.CommandDictionaryServiceImpl;
import ru.gb.server.core.comanddictionary.dictionaryinterface.CommandDictionaryService;
import ru.gb.server.core.command.commandinterface.CommandService;
import ru.gb.server.core.commandhandler.ServerHandler;
import ru.gb.server.flyway.FlywayMigration;
import ru.gb.server.flyway.flywayinterface.FlywayStarter;
import ru.gb.server.core.networkservice.networkInterface.ServerService;
import ru.gb.server.core.networkservice.NettyServerService;
import ru.gb.server.database.DatabaseHandler;
import ru.gb.server.util.PropertyUtils;

import java.util.List;


public class Factory {

    public static ServerService getServerService() {
        return new NettyServerService(getCommandDictionaryService());
    }

    public static DatabaseHandler getDBConnection() {
        return new DatabaseHandler();
    }

    public static ServerHandler getServerHandler() {
        return new ServerHandler();
    }

    public static FlywayStarter getFlyway() {
        return new FlywayMigration();
    }

    public static CommandDictionaryService getCommandDictionaryService() {
        return new CommandDictionaryServiceImpl(getCommandServices());
    }

    public static List<CommandService> getCommandServices() {
        return new CreateCommandListImpl(getServerHandler()).create();
    }

    public static PropertyUtils getProperties () {
        return new PropertyUtils();
    }
}
