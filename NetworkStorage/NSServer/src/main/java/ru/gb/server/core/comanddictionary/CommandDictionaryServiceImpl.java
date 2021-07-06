package ru.gb.server.core.comanddictionary;

import domain.MessageDTO;
import domain.MessageType;
import io.netty.channel.ChannelHandlerContext;
import ru.gb.server.core.comanddictionary.dictionaryinterface.CommandDictionaryService;
import ru.gb.server.core.command.commandinterface.CommandService;
import ru.gb.server.core.commandhandler.CommandHandler;
import ru.gb.server.factory.Factory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandDictionaryServiceImpl implements CommandDictionaryService {

    private final Map<MessageType, CommandService> commandDictionary;
    private final List<CommandService> commandServices;

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
    public void processCommand(MessageDTO messageDTO, ChannelHandlerContext ctx) {
        MessageType command = messageDTO.getMessageType();

        if (commandDictionary.containsKey(command)) {
            commandDictionary.get(command).processCommand(messageDTO, ctx);
        }
    }
}
