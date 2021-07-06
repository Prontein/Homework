package ru.gb.client.factory;

import ru.gb.client.core.commandhandler.CommandDictionary.dictionaryinterface.CommandDictionaryService;
import ru.gb.client.core.commandhandler.CommandDictionary.CommandDictionaryServiceImpl;
import ru.gb.client.core.command.commandinterface.CommandService;
import ru.gb.client.core.networkservice.NettyClient;
import ru.gb.client.core.controller.callback.Callback;
import ru.gb.client.core.networkservice.networkInterface.ClientNetworkService;
import ru.gb.client.core.util.PropertyUtils;
import java.util.List;


public class Factory {

    public static ClientNetworkService getNetworkService(Callback callback) {
        return new NettyClient(callback);
    }

    public static CommandDictionaryService getCommandDictionaryService() {
        return new CommandDictionaryServiceImpl(getCommandServices());
    }

    public static List<CommandService> getCommandServices() {
        return new CreateCommandListImpl().create();
    }

    public static PropertyUtils getProperties () {
        return new PropertyUtils();
    }


}
