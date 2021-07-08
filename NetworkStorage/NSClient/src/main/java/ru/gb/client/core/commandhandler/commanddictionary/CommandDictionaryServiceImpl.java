package ru.gb.client.core.commandhandler.commanddictionary;

import domain.MessageDTO;
import domain.MessageType;
import org.reflections.Reflections;
import ru.gb.client.core.command.uicommand.CommandClient;
import ru.gb.client.core.commandhandler.commanddictionary.dictionaryservice.CommandDictionaryService;
import ru.gb.client.core.command.commandservice.CommandService;
import ru.gb.client.core.controller.MainController;

import java.util.*;

public class CommandDictionaryServiceImpl implements CommandDictionaryService {
    private final List<CommandService> commandServices;
    private final Map<MessageType, CommandService> commandDictionary;

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

        Reflections reflections = new Reflections("ru.gb.client.core.command");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(CommandClient.class);
        putCommandClassInList(commandServices, classes);

        return commandServices;
    }

    private void putCommandClassInList(List<CommandService> commandServices, Set<Class<?>> classes) {
        for (Class<?> cls : classes) {
            try {
                commandServices.add((CommandService) cls.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void processCommand(MessageDTO messageDTO, MainController controller) {
        MessageType command = messageDTO.getMessageType();

        if (commandDictionary.containsKey(command)) {
            commandDictionary.get(command).processCommand(messageDTO, controller);
        }
    }
}
