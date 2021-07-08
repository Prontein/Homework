package ru.gb.client.factory;

import ru.gb.client.core.commandhandler.commanddictionary.dictionaryservice.CommandDictionaryService;
import ru.gb.client.core.commandhandler.commanddictionary.CommandDictionaryServiceImpl;
import ru.gb.client.core.networkservice.NettyClient;
import ru.gb.client.core.controller.callback.Callback;
import ru.gb.client.core.networkservice.clientservice.FunctionalNettyClient;


public class Factory {
    public static FunctionalNettyClient getNetworkService(Callback callback) {
        return new NettyClient(callback);
    }

    public static CommandDictionaryService getCommandDictionaryService() {
        return new CommandDictionaryServiceImpl();
    }
}
