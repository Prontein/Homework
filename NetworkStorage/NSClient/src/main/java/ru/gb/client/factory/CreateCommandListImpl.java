package ru.gb.client.factory;

import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.reflections.Reflections;
import ru.gb.client.factory.commandlist.CreateCommandList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CreateCommandListImpl implements CreateCommandList {

    @Override
    public List create() {
        List commandServices = new ArrayList();
        Reflections reflections = new Reflections("ru.gb.client.core.command");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(CommandLine.Command.class);

        for(Class cls: classes) {
            try {
                commandServices.add( cls.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return commandServices;
    }
}
