package ru.gb.server.core.comanddictionary;

import domain.MessageDTO;
import domain.MessageType;
import io.netty.channel.ChannelHandlerContext;
import org.reflections.Reflections;
import ru.gb.server.core.comanddictionary.dictionaryservice.CommandDictionaryService;
import ru.gb.server.core.command.CommandServer;
import ru.gb.server.core.command.commandservice.CommandService;

import java.util.*;

public class CommandDictionaryServiceImpl implements CommandDictionaryService {

    private final Map<MessageType, CommandService> commandDictionary;
    private final List<CommandService> commandServices;

    public CommandDictionaryServiceImpl() {
        this.commandServices = createCommandList();
        this.commandDictionary = Collections.unmodifiableMap(getCommandDictionary());
    }

    private Map<MessageType, CommandService> getCommandDictionary() {
        Map<MessageType, CommandService> commandDictionary = new HashMap<>();

        for (CommandService commandService : commandServices) {
            commandDictionary.put(commandService.getCommand(), commandService);
        }
        return commandDictionary;
    }

    private List<CommandService> createCommandList() {
        List<CommandService> commandServices = new ArrayList<>();

        Reflections reflections = new Reflections("ru.gb.server.core.command");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(CommandServer.class);
        putCommandClassInList(commandServices, classes);

        return commandServices;
    }

    private void putCommandClassInList(List<CommandService> commandServices, Set<Class<?>> classes) {
        for (Class<?> cls : classes) {
            try {
                commandServices.add((CommandService) cls.newInstance());
            } catch (InstantiationException | IllegalAccessException  e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void processCommand(MessageDTO messageDTO, ChannelHandlerContext ctx) {
        MessageType command = messageDTO.getMessageType();

        if (commandDictionary.containsKey(command)) {
            commandDictionary.get(command).processCommand(messageDTO, ctx);
        }
    }
}
