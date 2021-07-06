package ru.gb.server.factory;

import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.reflections.Reflections;
import ru.gb.server.core.command.commandinterface.CommandService;
import ru.gb.server.core.commandhandler.ServerHandler;
import ru.gb.server.factory.commandlist.CreateCommandList;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CreateCommandListImpl implements CreateCommandList {

    private final ServerHandler serverHandler;
    public CreateCommandListImpl(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    @Override
    public List create() {
        List commandServices = new ArrayList();
        Reflections reflections = new Reflections("ru.gb.server.core.command");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(CommandLine.Command.class);

        for(Class cls: classes) {
            try {
                commandServices.add( cls.getConstructor(ServerHandler.class).newInstance(serverHandler));
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return commandServices;
    }
}
