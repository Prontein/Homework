package ru.gb.client.core.commandhandler.CommandDictionary;

import domain.MessageDTO;
import domain.MessageType;
import ru.gb.client.core.commandhandler.CommandDictionary.dictionaryinterface.CommandDictionaryService;
import ru.gb.client.core.command.commandinterface.CommandService;
import ru.gb.client.core.controller.MainController;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandDictionaryServiceImpl implements CommandDictionaryService {
    private final List<CommandService> commandServices;
    private final Map<MessageType, CommandService> commandDictionary;

    public CommandDictionaryServiceImpl(List<CommandService> commandServices) {
        this.commandServices = commandServices;
        this.commandDictionary = Collections.unmodifiableMap(getCommandDictionary());
    }

    private Map<MessageType, CommandService> getCommandDictionary() {
        Map<MessageType, CommandService> commandDictionary = new HashMap<>();

        for (CommandService commandService : commandServices) {
            commandDictionary.put(commandService.getCommand(), commandService);
        }

        return commandDictionary;
    }

    @Override
    public void processCommand(MessageDTO messageDTO, MainController controller) {
        MessageType command = messageDTO.getMessageType();

        if (commandDictionary.containsKey(command)) {
            commandDictionary.get(command).processCommand(messageDTO, controller);
        }
    }
}
